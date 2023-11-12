package com.mr_17.sudoku.common

import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {
    fun provideUiContext(): CoroutineContext
    fun provideIoContext(): CoroutineContext
}
