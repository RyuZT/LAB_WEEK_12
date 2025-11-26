package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)

        // 1. Setup Adapter dengan Click Listener
        movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                // Navigasi ke DetailsActivity saat item diklik
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("movie_title", movie.title)
                intent.putExtra("movie_overview", movie.overview)
                intent.putExtra("movie_poster", movie.posterPath)
                intent.putExtra("movie_release_date", movie.releaseDate)
                startActivity(intent)
            }
        })

        recyclerView.adapter = movieAdapter

        // 2. Setup ViewModel
        val movieRepository = (application as MovieApplication).movieRepository

        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]

        // 3. Mengambil Data menggunakan Flow (Lifecycle Aware)
        lifecycleScope.launch {
            // repeatOnLifecycle memastikan flow hanya dikumpulkan saat Activity minimal STARTED
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Coroutine 1: Ambil data Popular Movies
                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        // Perhatikan: Tidak ada lagi logika filter/sort di sini.
                        // Data 'movies' sudah difilter & diurutkan di ViewModel.
                        movieAdapter.addMovies(movies)
                    }
                }

                // Coroutine 2: Ambil Error jika ada
                launch {
                    movieViewModel.error.collect { error ->
                        if (error.isNotEmpty()) {
                            Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}