package com.example.doggs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun DoggsApp(navController: NavController, viewModel: DogViewModel) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    Column {
        AppBar(title = "Doggs", navController = navController, showProfile = true, showSettings = true)
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    label = { Text("Wyszukaj pieska") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { navController.navigate("addDog") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Dodaj psa", tint = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            DogList(
                dogs = viewModel.searchDogs(textState.text),
                onFavoriteClick = viewModel::toggleFavorite,
                onDeleteClick = viewModel::removeDog,
                onDogClick = { dog -> navController.navigate("dogDetail/${dog.name}") }
            )
        }
    }
}

@Composable
fun AddDogScreen(navController: NavController, viewModel: DogViewModel) {
    Column {
        AppBar(title = "Dodaj psa", navController = navController, showBack = true)
        var dogName by remember { mutableStateOf("") }
        var dogBreed by remember { mutableStateOf("") }
        var imageUrl by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var isError by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            try {
                isLoading = true
                isError = false
                imageUrl = viewModel.fetchDogImageUrl()
            } catch (e: Exception) {
                isError = true
            } finally {
                isLoading = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DogImageBoxBig(imageUrl = imageUrl, isLoading = isLoading, isError = isError)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = dogName,
                onValueChange = { dogName = it },
                label = { Text("Imię psa") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = dogBreed,
                onValueChange = { dogBreed = it },
                label = { Text("Rasa psa") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (dogName.isNotBlank() && dogBreed.isNotBlank()) {
                    viewModel.addDog(Dog(name = dogName, breed = dogBreed, imageUrl = imageUrl))
                    navController.popBackStack()
                }
            }) {
                Text("Dodaj")
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    Column {
        AppBar(title = "Ustawienia", navController = navController, showBack = true)
        Column(modifier = Modifier.fillMaxSize()) {}
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Column {
        AppBar(title = "Profil", navController = navController, showBack = true)
        Column(modifier = Modifier.fillMaxSize()) {}
    }
}

@Composable
fun DogDetailScreen(navController: NavController, dog: Dog, viewModel: DogViewModel) {
    Column {
        DogDetailTopBar(navController, dog) {
            viewModel.removeDog(dog)
            navController.popBackStack()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (dog.imageUrl != null) {
                    AsyncImage(
                        model = dog.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = dog.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = dog.breed, fontSize = 18.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun DogList(
    dogs: List<Dog>,
    onFavoriteClick: (Dog) -> Unit,
    onDeleteClick: (Dog) -> Unit,
    onDogClick: (Dog) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        dogs.forEach { dog ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onDogClick(dog) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                DogImageBox(dog.imageUrl)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = dog.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = dog.breed, fontSize = 14.sp, color = Color.DarkGray)
                }
                IconButton(onClick = { onFavoriteClick(dog) }) {
                    if (dog.isFavorite) {
                        val brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6A5ACD),
                                Color(0xFFFFC0CB)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(50f, 100f)
                        )

                        Icon(
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.99f)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(brush, blendMode = BlendMode.SrcAtop)
                                    }
                                },
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Ulubiony"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Nieulubiony",
                            tint = Color(0xFF800080)
                        )
                    }
                }
                IconButton(onClick = { onDeleteClick(dog) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Usuń", tint = Color.Red)
                }
            }
            Divider(color = Color.Gray, thickness = 1.dp)
        }
    }
}

@Composable
fun AppBar(
    title: String,
    navController: NavController,
    showBack: Boolean = false,
    showSettings: Boolean = false,
    showProfile: Boolean = false,
) {
    Surface(
        color = Color(0xFFFEF7FF),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp), 
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Cofnij",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else if (showSettings) {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Ustawienia",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            if (showProfile) {
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profil",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}

@Composable
fun DogImageBox(imageUrl: String?) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl == null) {
            Icon(Icons.Filled.Warning, contentDescription = "Brak zdjęcia", tint = Color.Red)
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun DogImageBoxBig(imageUrl: String?, isLoading: Boolean, isError: Boolean) {
    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> Icon(Icons.Filled.Refresh, contentDescription = "Ładowanie", tint = Color.Gray)
            isError || imageUrl == null -> Icon(Icons.Filled.Warning, contentDescription = "Błąd", tint = Color.Red)
            else -> AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun DogDetailTopBar(navController: NavController, dog: Dog, onDelete: () -> Unit) {
    Surface(color = Color(0xFEF7FF), shadowElevation = 4.dp) {
        Row(
             modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Cofnij")
            }
            Text(text = "Szczegóły psa", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Usuń psa", tint = Color.Red)
            }
        }
    }
}
