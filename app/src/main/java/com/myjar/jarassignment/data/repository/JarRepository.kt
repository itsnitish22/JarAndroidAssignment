package com.myjar.jarassignment.data.repository

import com.myjar.jarassignment.data.api.ApiService
import com.myjar.jarassignment.data.model.ComputerItem

interface JarRepository {
    suspend fun fetchResults(): List<ComputerItem>
}

class JarRepositoryImpl(
    private val apiService: ApiService
) : JarRepository {
    override suspend fun fetchResults(): List<ComputerItem> {
        return apiService.fetchResults()
    }
}