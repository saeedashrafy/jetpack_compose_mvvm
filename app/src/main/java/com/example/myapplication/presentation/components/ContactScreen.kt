package com.example.myapplication.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.core.unit
import com.example.myapplication.presentation.theme.DarkBlue
import com.example.myapplication.presentation.viewmodel.*
import com.example.myapplication.presentation.viewmodel.ContactSingleEvent
import com.example.myapplication.presentation.viewmodel.ContactViewIntent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.getViewModel


@Composable
fun ContactScreen() {
    val (state, singleEvent, processIntent) = getViewModel<ContactVM>()


    DisposableEffect("Initial") {
        // dispatch initial intent

        processIntent(ContactViewIntent.NameChanged())
        processIntent(ContactViewIntent.EmailChanged())
        processIntent(ContactViewIntent.PhoneChanged())
        processIntent(ContactViewIntent.SubjectChanged())
        processIntent(ContactViewIntent.BodyChanged())

        onDispose { }
    }



    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState



    LaunchedEffect("SingleEvent") {
        // observe single event
        singleEvent
            .onEach { event ->
                when (event) {
                    is ContactSingleEvent.SubmitContactSuccess -> snackbarHostState.showSnackbar(event.message)
                    is ContactSingleEvent.SubmitContactFailure -> snackbarHostState.showSnackbar("خطا در ارسال")
                }.unit
            }
            .collect()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.White,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            val scrollState = rememberScrollState()

            Column(modifier = Modifier
                .verticalScroll(scrollState)) {
                var nameValue by remember { mutableStateOf(TextFieldValue("")) }
                var phoneValue by remember { mutableStateOf(TextFieldValue("")) }
                var emailValue by remember { mutableStateOf(TextFieldValue("")) }
                var subjectValue by remember { mutableStateOf(TextFieldValue("")) }
                var bodyValue by remember { mutableStateOf(TextFieldValue("")) }


                Image(
                    painterResource(R.drawable.ic_contactus),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(150.dp),
                    alignment = Alignment.TopCenter


                )

                Text(
                    text="ثبت درخواست",
                    style = MaterialTheme.typography.h1,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                )

                OutlinedTextField(
                    value = nameValue,
                    onValueChange = {
                        nameValue = it
                        processIntent(ContactViewIntent.NameChanged(it.text))
                    },
                    label = { Text("نام (اجباری)",) },
                    placeholder = { Text(text = "نام خود را وارد نمایید") },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    )
                OutlinedTextField(
                    value = phoneValue,
                    onValueChange = {
                        phoneValue = it
                        processIntent(ContactViewIntent.PhoneChanged(it.text))
                    },
                    label = { Text("شماره موبایل") },
                    placeholder = { Text(text = "شماره موبایل خود را وارد نمایید") },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true

                )
                OutlinedTextField(
                    value = bodyValue,
                    onValueChange = {
                        bodyValue = it
                        processIntent(ContactViewIntent.BodyChanged(it.text))
                    },
                    label = { Text("متن درخواست (اجباری)") },
                    placeholder = { Text(text = "متن درخواست خود را وارد نمایید") },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                        .fillMaxWidth(),
                    )

                Button(
                    onClick = { processIntent(ContactViewIntent.Submit) },
                    modifier = Modifier
                        .padding(top = 20.dp, start = 32.dp, end = 32.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                    enabled =state.errors.isEmpty() && !state.isLoading
                ) {
                    if(state.isLoading){
                        CircularProgressIndicator()
                    }else{
                        Text("ارسال درخواست")
                    }

                }


            }
        }
    }

}