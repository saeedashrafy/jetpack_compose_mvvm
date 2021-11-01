package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.entity.ContactMessage
import com.example.myapplication.domain.entity.Failure


internal enum class ValidationError {
    INVALID_EMAIL_ADDRESS,
    TOO_SHORT_NAME,
    TOO_SHORT_SUBJECT,
}

internal sealed class ContactViewIntent {
    object Submit : ContactViewIntent()
    object Retry : ContactViewIntent()
    data class NameChanged(val name: String?="") : ContactViewIntent()
    data class EmailChanged(val email: String?="") : ContactViewIntent()
    data class PhoneChanged(val phone: String?="") : ContactViewIntent()
    data class SubjectChanged(val subject: String?="") : ContactViewIntent()
    data class BodyChanged(val body: String? ="") : ContactViewIntent()

    object NameChangedFirstTime : ContactViewIntent()
    object EmailChangedFirstTime : ContactViewIntent()
    object PhoneChangedFirstTime : ContactViewIntent()
    object SubjectChangedFirstTime : ContactViewIntent()
    object BodyChangedFirstTime : ContactViewIntent()
}

internal data class ContactViewState(
    val isLoading: Boolean,
    val errors: Set<ValidationError>,
    val nameChanged: Boolean,
    val emailChanged: Boolean,
    val phoneChanged: Boolean,
    val subjectChanged: Boolean,
    val bodyChanged: Boolean,

    val name: String?,
    val email: String?,
    val phone: String?,
    val subject: String?,
    val body: String?,


    ) {
    companion object {
        fun initial(
            name: String?,
            email: String?,
            phone: String?,
            subject: String?,
            body: String?
        ) = ContactViewState(
            isLoading = false,
            errors = emptySet(),
            nameChanged = false,
            emailChanged = false,
            phoneChanged = false,
            subjectChanged = false,
            bodyChanged = false,
            name = name,
            email = email,
            phone = phone,
            subject = subject,
            body = body
        )
    }
}


internal sealed class ContactPartialChange {
    abstract fun reduce(vs: ContactViewState): ContactViewState

    data class ErrorsChanged(val errors: Set<ValidationError>) : ContactPartialChange() {
        override fun reduce(viewState: ContactViewState) = viewState.copy(errors = errors)
    }
    sealed class SubmitContact : ContactPartialChange() {

        object Loading : SubmitContact()
        data class Success(val contactMessage: ContactMessage) : SubmitContact()
        data class FailureData(val failureMessage: String?) : SubmitContact()

        override fun reduce(vs: ContactViewState): ContactViewState {
            return when (this) {
                Loading -> vs.copy(
                    isLoading = true,
                )
                is Success -> vs.copy(
                    isLoading = false,

                )
                is FailureData -> vs.copy(
                    isLoading = false,
                )
            }
        }
    }

    sealed class FirstChange : ContactPartialChange() {
        object EmailChangedFirstTime : FirstChange()
        object NameChangedFirstTime : FirstChange()
        object PhoneChangedFirstTime : FirstChange()
        object SubjectChangedFirstTime : FirstChange()
        object BodyChangedFirstTime : FirstChange()

        override fun reduce(viewState: ContactViewState): ContactViewState {
            return when (this) {
                EmailChangedFirstTime -> viewState.copy(emailChanged = true)
                NameChangedFirstTime -> viewState.copy(nameChanged = true)
                PhoneChangedFirstTime -> viewState.copy(phoneChanged = true)
                SubjectChangedFirstTime -> viewState.copy(subjectChanged = true)
                BodyChangedFirstTime -> viewState.copy(bodyChanged = true)
            }
        }
    }

    sealed class FormValueChange : ContactPartialChange() {
        override fun reduce(viewState: ContactViewState): ContactViewState {
            return when (this) {
                is NameChanged -> viewState.copy(name = name)
                is EmailChanged -> viewState.copy(email = email)
                is PhoneChanged -> viewState.copy(phone = phone)
                is SubjectChanged -> viewState.copy(subject = subject)
                is BodyChanged -> viewState.copy(body = body)
            }
        }

        data class EmailChanged(val email: String?) : FormValueChange()
        data class NameChanged(val name: String?) : FormValueChange()
        data class PhoneChanged(val phone: String?) : FormValueChange()
        data class SubjectChanged(val subject: String?) : FormValueChange()
        data class BodyChanged(val body: String?) : FormValueChange()
    }
}





internal sealed class ContactSingleEvent {
    data class SubmitContactSuccess(val message:String) : ContactSingleEvent()
    data class SubmitContactFailure(val failureMessage: String?) : ContactSingleEvent()

}
