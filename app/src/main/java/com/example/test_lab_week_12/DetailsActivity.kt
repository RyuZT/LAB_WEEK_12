package com.example.test_lab_week_12

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Setting judul action bar
        supportActionBar?.title = "Movie Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 1. Ambil Data dari Intent
        val title = intent.getStringExtra("movie_title")
        val overview = intent.getStringExtra("movie_overview")
        val posterPath = intent.getStringExtra("movie_poster")
        val releaseDate = intent.getStringExtra("movie_release_date")

        // 2. Hubungkan dengan View di XML (activity_details.xml)
        val titleTextView: TextView = findViewById(R.id.detail_title)
        val dateTextView: TextView = findViewById(R.id.detail_date)
        val overviewTextView: TextView = findViewById(R.id.detail_overview)
        val posterImageView: ImageView = findViewById(R.id.detail_poster)

        // 3. Masukkan data ke View
        titleTextView.text = title
        dateTextView.text = releaseDate
        overviewTextView.text = overview

        // Load gambar pakai Glide
        if (posterPath != null) {
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500$posterPath")
                .into(posterImageView)
        }
    }

    // Agar tombol Back di pojok kiri atas berfungsi
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}