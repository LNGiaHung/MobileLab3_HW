package com.giahung.lab3homework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.giahung.lab3homework.data.Image
import com.giahung.lab3homework.ui.ImageViewModel
import com.giahung.lab3homework.ui.theme.Lab3HomeworkTheme

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
fun ImageGallery(viewModel: ImageViewModel = viewModel()) {
    val images = viewModel.images.collectAsState().value
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
                items(images.size) { index ->
                    ImageCard(images[index])
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
fun ImageCard(image: Image) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = image.webformatUrl,
            contentDescription = image.tags,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = image.tags,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(8.dp)
        )
    }
}