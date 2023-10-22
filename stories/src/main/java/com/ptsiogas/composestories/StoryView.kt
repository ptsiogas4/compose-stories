package com.ptsiogas.composestories

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun StoryView(stories: List<Story>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        items(stories) { story ->
            StoryItem(story)
        }
    }
}

@Composable
fun StoryItem(story: Story) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {

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
            AsyncImage(model = story.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier
                .size(75.dp)
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

data class Story(val userName: String, val imageUrl: String)

@Composable
@Preview
fun StoryViewPreview() {
    //random image url for story
    val imageUrl1 = "https://picsum.photos/200/310"
    val imageUrl2 = "https://picsum.photos/200/320"
    val imageUrl3 = "https://picsum.photos/200/330"

    val stories = listOf(
        Story("user1", imageUrl1),
        Story("user2", imageUrl2),
        Story("user3", imageUrl3),
        // Add more stories here
    )

    StoryView(stories = stories)
}
