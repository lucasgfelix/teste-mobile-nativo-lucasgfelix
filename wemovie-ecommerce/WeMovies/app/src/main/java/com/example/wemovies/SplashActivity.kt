package com.example.wemovies

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(R.layout.activity_splash)

        val welcomeText = findViewById<TextView>(R.id.splash_welcome)  // "Bem-vindo a"
        val weMoviesText = findViewById<TextView>(R.id.splash_logo)  // "WeMovies"
        val ellipse1 = findViewById<ImageView>(R.id.ellipse1)  // Imagem 1
        val ellipse2 = findViewById<ImageView>(R.id.ellipse2)  // Imagem 2


        welcomeText.visibility = TextView.INVISIBLE
        weMoviesText.visibility = TextView.INVISIBLE
        ellipse1.visibility = ImageView.INVISIBLE
        ellipse2.visibility = ImageView.INVISIBLE


        val fadeInWelcome = AlphaAnimation(0f, 1f)
        fadeInWelcome.duration = 1500
        fadeInWelcome.startOffset = 500
        welcomeText.startAnimation(fadeInWelcome)
        welcomeText.visibility = TextView.VISIBLE


        val fadeInWeMovies = AlphaAnimation(0f, 1f)
        fadeInWeMovies.duration = 1500
        fadeInWeMovies.startOffset = 1500
        weMoviesText.startAnimation(fadeInWeMovies)
        weMoviesText.visibility = TextView.VISIBLE

        val fadeInEllipse1 = AlphaAnimation(0f, 1f)
        fadeInEllipse1.duration = 1500
        fadeInEllipse1.startOffset = 1500
        ellipse1.startAnimation(fadeInEllipse1)
        ellipse1.visibility = ImageView.VISIBLE

        val fadeInEllipse2 = AlphaAnimation(0f, 1f)
        fadeInEllipse2.duration = 1500
        fadeInEllipse2.startOffset = 1500
        ellipse2.startAnimation(fadeInEllipse2)
        ellipse2.visibility = ImageView.VISIBLE


        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3500)
    }
}