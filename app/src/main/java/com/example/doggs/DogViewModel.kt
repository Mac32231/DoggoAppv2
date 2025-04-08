package com.example.doggs
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class Dog(
    val name: String,
    val breed: String,
    val isFavorite: Boolean = false,
    val imageUrl: String? = null
)

class DogViewModel : ViewModel() {
    var dogs by mutableStateOf(listOf<Dog>())
        private set

    fun addDog(dog: Dog) {
        dogs = dogs + dog
    }

    fun toggleFavorite(dog: Dog) {
        dogs = dogs.map { if (it == dog) it.copy(isFavorite = !it.isFavorite) else it }
    }

    fun removeDog(dog: Dog) {
        dogs = dogs.filter { it != dog }
    }

    fun searchDogs(query: String): List<Dog> {
        return dogs
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedWith(
                compareByDescending<Dog> { it.isFavorite }
                    .thenBy { it.name.lowercase() }
            )
    }

    suspend fun fetchDogImageUrl(): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = URL("https://dog.ceo/api/breeds/image/random").readText()
            val json = JSONObject(response)
            json.getString("message")
        } catch (e: Exception) {
            null
        }
    }
}