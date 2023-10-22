package com.ptsiogas.composestories

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

@Composable
fun StoriesView(userStories: List<UserStory>) {
    if (userStories.isEmpty()) {
        Text(text = "No stories to show")
        return
    }
    val navController = rememberNavController()
    val currentStory = remember { mutableStateOf(userStories[0]) }
    val currentStoryIndex = remember { mutableStateOf(0) }
    val movingToNextStory = remember { mutableStateOf(true) }

    val storyContent: @Composable (Int) -> Unit = { index ->
        AsyncImage(
            model = currentStory.value.images[index],
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    val slideDuration = 200
    NavHost(navController = navController, startDestination = "storyList") {
        composable(route = "story", enterTransition = {
            if(movingToNextStory.value) {
                slideIntoContainer(
                    animationSpec = tween(slideDuration, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            } else {
                slideIntoContainer(
                    animationSpec = tween(slideDuration, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        }, exitTransition = {
            if(movingToNextStory.value) {
                slideOutOfContainer(
                    animationSpec = tween(slideDuration, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            } else {
                slideOutOfContainer(
                    animationSpec = tween(slideDuration, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        }) {
            Stories(numberOfPages = currentStory.value.images.size,
                onEveryStoryChange = { position ->
                    Log.i("DATA", "Story Change $position")
                },
                onComplete = {
                    Log.i("Action", "Completed")
                    val nextIndex = currentStoryIndex.value + 1
                    if (nextIndex < userStories.size) {
                        movingToNextStory.value = true
                        currentStory.value = userStories[nextIndex]
                        currentStoryIndex.value = nextIndex
                        navController.navigate("story")
                    } else {
                        navController.navigate("storyList")
                    }
                }, onPreviousUserStory = {
                    Log.i("Action", "Previous")
                    val previousIndex = currentStoryIndex.value - 1
                    if (previousIndex >= 0) {
                        movingToNextStory.value = false
                        currentStory.value = userStories[previousIndex]
                        currentStoryIndex.value = previousIndex
                        navController.navigate("story")
                    }
                }, onClose = {
                    navController.navigate("storyList")
                }, content = storyContent
            )
        }
        composable("storyList") {
            StoryView(stories = userStories, onClick = { story ->
                currentStory.value = story
                currentStoryIndex.value = userStories.indexOf(story)
                navController.navigate("story")
            })
        }
    }
}

@Composable
fun StoryView(stories: List<UserStory>, onClick: (UserStory) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        items(stories) { story ->
            StoryItem(story, onClick = {
                onClick(story)
            })
        }
    }
}

@Composable
fun StoryItem(story: UserStory, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            // Load the user's profile picture here
            AsyncImage(
                model = story.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        }
        Text(
            text = story.userName,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

data class UserStory(val userName: String, val imageUrl: String, val images: ArrayList<String>) {
    companion object {
        val demoStories = arrayListOf(
            UserStory(
                "user1",
                "https://picsum.photos/80/80",
                arrayListOf(
                    "https://picsum.photos/1080/1920",
                    "https://picsum.photos/1000/1800",
                )
            ),
            UserStory(
                "user2",
                "https://picsum.photos/100/100",
                arrayListOf(
                    "https://picsum.photos/800/1600",
                )
            ),
            UserStory(
                "user3",
                "https://picsum.photos/120/120",
                arrayListOf(
                    "https://picsum.photos/900/1700"
                )
            ),
            UserStory(
                "user4",
                "https://picsum.photos/130/130",
                arrayListOf(
                    "https://picsum.photos/600/800"
                )
            ),
        )
    }
}

@Composable
@Preview
fun StoryViewPreview() {
//    //random image url for story
//    val imageUrl1 = "https://picsum.photos/200/310"
//    val imageUrl2 = "https://picsum.photos/200/320"
//    val imageUrl3 = "https://picsum.photos/200/330"
//
//    val stories = listOf(
//        Story("user1", imageUrl1),
//        Story("user2", imageUrl2),
//        Story("user3", imageUrl3),
//        // Add more stories here
//    )
//
//    StoryView(stories = stories)
}