package com.ptsiogas.composestories

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun LinearIndicator(
    modifier: Modifier,
    isLastStory: Boolean = false,
    startProgress: Boolean = false,
    indicatorBackgroundColor: Color,
    indicatorProgressColor: Color,
    slideDurationInSeconds: Long,
    onPauseTimer: Boolean = false,
    onAnimationEnd: () -> Unit,
    currentStoryIndex: Int,
    storyIndex: Int,
    haveSeenStory: Boolean = false
) {

    val delayInMillis = rememberSaveable {
        (slideDurationInSeconds * 1000) / 100
    }

    var progress by remember {
        mutableStateOf(0.00f)
    }
    // if currentStoryIndex is not the StoryIndex then reset the progress
    if (currentStoryIndex != storyIndex) {
        progress = 0.00f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    val animatedProgressWithReset = if (haveSeenStory) {
        1f
    } else if (progress == 0f) {
        0f
    } else {
        animatedProgress
    }


    if (startProgress) {
        LaunchedEffect(key1 = onPauseTimer) {
            while (progress < 1f && isActive && onPauseTimer.not()) {
                progress += 0.01f
                delay(delayInMillis)
            }

            //When the timer is not paused and animation completes then move to next page.
            if (onPauseTimer.not()) {
                delay(200)
                if (isLastStory) {
                    progress = 0.00f
                }
                onAnimationEnd()
            }
        }
    }

    LinearProgressIndicator(
        modifier = modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp)),
        progress = animatedProgressWithReset,
        color = indicatorProgressColor,
        trackColor = indicatorBackgroundColor
    )
}