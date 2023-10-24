package com.ptsiogas.composestories

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Math.abs

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(userStories: List<UserStory>, selectedUserIndex: Int, onClose: () -> Unit) {
    val pagerState =
        rememberPagerState(pageCount = userStories.size, initialPage = selectedUserIndex)

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = {
            onClose()
        },
        sheetState = modalBottomSheetState,
        dragHandle = { },
    ) {
        HorizontalPager(
            state = pagerState, dragEnabled = true,
            modifier = Modifier
        ) { pageIndex ->
            Stories(
                modalBottomSheetState = modalBottomSheetState,
                isInSpotlight = pageIndex == pagerState.currentPage,
                userStory = userStories.getOrNull(pageIndex) ?: UserStory("", "", ArrayList()),
                numberOfPages = userStories.getOrNull(pageIndex)?.images?.size ?: 0,
                onEveryStoryChange = { position ->
                    Log.i("DATA", "Story Change $position")
                },
                onComplete = {
                    Log.i("Action", "Completed")
                    val nextIndex = pageIndex + 1
                    if (nextIndex < userStories.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextIndex)
                        }
                    } else {
                        onClose()
                    }
                }, onPreviousUserStory = {

                }, onClose = {
                    onClose()
                }
            )
        }
    }
}

@OptIn(
    ExperimentalPagerApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun Stories(
    modalBottomSheetState: SheetState,
    isInSpotlight: Boolean,
    userStory: UserStory,
    numberOfPages: Int,
    indicatorModifier: Modifier = Modifier
        .padding(top = 12.dp, bottom = 12.dp)
        .clip(RoundedCornerShape(12.dp)),
    spaceBetweenIndicator: Dp = 4.dp,
    indicatorBackgroundColor: Color = Color.LightGray,
    indicatorProgressColor: Color = Color.White,
    indicatorBackgroundGradientColors: List<Color> = emptyList(),
    slideDurationInSeconds: Long = 5,
    touchToPause: Boolean = true,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit,
    onPreviousUserStory: () -> Unit,
    onClose: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = numberOfPages)
    val coroutineScope = rememberCoroutineScope()
    val isPressed = remember { mutableStateOf(false) }
    var pauseTimer by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        //Full screen content behind the indicator
        StoryImage(pagerState = pagerState, userStory = userStory)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) { // (1)
            val maxWidth = this.size.width // (2)
            detectTapGestures(
                onLongPress = {
                    pauseTimer = true
                    isPressed.value = true
                },
                onPress = {
                    val pressStartTime = System.currentTimeMillis()
                    isPressed.value = true
                    this.tryAwaitRelease() // (4)
                    val pressEndTime = System.currentTimeMillis()
                    val totalPressTime = pressEndTime - pressStartTime // (5)
                    if (totalPressTime < 200 && modalBottomSheetState.requireOffset() == 0f) {
                        val isTapOnRightTwoTiers = (it.x > (maxWidth / 4)) // (6)
                        if (isTapOnRightTwoTiers) {
                            // Next
                            coroutineScope.launch {
                                val newPage = pagerState.currentPage + 1
                                if (pagerState.currentPage < numberOfPages - 1) {
                                    onEveryStoryChange?.invoke(newPage)
                                    pagerState.animateScrollToPage(newPage)
                                } else {
                                    onComplete()
                                }
                            }
                        } else {
                            // Previous
                            coroutineScope.launch {
                                val newPage = pagerState.currentPage - 1
                                if (pagerState.currentPage > 0) {
                                    onEveryStoryChange?.invoke(newPage)
                                    pagerState.animateScrollToPage(newPage)
                                } else {
                                    onPreviousUserStory()
                                }
                            }
                        }
                    }
                    isPressed.value = false
                },
            )
        }) {
        //Indicator based on the number of items
        val modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    if (indicatorBackgroundGradientColors.isEmpty()) listOf(
                        Color.Black,
                        Color.Transparent
                    ) else indicatorBackgroundGradientColors
                )
            )
        AnimatedVisibility(visible = (!pauseTimer)) {
            Column {
                Row(
                    modifier = modifier.statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.padding(spaceBetweenIndicator))

                    ListOfIndicators(
                        isInSpotlight = isInSpotlight,
                        userStory = userStory,
                        numberOfPages,
                        indicatorModifier,
                        indicatorBackgroundColor,
                        indicatorProgressColor,
                        slideDurationInSeconds,
                        pauseTimer,
                        coroutineScope,
                        pagerState,
                        spaceBetweenIndicator,
                        onEveryStoryChange = onEveryStoryChange,
                        onComplete = onComplete,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -24.dp)
                ) {
                    IconButton(onClick = {
                        onClose()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun RowScope.ListOfIndicators(
    isInSpotlight: Boolean = false,
    userStory: UserStory,
    numberOfPages: Int,
    indicatorModifier: Modifier,
    indicatorBackgroundColor: Color,
    indicatorProgressColor: Color,
    slideDurationInSeconds: Long,
    pauseTimer: Boolean,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    spaceBetweenIndicator: Dp,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit,
) {
    println("DATA: ${userStory.images.size}")
    val currentPage = pagerState.currentPage

    for (index in 0 until numberOfPages) {
        LinearIndicator(
            modifier = indicatorModifier.weight(1f),
            isLastStory = index == numberOfPages - 1,
            index == currentPage,
            indicatorBackgroundColor,
            indicatorProgressColor,
            slideDurationInSeconds,
            pauseTimer || !isInSpotlight,
            currentStoryIndex = currentPage,
            storyIndex = index,
            onAnimationEnd = {
                coroutineScope.launch {
                    val newPage = pagerState.currentPage + 1

                    if (newPage < numberOfPages) {
                        onEveryStoryChange?.invoke(newPage)
                        pagerState.animateScrollToPage(newPage)
                    }

                    if (newPage == numberOfPages) {
                        onComplete()
                    }
                }
            },
            haveSeenStory = index < currentPage
        )

        Spacer(modifier = Modifier.padding(spaceBetweenIndicator))
    }
}