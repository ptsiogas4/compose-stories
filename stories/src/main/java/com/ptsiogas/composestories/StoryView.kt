package com.ptsiogas.composestories

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun StoriesView(userStories: List<UserStory>) {
    if (userStories.isEmpty()) {
        Text(text = "No stories to show")
        return
    }
    val navController = rememberNavController()

    val pagerState = rememberPagerState(pageCount = userStories.size, initialPage = 0)
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val showModal = remember { mutableStateOf(false) }

    if (showModal.value) {
        ModalBottomSheet(
            modifier = Modifier,
            onDismissRequest = {
                showModal.value = false
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
                            showModal.value = false
                        }
                    }, onPreviousUserStory = {

                    }, onClose = {
                        showModal.value = false
                    }
                )
            }
        }
    }

    NavHost(navController = navController, startDestination = "storyList") {
        composable("storyList") {
            StoryView(stories = userStories, onClick = { story ->
                showModal.value = true
                coroutineScope.launch {
                    pagerState.scrollToPage(userStories.indexOf(story))
                }
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