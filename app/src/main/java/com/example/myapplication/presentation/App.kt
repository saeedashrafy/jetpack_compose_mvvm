package com.example.myapplication.presentation

import android.app.Application
import com.example.myapplication.BuildConfig
import com.example.myapplication.core.coreModule
import com.example.myapplication.data.dataModule
import com.example.myapplication.domain.domainModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import kotlin.time.ExperimentalTime

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalTime
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)

            modules(
                presentationModule,
                coreModule,
                dataModule,
                domainModule,

                )
        }
    }
}