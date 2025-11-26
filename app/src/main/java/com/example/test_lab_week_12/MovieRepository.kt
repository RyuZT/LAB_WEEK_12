package com.example.test_lab_week_12

import com.example.test_lab_week_12.api.MovieService
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val movieService: MovieService) {
    private val apiKey = "93cd21b7c40fb8cf150d46de2ab8cc44"

    // FETCH MOVIES DENGAN FLOW
    // Fungsi ini mengembalikan Flow yang berisi List<Movie>
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // emit daftar popular movies dari API
            emit(movieService.getPopularMovies(apiKey).results)
        }.flowOn(Dispatchers.IO) // Menjalankan flow ini di background thread (IO)
    }
}