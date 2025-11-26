package com.example.test_lab_week_12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    // Define StateFlow sebagai pengganti LiveData
    // MutableStateFlow adalah StateFlow yang nilainya bisa diubah
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    // Fetch movies dari API menggunakan Flow
    private fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .catch { exception ->
                    // catch adalah terminal operator untuk menangkap error dari Flow
                    _error.value = "An exception occurred: ${exception.message}"
                }
                .collect { movies ->
                    // collect adalah terminal operator untuk mengambil nilai dari Flow
                    // hasilnya dimasukkan ke StateFlow
                    _popularMovies.value = movies
                }
        }
    }
}