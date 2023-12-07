package com.mr.suraksha

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.mr.suraksha.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var mProgressStatus = 0

    private val mHandler = Handler()

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread {
            while (mProgressStatus < 100) {
                mProgressStatus++
                SystemClock.sleep(35)
            }
            mHandler.post {
                val homeIntent = Intent(this, LoginActivity::class.java)
                startActivity(homeIntent)
                finish()
            }
        }.start()

    }
}