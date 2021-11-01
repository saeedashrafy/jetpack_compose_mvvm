package com.example.myapplication.presentation.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.example.myapplication.presentation.viewmodel.NewsDetailsVM
import com.example.myapplication.presentation.viewmodel.NewsDetailsViewIntent
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewsDetailsScreen(id: Int, modifier: Modifier = Modifier) {


    val (state, singleEvent, processIntent) = getViewModel<NewsDetailsVM>()
    DisposableEffect("Initial") {
        // dispatch initial intent
        processIntent(NewsDetailsViewIntent.Initial(id))
        onDispose { }
    }

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier
    ) {


        if(state.isLoading){
            LinearProgressIndicator(modifier.fillMaxWidth())
        }
        state.details?.let {

            WebPageScreen(getHtml(it.title ,it.content))
        }


    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPageScreen(urlToRender: String) {
    AndroidView(factory = {
        WebView(it).apply {
            settings.defaultTextEncodingName = "utf-8"
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadDataWithBaseURL("", urlToRender, "text/html", "UTF-8", null)

        }
    }, update = {
        it.loadDataWithBaseURL("", urlToRender, "text/html", "UTF-8", null)
    })
}


@Composable
fun Html(text: String) {

    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
    })
}



fun getHtml(title:String,content: String): String {
    val myFont = "file:///android_res/font/vazir_medium.ttf"
    return """

  <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

  <html>
    <head>
  
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
      <style type="text/css">
@font-face {
          font-family: "my_font";
          src: url("$myFont");
        }

        body { font-family: "my_font"; backGround:white; text-align:right; padding:7px ;  style=\"text-align:justify;\">"}
         
     
 
      </style>
    </head>

    <body>
     <div style="margin: auto; text-align:right;    font-size: 20px;  direction: rtl;">
     ${title}
     </div>
    
    <div style="margin: auto; text-align:right;  direction: rtl;">
     ${content}
     </div>
     </body>

  </html>

"""

}