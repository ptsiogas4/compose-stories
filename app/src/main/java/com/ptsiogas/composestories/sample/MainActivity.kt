package com.ptsiogas.composestories.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.ptsiogas.composestories.Stories
import com.ptsiogas.composestories.Story
import com.ptsiogas.composestories.StoryView
import com.ptsiogas.composestories.sample.ui.theme.SampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //random image url for story
//                    val imageUrl1 = "https://picsum.photos/200/310"
//                    val imageUrl2 = "https://picsum.photos/200/320"
//                    val imageUrl3 = "https://picsum.photos/200/330"
//
//                    val stories = listOf(
//                        Story("user1", imageUrl1),
//                        Story("user2", imageUrl2),
//                        Story("user3", imageUrl3),
//                        // Add more stories here
//                    )
//
//                    StoryView(stories = stories)

                    SampleStories()
                }
            }
        }
    }
}

@Composable
fun SampleStories() {
    val listOfImages = listOf(
        "https://picsum.photos/200/310",
        "https://picsum.photos/200/320",
        "https://picsum.photos/200/330"
    )

    Stories(numberOfPages = listOfImages.size,
        onEveryStoryChange = { position ->
            Log.i("DATA", "Story Change $position")
        },
        onComplete = {
            Log.i("Action", "Completed")
        }) { index ->
        AsyncImage(
            model = listOfImages[index],
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleTheme {
        Greeting("Android")
    }
}