package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)

        // --- INI BAGIAN PENTING (KLIK LOGIC) ---
        movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                // Saat item diklik, pindah ke DetailsActivity
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("movie_title", movie.title)
                intent.putExtra("movie_overview", movie.overview)
                intent.putExtra("movie_poster", movie.posterPath)
                intent.putExtra("movie_release_date", movie.releaseDate)
                startActivity(intent)
            }
        })
        // ---------------------------------------

        recyclerView.adapter = movieAdapter

        // Mengambil Repository dari Application
        val movieRepository = (application as MovieApplication).movieRepository

        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]

        movieViewModel.popularMovies.observe(this) { popularMovies ->
            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
            movieAdapter.addMovies(
                popularMovies
                    .filter { movie ->
                        movie.releaseDate?.startsWith(currentYear) == true
                    }
                    .sortedByDescending { it.popularity }
            )
        }

        movieViewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}