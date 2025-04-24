package com.giahung.lab3homework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.giahung.lab3homework.ui.ImageViewModel
import com.giahung.lab3homework.ui.ImageWithObjects
import com.giahung.lab3homework.ui.theme.Lab3HomeworkTheme
import com.giahung.lab3homework.utils.DetectedObject // Ensure correct import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab3HomeworkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageGallery()
                }
            }
        }
    }
}

@Composable
fun ImageGallery() {
    val context = LocalContext.current
    val viewModel: ImageViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ImageViewModel(context) as T
        }
    })
    val imagesWithObjects = viewModel.imagesWithObjects.collectAsState().value
    val currentPage = viewModel.currentPage.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image Grid
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(imagesWithObjects.size) { index ->
                    ImageCard(imagesWithObjects[index])
                }
            }
        }

        // Pagination Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.previousPage() },
                enabled = currentPage > 1 && !isLoading
            ) {
                Text("Previous")
            }
            Text("Page $currentPage")
            Button(
                onClick = { viewModel.nextPage() },
                enabled = !isLoading
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ImageCard(imageWithObjects: ImageWithObjects) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            // Display the image
            AsyncImage(
                model = imageWithObjects.image.webformatUrl,
                contentDescription = imageWithObjects.image.tags,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            // Draw bounding boxes for detected objects
            Canvas(modifier = Modifier.matchParentSize()) {
                val imageWidth = size.width
                val imageHeight = size.height
                imageWithObjects.detectedObjects.forEach { obj ->
                    val rect = obj.boundingBox
                    // Scale the bounding box to the canvas size
                    val left = (rect.left / 640f) * imageWidth // Assuming original image width ~640px
                    val top = (rect.top / 480f) * imageHeight // Assuming original image height ~480px
                    val right = (rect.right / 640f) * imageWidth
                    val bottom = (rect.bottom / 480f) * imageHeight
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset(left, top),
                        size = Size(right - left, bottom - top),
                        style = Stroke(width = 2f)
                    )
                }
            }
        }

        // Display image tags
        Text(
            text = imageWithObjects.image.tags,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        // Display detected objects
        imageWithObjects.detectedObjects.forEach { obj ->
            Text(
                text = "${obj.label}: ${(obj.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}