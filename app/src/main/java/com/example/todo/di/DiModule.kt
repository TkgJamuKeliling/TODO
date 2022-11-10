package com.example.todo.di

import com.example.todo.TodoApp
import com.example.todo.dialog.GlobalDialog
import com.example.todo.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/** Created By zen
 * 09/11/22
 * 21.31
 */
object DiModule {
    val appModule = module {
        single { androidApplication() as TodoApp }
        factory { GlobalDialog() }
        viewModel { MainViewModel() }
    }
}