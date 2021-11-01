package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.IntentDispatcher
import com.example.myapplication.core.flatMapFirst
import com.example.myapplication.core.withLatestFrom
import com.example.myapplication.domain.entity.Contact
import com.example.myapplication.domain.entity.Either
import com.example.myapplication.domain.usecase.SubmitContactUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class ContactVM(
    private val submitContactUseCase: SubmitContactUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _eventChannel = Channel<ContactSingleEvent>(Channel.BUFFERED)
    private val _intentFlow = MutableSharedFlow<ContactViewIntent>(extraBufferCapacity = 64)

    private val detailsViewState: StateFlow<ContactViewState>

    @Composable
    internal operator fun component1(): ContactViewState =
        detailsViewState.collectAsState().value
    @Composable
    internal operator fun component2(): Flow<ContactSingleEvent> = _eventChannel.receiveAsFlow()
    @Composable
    internal operator fun component3(): IntentDispatcher<ContactViewIntent> =
        { _intentFlow.tryEmit(it) }

    init {
        val initialVS = ContactViewState.initial(
            name = savedStateHandle.get<String?>("name"),
            email = savedStateHandle.get<String?>("email"),
            phone = savedStateHandle.get<String?>("phone"),
            subject = savedStateHandle.get<String?>("subject"),
            body = savedStateHandle.get<String?>("body"),
        )
        detailsViewState = _intentFlow
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
            .toContactPartialChangeFlow()
            .onEach { Log.d("TAG", it.toString()) }
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch { Log.d("###", "[CONTACT_VM] Throwable: $it") }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )
    }


    private fun Flow<ContactPartialChange>.sendSingleEvent(): Flow<ContactPartialChange> {
        return onEach { change ->
            val event = when (change) {
                is ContactPartialChange.ErrorsChanged -> return@onEach
                ContactPartialChange.SubmitContact.Loading -> return@onEach
                is ContactPartialChange.SubmitContact.Success -> ContactSingleEvent.SubmitContactSuccess(change.contactMessage.description)
                is ContactPartialChange.SubmitContact.FailureData -> ContactSingleEvent.SubmitContactFailure(change.failureMessage)

                is ContactPartialChange.FormValueChange.EmailChanged -> return@onEach
                is ContactPartialChange.FormValueChange.PhoneChanged -> return@onEach
                is ContactPartialChange.FormValueChange.NameChanged -> return@onEach
                is ContactPartialChange.FormValueChange.BodyChanged -> return@onEach
                is ContactPartialChange.FormValueChange.SubjectChanged -> return@onEach

                is ContactPartialChange.FirstChange.EmailChangedFirstTime -> return@onEach
                is ContactPartialChange.FirstChange.PhoneChangedFirstTime -> return@onEach
                is ContactPartialChange.FirstChange.NameChangedFirstTime -> return@onEach
                is ContactPartialChange.FirstChange.BodyChangedFirstTime -> return@onEach
                is ContactPartialChange.FirstChange.SubjectChangedFirstTime -> return@onEach


            }
            _eventChannel.send(event)
        }
    }


    @OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun Flow<ContactViewIntent>.toContactPartialChangeFlow(): Flow<ContactPartialChange> {
        val nameErrors = filterIsInstance<ContactViewIntent.NameChanged>()
            .map { it.name }
            .map { validateFirstName(it) to it }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )

        val emailErrors = filterIsInstance<ContactViewIntent.EmailChanged>()
            .map { it.email }
            .map { validateEmail(it) to it }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )
        val phoneErrors = filterIsInstance<ContactViewIntent.PhoneChanged>()
            .map { it.phone }
            .map { validatePhone(it) to it }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )
        val subjectErrors = filterIsInstance<ContactViewIntent.SubjectChanged>()
            .map { it.subject }
            .map { validateSubject(it) to it }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )
        val bodyErrors = filterIsInstance<ContactViewIntent.BodyChanged>()
            .map { it.body }
            .map { validateBody(it) to it }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )
        val contactFormFlow =
            combine(
                nameErrors,
                emailErrors,
                phoneErrors,
                subjectErrors,
                bodyErrors
            ) { name, email, phone,
                subject, body ->
                val errors = email.first + name.first + phone.first + subject.first + body.first
                if (errors.isEmpty()) Either.Right(
                    Contact(
                        name = name.second!!,
                        email = email.second!!,
                        phone = phone.second!!,
                        subject = subject.second!!,
                        body = body.second!!
                    )
                ) else Either.Left(errors)
            }
                .shareIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed()
                )

        val submitContactChanges = filterIsInstance<ContactViewIntent.Submit>()
            .withLatestFrom(contactFormFlow) { _, userForm -> userForm }
            .onEach { Log.d("TAG", it.toString()) }
            .mapNotNull { it.rightOrNull() }
            .onEach { Log.d("TA", it.toString()) }
            .flatMapFirst { contact ->
                submitContactUseCase(contact)
                    .map {

                        it.fold({ failure ->
                                ContactPartialChange.SubmitContact.FailureData(
                                    failure.message
                                )
                        }, { message ->
                            Log.d("TAG",message.toString())
                            @Suppress("USELESS_CAST")
                            ContactPartialChange.SubmitContact.Success(message)
                        }).let {
                            return@map it as ContactPartialChange
                        }

                    }
                    .onStart { emit(ContactPartialChange.SubmitContact.Loading)  }
                    .catch { emit(ContactPartialChange.SubmitContact.FailureData(it?.message)) }
            }

        val firstChanges = merge(
            filterIsInstance<ContactViewIntent.EmailChangedFirstTime>()
                .map { ContactPartialChange.FirstChange.EmailChangedFirstTime },
            filterIsInstance<ContactViewIntent.NameChangedFirstTime>()
                .map { ContactPartialChange.FirstChange.NameChangedFirstTime },
            filterIsInstance<ContactViewIntent.PhoneChangedFirstTime>()
                .map { ContactPartialChange.FirstChange.PhoneChangedFirstTime },
            filterIsInstance<ContactViewIntent.SubjectChangedFirstTime>()
                .map { ContactPartialChange.FirstChange.SubjectChangedFirstTime },
            filterIsInstance<ContactViewIntent.BodyChangedFirstTime>()
                .map { ContactPartialChange.FirstChange.BodyChangedFirstTime }

        )

        val formValuesChanges = merge(
            nameErrors
                .map { it.second }
                .onEach { savedStateHandle.set("name", it) }
                .map { ContactPartialChange.FormValueChange.NameChanged(it) },
            phoneErrors
                .map { it.second }
                .onEach { savedStateHandle.set("phone", it) }
                .map { ContactPartialChange.FormValueChange.PhoneChanged(it) },
            emailErrors
                .map { it.second }
                .onEach { savedStateHandle.set("email", it) }
                .map { ContactPartialChange.FormValueChange.EmailChanged(it) },
            subjectErrors
                .map { it.second }
                .onEach { savedStateHandle.set("subject", it) }
                .map { ContactPartialChange.FormValueChange.SubjectChanged(it) },
            bodyErrors
                .map { it.second }
                .onEach { savedStateHandle.set("body", it) }
                .map { ContactPartialChange.FormValueChange.BodyChanged(it) },
        )

        return merge(
            contactFormFlow
                .map {
                    ContactPartialChange.ErrorsChanged(
                        it.leftOrNull()
                            ?: emptySet()
                    )
                },
            submitContactChanges,
            firstChanges,
            formValuesChanges,
        )


    }

    private companion object {
        const val MIN_LENGTH_NAME =1
        const val MIN_LENGTH_LAST_NAME = 3

        fun validateFirstName(name: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

               if (name == null || name.length < MIN_LENGTH_NAME) {
                   errors += ValidationError.TOO_SHORT_NAME
               }
            return errors
        }

        fun validateEmail(email: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            /* if (email == null || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                 errors += ValidationError.INVALID_EMAIL_ADDRESS
             }*/
            // more validation here
            return errors
        }

        fun validatePhone(phone: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()
            // more validation here
            return errors
        }

        fun validateSubject(subject: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            /*   if (firstName == null || firstName.length < MIN_LENGTH_NAME) {
                   errors += ValidationError.TOO_SHORT_NAME
               }*/
            // more validation here
            return errors
        }

        fun validateBody(body: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

               if (body == null || body.length < MIN_LENGTH_NAME) {
                   errors += ValidationError.TOO_SHORT_NAME
               }
            // more validation here
            return errors
        }


    }

}