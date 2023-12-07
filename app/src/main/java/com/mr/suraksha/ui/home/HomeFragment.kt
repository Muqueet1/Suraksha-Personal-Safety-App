package com.mr.suraksha.ui.home

import android.Manifest
import com.mr.suraksha.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mr.suraksha.User
import com.mr.suraksha.databinding.FragmentHomeBinding
import java.util.Locale

class HomeFragment : Fragment() {

    private var homeBinding: FragmentHomeBinding? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var context: Context
    private lateinit var txtLatitude: TextView
    private lateinit var txtLongitude: TextView
    private lateinit var txtAddrress: TextView
    private lateinit var txtContact1: TextView
    private lateinit var txtContact2: TextView
    private lateinit var txtName1: TextView
    private lateinit var txtName2: TextView
    private lateinit var imgback: ImageView
    private lateinit var btnPanic: ImageButton
    private lateinit var link:String
    private lateinit var MyNumber:String
    private lateinit var Contact1: String
    private lateinit var Contact2: String
    private lateinit var Name1: String
    private lateinit var Name2: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = homeBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        context = requireContext()
        imgback = binding.imgloc
        txtLatitude = binding.textLatitude
        txtLongitude = binding.textLongitude
        txtAddrress = binding.textAddress
        txtName1 = binding.textName1
        txtName2 = binding.textName2
        txtContact1 = binding.textContact1
        txtContact2 = binding.textContact2

        // Inflate the layout for this fragment
        val user = User(context)
        MyNumber = user.mobile
        Contact1 = user.contact1
        Contact2 = user.contact2
        Name1 = user.name1
        Name2 = user.name2

        if (Contact1.equals("") || Contact2.equals("")){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Alert")
            builder.setMessage("Please Select your trusted contacts!")
            builder.setIcon(R.drawable.baseline_info_24)
            builder.setPositiveButton("OK") { dialog, which ->
                // Use the NavController to navigate to the DashboardFragment
                findNavController().navigate(R.id.action_navigation_home_to_navigation_setting)
            }
            // Create and show the dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        txtName1.setText(Name1)
        txtContact1.setText(Contact1)

        txtName2.setText(Name2)
        txtContact2.setText(Contact2)

        //Code Here
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        getLocation()



        btnPanic = binding.btnPanic

////////////////////////Location Code End Here////////////////////////////////////
        btnPanic.setOnLongClickListener(OnLongClickListener {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(1000) // 5000 miliseconds = 5 seconds
            sendSMSMessage()
            true
        })

        return root
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        var longitude = location.longitude
                        var latitude = location.latitude
                        link = ("https://www.google.com/maps/place/"
                                + latitude + ","
                                + longitude + "/@"
                                + latitude + ","
                                + longitude + ",17z/data=!3m1!4b1")

                        val geocoder = Geocoder(context, Locale.getDefault())
                        val list: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        homeBinding.apply {
                            txtLatitude.text = "${list!![0].latitude}"
                            txtLongitude.text = "${list!![0].longitude}"
                            txtAddrress.text = "${list[0].getAddressLine(0)}"



                            /*tvLatitude.text = "Latitude\n${list[0].latitude}"
                            tvLongitude.text = "Longitude\n${list[0].longitude}"
                            tvCountryName.text = "Country Name\n${list[0].countryName}"
                            tvLocality.text = "Locality\n${list[0].locality}"
                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"*/
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Please turn on loc", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

      fun sendSMSMessage(){
        var phoneNumber = arrayOf(Contact1, Contact2)
        var msg1 = "Hi I am in trouble.\n Please Help me.\n" + txtAddrress.text.toString()
        var msg2 = "My Current Location:$link"

        try {
            // on below line we are initializing sms manager.
            //as after android 10 the getDefault function no longer works
            //so we have to check that if our android version is greater
            //than or equal toandroid version 6.0 i.e SDK 23
            val smsManager:SmsManager = SmsManager.getDefault()

             //Toast.makeText(context, MyNumber+"mera", Toast.LENGTH_LONG).show()
            for (number in phoneNumber){
                // on below line we are sending text message.
                smsManager.sendTextMessage(number, MyNumber, msg1, null, null)
                smsManager.sendTextMessage(number, MyNumber, msg2, null,null)
                // on below line we are displaying a toast message for message send,
                Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            // on catch block we are displaying toast message for error.
            Toast.makeText(context, "Please enter all the data.."+e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        homeBinding = null
    }

}