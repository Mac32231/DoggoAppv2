package com.example.doggs.data

import com.example.doggs.data.network.DogsService
import com.example.doggs.model.DogPhoto

interface DogsPhotosRepository {
    suspend fun getRandomDogImage(): DogPhoto
}

class NetworkDogsPhotosRepository(
    private val dogsService: DogsService,
) : DogsPhotosRepository {

    override suspend fun getRandomDogImage(): DogPhoto = dogsService.getRandomDogImage()
}


