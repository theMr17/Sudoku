package com.mr_17.sudoku.ui.activegame.buildlogic

import android.content.Context
import com.mr_17.sudoku.common.ProductionDispatcherProvider
import com.mr_17.sudoku.persistence.GameRepositoryImpl
import com.mr_17.sudoku.persistence.LocalGameStorageImpl
import com.mr_17.sudoku.persistence.LocalSettingsStorageImpl
import com.mr_17.sudoku.persistence.LocalStatisticsStorageImpl
import com.mr_17.sudoku.persistence.settingsDataStore
import com.mr_17.sudoku.persistence.statsDataStore
import com.mr_17.sudoku.ui.activegame.ActiveGameContainer
import com.mr_17.sudoku.ui.activegame.ActiveGameLogic
import com.mr_17.sudoku.ui.activegame.ActiveGameViewModel

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic {
    return ActiveGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(
            context.statsDataStore
        ),
        ProductionDispatcherProvider
    )
}