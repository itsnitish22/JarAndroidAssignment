package com.myjar.jarassignment.data.repository

import com.myjar.jarassignment.data.api.ApiService
import com.myjar.jarassignment.data.model.ComputerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface JarRepository {
    fun fetchResults(): Flow<List<ComputerItem>>
}

class JarRepositoryImpl(
    private val apiService: ApiService
) : JarRepository {
    override fun fetchResults(): Flow<List<ComputerItem>> = flow {
        val response = apiService.fetchResults()
        emit(response)
    }
}