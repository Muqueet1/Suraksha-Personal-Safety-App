package com.mr.suraksha

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.SEND_SMS
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mr.suraksha.databinding.ActivityEmergencyContactBinding


class EmergencyContact : AppCompatActivity() {
    private lateinit var  btnsubmit: Button
    private lateinit var edtNum1: EditText
    private lateinit var edtNum2:EditText
    private lateinit var txt_name1: TextView
    private lateinit var txt_name2: TextView
    val RequestPermissionCode = 1
    data class ContactInfo(val name: String, val number: String)

    private val pickContact1 =
        registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
            if (contactUri != null) {
                val contactInfo = displayContactInfo(contactUri)
                if (contactInfo != null) {
                    txt_name1.setText(contactInfo.name)
                    edtNum1.setText(contactInfo.number)

                }
            }
        }
    private val pickContact2 =
        registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
            if (contactUri != null) {
                val contactInfo = displayContactInfo(contactUri)
                if (contactInfo != null) {
                    txt_name2.setText(contactInfo.name)
                    edtNum2.setText(contactInfo.number)


                }
            }
        }

    private lateinit var binding: ActivityEmergencyContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        edtNum1 = binding.userPhone1
        edtNum2 = binding.userPhone2
        txt_name1 = binding.txtName1
        txt_name2 = binding.txtName2

        btnsubmit = binding.btnSubmit


        EnableRuntimePermission()

        btnsubmit.setOnClickListener {
            val phone1 = edtNum1.text.toString()
            val phone2 = edtNum2.text.toString()
            val name1 = txt_name1.text.toString()
            val name2 = txt_name2.text.toString()
            if (!TextUtils.isEmpty(phone1) && !TextUtils.isEmpty(phone2)) {
                val intent = Intent(this, SplashActivity::class.java)
                var user = User(this)
                user.contact1 = phone1.trim { it <= ' ' }
                user.contact2 = phone2.trim { it <= ' ' }
                user.name1 = name1.trim { it <= ' ' }
                user.name2 = name2.trim { it <= ' ' }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@EmergencyContact, "Please enter the details", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun SelectContact1(v: View?) {
        pickContact1.launch(null)
    }

    fun SelectContact2(v: View?) {
        pickContact2.launch(null)
    }

    fun EnableRuntimePermission() {
        if (checkPermission()) {
            Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            requestPermission()
        }
    }
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, READ_CONTACTS)
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf<String>(READ_CONTACTS), RequestPermissionCode)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@EmergencyContact, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this@EmergencyContact, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                        showMessageOKCancel("You need to allow access to both the permissions") { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(arrayOf(ACCESS_FINE_LOCATION, SEND_SMS), RequestPermissionCode)
                            }
                        }
                        return
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun displayContactInfo(contactUri: android.net.Uri): ContactInfo? {

        val cursor = contentResolver.query(contactUri, null, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhoneNumber > 0) {
                    val cursor2 = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null,
                        null
                    )

                    cursor2?.use {
                        while (cursor2.moveToNext()) {
                            val number =
                                cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            return ContactInfo(contactName, number)
                        }
                    }
                }
            }
        }

        return null
    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}