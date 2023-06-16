package com.example.mykotlinsocialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)



        val logoImg: ImageView = findViewById(R.id.iv_logo)

        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide)
        logoImg.startAnimation(slideAnimation)

        val token = SharedPreferencesHelper(this).getToken()



        Handler().postDelayed({

            if (token != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }

        }, 3000)

    }
}