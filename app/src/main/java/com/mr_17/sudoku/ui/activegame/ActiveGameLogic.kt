package com.mr_17.sudoku.ui.activegame

import com.mr_17.sudoku.common.BaseLogic
import com.mr_17.sudoku.common.DispatcherProvider
import com.mr_17.sudoku.domain.IGameRepository
import com.mr_17.sudoku.domain.IStatisticsRepository
import com.mr_17.sudoku.domain.SudokuPuzzle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ActiveGameLogic(
    private val container: ActiveGameContainer?,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    private val dispatcher: DispatcherProvider
): BaseLogic<ActiveGameEvent>(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUiContext() + jobTracker

    init {
        jobTracker = Job()
    }

    inline fun startCoroutineTimer(
        crossinline action: () -> Unit
    ) = launch {
        while (true) {
            action()
            delay(1000)
        }
    }

    private var timerTracker: Job? = null

    private val Long.timeOffset: Long
        get() {
            return if(this <= 0) 0 else this - 1
        }

    override fun onEvent(event: ActiveGameEvent) {
        when (event) {
            is ActiveGameEvent.OnInput -> onInput(
                event.input,
                viewModel.timerState
            )
            ActiveGameEvent.OnNewGameClicked -> onNewGameClicked()
            ActiveGameEvent.OnStart -> onStart()
            ActiveGameEvent.OnStop -> onStop()
            is ActiveGameEvent.OnTileFocused -> onTileFocused(event.x, event.y)
        }
    }

    /**
     * Check for any tile which hasFocus, and if so, write that value
     */
    private fun onInput(input: Int, elapsedTime: Long) = launch {
        var focusedTile: SudokuTile? = null
        viewModel.boardState.values.forEach {
            if (it.hasFocus) focusedTile = it
        }

        if (focusedTile != null) {
            gameRepo.updateNode(focusedTile!!.x,
                focusedTile!!.y,
                input,
                elapsedTime,
                //success
                { isComplete ->
                    focusedTile?.let {
                        viewModel.updateBoardState(
                            it.x,
                            it.y,
                            input,
                            false
                        )
                    }
                    if (isComplete) {
                        timerTracker?.cancel()
                        checkIfNewRecord()
                    }
                },
                //error
                {
                    container?.showError()
                }
            )
        }
    }

    private fun checkIfNewRecord() = launch {
        statsRepo.updateStatistics(
            viewModel.timerState,
            viewModel.difficulty,
            viewModel.boundary,
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompleteState()
            },
            {
                container?.showError()
                viewModel.updateCompleteState()
            }
        )
    }

    private fun onNewGameClicked() = launch {
        viewModel.showLoadingState()

        if (!viewModel.isCompleteState) {
            gameRepo.getCurrentGame(
                { puzzle, _ ->
                    updateWithTime(puzzle)
                },
                {
                    container?.showError()
                }
            )
        } else {
            navigateToNewGame()
        }
    }

    private fun updateWithTime(puzzle: SudokuPuzzle) = launch {
        gameRepo.updateGame(puzzle.copy(elapsedTime = viewModel.timerState.timeOffset),
            { navigateToNewGame() },
            {
                navigateToNewGame()
                container?.showError()
            }
        )
    }

    private fun navigateToNewGame() {
        cancelStuff()
        container?.onNewGameClick()
    }

    private fun cancelStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }

    private fun onStart() = launch {
        gameRepo.getCurrentGame(
            { puzzle, isComplete ->
                viewModel.initializeBoardState(
                    puzzle,
                    isComplete
                )
                if (!isComplete) timerTracker = startCoroutineTimer {
                    viewModel.updateTimerState()
                }
            },
            {
                //Probably this happened when the App is first run or data deleted.
                //Prompt the user to create a new game immediately.
                container?.onNewGameClick()
            }
        )
    }

    private fun onStop() {
        if (!viewModel.isCompleteState) {
            launch {
                gameRepo.saveGame(
                    viewModel.timerState.timeOffset,
                    { cancelStuff() },
                    {
                        cancelStuff()
                        container?.showError()
                    }
                )
            }
        } else {
            cancelStuff()
        }
    }

    private fun onTileFocused(x: Int, y: Int) {
        viewModel.updateFocusState(x, y)
    }
}