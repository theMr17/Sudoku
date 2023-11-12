package com.mr_17.sudoku.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object ProductionDispatcherProvider: DispatcherProvider {
    override fun provideUiContext(): CoroutineContext {
        return Dispatchers.Main
    }

    override fun provideIoContext(): CoroutineContext {
        return Dispatchers.IO
    }
}
