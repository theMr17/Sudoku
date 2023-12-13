package com.mr_17.sudoku.ui.activegame

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mr_17.sudoku.R
import com.mr_17.sudoku.common.toTime
import com.mr_17.sudoku.ui.activeGameSubtitle
import com.mr_17.sudoku.ui.components.AppToolbar
import com.mr_17.sudoku.ui.components.LoadingScreen
import com.mr_17.sudoku.ui.inputButton
import com.mr_17.sudoku.ui.mutableSudokuSquare
import com.mr_17.sudoku.ui.newGameSubtitle
import com.mr_17.sudoku.ui.readOnlySudokuSquare

enum class ActiveGameScreenState {
    LOADING,
    ACTIVE,
    COMPLETE
}

@Composable
fun ActiveGameScreen(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {
    val contentTransitionState = remember {
        MutableTransitionState(
            ActiveGameScreenState.LOADING
        )
    }

    viewModel.subContentState = {
        contentTransitionState.targetState = it
    }

    val transition = updateTransition(contentTransitionState, label = "")

    val loadingAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = ""
    ) {
        if (it == ActiveGameScreenState.LOADING) 1f else 0f
    }

    val activeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = ""
    ) {
        if (it == ActiveGameScreenState.ACTIVE) 1f else 0f
    }

    val completeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = ""
    ) {
        if (it == ActiveGameScreenState.COMPLETE) 1f else 0f
    }

    Column(
        Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxHeight()
    ) {
        AppToolbar(
            modifier = Modifier.wrapContentHeight(),
            title = stringResource(R.string.app_name)
        ) {
            NewGameIcon(onEventHandler = onEventHandler)
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when (contentTransitionState.currentState) {
                ActiveGameScreenState.ACTIVE -> Box(
                    Modifier.alpha(activeAlpha)
                ) {
                    GameContent(
                        onEventHandler,
                        viewModel
                    )
                }
                ActiveGameScreenState.COMPLETE -> Box(
                    Modifier.alpha(activeAlpha)
                ) {
                    GameCompleteContent(
                        viewModel.timerState,
                        viewModel.isNewRecordState
                    )
                }
                ActiveGameScreenState.LOADING -> Box(
                    Modifier.alpha(activeAlpha)
                ) {
                    LoadingScreen()
                }
            }
        }
    }
}

