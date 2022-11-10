package com.example.todo

import android.app.Application
import com.example.todo.di.DiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/** Created By zen
 * 09/11/22
 * 21.30
 */
class TodoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TodoApp)
            modules(DiModule.appModule)
        }
    }
}