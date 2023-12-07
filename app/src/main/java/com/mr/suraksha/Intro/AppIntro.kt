package com.mr.suraksha.Intro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.mr.suraksha.LoginActivity
import com.mr.suraksha.MainActivity
import com.mr.suraksha.R
import com.mr.suraksha.SplashActivity
import com.mr.suraksha.databinding.ActivityAppIntroBinding

class AppIntro : AppCompatActivity() {

    private lateinit var binding: ActivityAppIntroBinding
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnSkip: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        // Check if the activity has been shown before
        if (isFirstTime()) {
            markFirstTimeShown()
        } else {
            redirectToMainActivity()
        }


}

    private fun btnVisibility(item: Int) {
        when (item) {
            0 -> {
                btnPrev.visibility = View.INVISIBLE
                btnNext.visibility = View.VISIBLE
            }

            3 -> {
                btnPrev.visibility = View.VISIBLE
                btnNext.visibility = View.INVISIBLE
                btnSkip.text = "Lets Start"
            }
            else ->{
                btnPrev.visibility = View.VISIBLE
                btnNext.visibility = View.VISIBLE
            }

        }
    }

private fun isFirstTime(): Boolean {
    // Check if the "firstTime" flag is set to true
    return sharedPreferences.getBoolean("firstTime", true)
}

private fun markFirstTimeShown() {
    // Set the "firstTime" flag to false
    sharedPreferences.edit().putBoolean("firstTime", false).apply()
    viewPager = binding.viewPager
    viewPager.adapter = PageAdapter(supportFragmentManager)

    tabLayout = binding.tabLayout
    tabLayout.setupWithViewPager(viewPager)

    btnSkip = binding.btnSkip
    btnPrev = binding.btnPrev
    btnNext = binding.btnNext


    viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.currentItem = tab.position
            btnVisibility(viewPager.currentItem)
        }
        override fun onTabUnselected(tab: TabLayout.Tab) {

        }
        override fun onTabReselected(tab: TabLayout.Tab) {

        }
    })

    btnSkip.setOnClickListener(View.OnClickListener {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    })

    btnVisibility(viewPager.currentItem)

    btnNext.setOnClickListener(View.OnClickListener {
        viewPager.currentItem = viewPager.currentItem + 1
        btnVisibility(viewPager.currentItem)
    })


    btnPrev.setOnClickListener(View.OnClickListener {
        viewPager.currentItem = viewPager.currentItem-1
        btnVisibility(viewPager.currentItem)
    })
}

private fun redirectToMainActivity() {
    // Redirect to the main activity or any other destination
    val intent = Intent(this, SplashActivity::class.java)
    startActivity(intent)
    finish()  // Finish this activity to prevent it from being shown again
}
}