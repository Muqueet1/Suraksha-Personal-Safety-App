package com.mr.suraksha

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.telephony.SmsManager
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mr.suraksha.databinding.ActivityMainBinding
import com.mr.suraksha.ui.home.HomeFragment
import java.lang.Math.sqrt
import java.util.Objects

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var refreshFab: FloatingActionButton

    //variable for FirebaseAuth class
    private lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        // Find the custom toolbar by its ID
        val toolbar: Toolbar = findViewById(R.id.my_custom_toolbar)

        // Set the toolbar as the support action bar
        setSupportActionBar(toolbar)

        // Customize the toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false) // Disable default title

        // Find logoff and share buttons
        val btnLogoff: ImageButton = findViewById(R.id.btnLogoff)
        val btnShare: ImageButton = findViewById(R.id.btnShare)

        // Set click listeners for buttons
        btnLogoff.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Logout")
            alertDialogBuilder.setMessage("Are you sure you want to Logout?")
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                val user = User(this)
                user.removeUser()
                mAuth.signOut()
                finish()
            }
            alertDialogBuilder.setNegativeButton("No") { dialog, which ->
                dialog.cancel()
            }

            alertDialogBuilder.create().show()

        }

        btnShare.setOnClickListener {
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            val shareBody = "Suraksha: Personal Safety App    \nDownload our App:- "
            val shareSub = "Suraksha: Personal Safety App"
            myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(myIntent, "Share App"))

        }

        val navView: BottomNavigationView = binding.navView


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_setting, R.id.navigation_about
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        refreshFab = binding.fab
        refreshFab.setOnClickListener(View.OnClickListener {
            Snackbar.make(it, "Refreshing", Snackbar.LENGTH_LONG).setAction("...", null).show()
            navController.navigate(R.id.navigation_home)
        })

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }


    private fun showExitDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Exit Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to exit?")
        alertDialogBuilder.setCancelable(false)

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            finish()
        }

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }

        alertDialogBuilder.create().show()
    }




}