@Composable
fun NewGameIcon(
    onEventHandler: (ActiveGameEvent) -> Unit
) {
    Icon(
        imageVector = Icons.Filled.Add,
        tint = MaterialTheme.colorScheme.tertiary,
        contentDescription = null,
        modifier = Modifier
            .clickable {
                onEventHandler.invoke(ActiveGameEvent.OnNewGameClicked)
            }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameContent(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {
    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        val margin = with(LocalDensity.current) {
            when {
                constraints.maxHeight.toDp().value < 500 -> 20
                constraints.maxHeight.toDp().value < 550 -> 8
                else -> 0
            }
        }

        ConstraintLayout {
            val (board, timer, diff, inputs) = createRefs()

            Box(
                Modifier
                    .constrainAs(board) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .background(MaterialTheme.colorScheme.surface)
                    .size(screenWidth - margin.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )
            ) {
                SudokuBoard(
                    onEventHandler,
                    viewModel,
                    screenWidth - margin.dp
                )
            }

            Row(
                Modifier
                    .wrapContentSize()
                    .constrainAs(diff) {
                        top.linkTo(board.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                (0..viewModel.difficulty.ordinal).forEach {
                    Icon(
                        contentDescription = stringResource(R.string.difficulty),
                        imageVector = Icons.Filled.Star,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 4.dp)
                    )
                }
            }

            Box(Modifier
                .wrapContentSize()
                .constrainAs(timer) {
                    top.linkTo(board.bottom)
                    start.linkTo(parent.start)
                }
                .padding(start = 16.dp))
            {
                TimerText(viewModel)
            }

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(diff) {
                        top.linkTo(board.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                (0..viewModel.difficulty.ordinal).forEach {
                    Icon(
                        contentDescription = stringResource(R.string.difficulty),
                        imageVector = Icons.Filled.Star,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(inputs) {
                        top.linkTo(timer.bottom)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.boundary == 4) {
                    InputButtonRow(
                        (0..4).toList(),
                        onEventHandler
                    )
                } else {
                    InputButtonRow(
                        (0..4).toList(),
                        onEventHandler
                    )

                    InputButtonRow(
                        (5..9).toList(),
                        onEventHandler
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuBoard(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel,
    size: Dp
) {
    val boundary = viewModel.boundary

    val tileOFfset = size.value / boundary

    var boardState by remember {
        mutableStateOf(viewModel.boardState, neverEqualPolicy())
    }

    viewModel.subBoardState = {
        boardState = it
    }

    SudokuTextFields(
        onEventHandler,
        tileOFfset,
        boardState
    )

    BoardGrid(
        boundary,
        tileOFfset
    )
}

@Composable
fun BoardGrid(
    boundary: Int,
    tileOFfset: Float
) {
    (1 until boundary).forEach {
        val width = if(it % boundary.sqrt == 0) 3.dp else 1.dp
        Divider(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .absoluteOffset((tileOFfset * it).dp, 0.dp)
                .fillMaxHeight()
                .width(width)
        )

        val height = if(it % boundary.sqrt == 0) 3.dp else 1.dp
        Divider(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .absoluteOffset((tileOFfset * it).dp, 0.dp)
                .fillMaxWidth()
                .height(height)
        )
    }
}

@Composable
fun SudokuTextFields(
    onEventHandler: (ActiveGameEvent) -> Unit,
    tileOFfset: Float,
    boardState: HashMap<Int, SudokuTile>
) {
    boardState.values.forEach {tile ->
        var text = tile.value.toString()

        if (!tile.readOnly) {
            if (text == "0") text = ""

            Text(
                text = text,
                style = mutableSudokuSquare(tileOFfset).copy(
                    color = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier
                    .absoluteOffset(
                        (tileOFfset * (tile.x - 1)).dp,
                        (tileOFfset * (tile.y - 1)).dp,
                    )
                    .size(tileOFfset.dp)
                    .background(
                        if(tile.hasFocus) MaterialTheme.colorScheme.onPrimary.copy(alpha = .25f)
                        else MaterialTheme.colorScheme.surface
                    )
                    .clickable {
                        onEventHandler.invoke(
                            ActiveGameEvent.OnTileFocused(tile.x, tile.y)
                        )
                    }
            )
        } else {
            Text(
                text = text,
                style = readOnlySudokuSquare(tileOFfset),
                modifier = Modifier
                    .absoluteOffset(
                        (tileOFfset * (tile.x - 1)).dp,
                        (tileOFfset * (tile.y - 1)).dp,
                    )
                    .size(tileOFfset.dp)
            )
        }
    }
}

@Composable
fun TimerText(viewModel: ActiveGameViewModel) {

    var timerState by remember {
        mutableStateOf("")
    }

    viewModel.subTimerState = {
        timerState = it.toTime()
    }

    Text(
        modifier = Modifier.requiredHeight(36.dp),
        text = timerState,
        style = activeGameSubtitle.copy(color = MaterialTheme.colorScheme.tertiary)
    )
}

@Composable
fun InputButtonRow(
    numbers: List<Int>,
    onEventHandler: (ActiveGameEvent) -> Unit
) {
    Row {
        numbers.forEach {
            SudokuInputButton(
                onEventHandler,
                it
            )
        }
    }

    //margin between rows
    Spacer(modifier = Modifier.size(2.dp))
}

@Composable
fun SudokuInputButton(
    onEventHandler: (ActiveGameEvent) -> Unit,
    number: Int
) {
    //This wrapper allows us to style a nice looking button instead of just adding onClick on a
    //text composable
    TextButton(
        //Here is how we handle click events using onClick and our onEventHandler
        onClick = { onEventHandler.invoke(ActiveGameEvent.OnInput(number)) },
        modifier = Modifier
            .requiredSize(56.dp)
            .padding(2.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),

        ) {
        Text(
            text = number.toString(),
            style = inputButton.copy(color = MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GameCompleteContent(timerState: Long, isNewRecordState: Boolean) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                contentDescription = stringResource(R.string.game_complete),
                imageVector = Icons.Filled.Star,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier.size(128.dp, 128.dp)
            )

            if (isNewRecordState) Image(
                contentDescription = null,
                imageVector = Icons.Filled.Star,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(36.dp, 36.dp)
                    .absoluteOffset(y = (-16).dp)
            )
        }

        Text(
            text = stringResource(R.string.total_time),
            style = newGameSubtitle.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )

        Text(
            text = timerState.toTime(),
            style = newGameSubtitle.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Normal
            )
        )
    }
}
