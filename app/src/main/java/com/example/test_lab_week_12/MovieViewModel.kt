package com.example.test_lab_week_12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map // Pastikan import map
import kotlinx.coroutines.launch
import java.util.Calendar

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    // Define StateFlow sebagai pengganti LiveData
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    // fetch movies from the API
    private fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                // --- IMPLEMENTASI FILTER DI SINI ---
                // Operator map digunakan untuk mengubah data di dalam stream flow
                .map { movies ->
                    // Kita filter tahun (opsional, agar sama seperti lab sebelumnya)
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

                    movies.filter { movie ->
                        movie.releaseDate?.startsWith(currentYear) == true
                    }.sortedByDescending {
                        it.popularity // Urutkan berdasarkan popularitas tertinggi
                    }
                }
                // -----------------------------------
                .catch { exception ->
                    _error.value = "An exception occurred: ${exception.message}"
                }
                .collect { movies ->
                    // Hasil di sini sudah terurut (sorted)
                    _popularMovies.value = movies
                }
        }
    }
}