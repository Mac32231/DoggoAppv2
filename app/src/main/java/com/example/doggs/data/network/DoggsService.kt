package com.example.doggs.data.network

import com.example.doggs.model.DogPhoto
import retrofit2.http.GET

interface DogsService {

    @GET("image/random")
    suspend fun getRandomDogImage(): DogPhoto
}