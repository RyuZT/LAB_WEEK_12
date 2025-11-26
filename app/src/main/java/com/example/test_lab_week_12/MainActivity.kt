package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)

        // Setup Adapter
        movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("movie_title", movie.title)
                intent.putExtra("movie_overview", movie.overview)
                intent.putExtra("movie_poster", movie.posterPath)
                intent.putExtra("movie_release_date", movie.releaseDate)
                startActivity(intent)
            }
        })
        recyclerView.adapter = movieAdapter

        val movieRepository = (application as MovieApplication).movieRepository

        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]

        // --- BAGIAN BARU: MENGGUNAKAN FLOW ---

        // lifecycleScope adalah coroutine scope yang sadar lifecycle activity
        lifecycleScope.launch {
            // repeatOnLifecycle memastikan coroutine berjalan hanya ketika Activity minimal STARTED
            // ini mencegah crash atau pemborosan resource saat aplikasi di background
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Launch coroutine pertama untuk collect popularMovies
                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        // Saat data diterima, filter dan masukkan ke adapter
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
                        movieAdapter.addMovies(
                            movies.filter { movie ->
                                movie.releaseDate?.startsWith(currentYear) == true
                            }.sortedByDescending { it.popularity }
                        )
                    }
                }

                // Launch coroutine kedua untuk collect error
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