package com.mr.suraksha.ui.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mr.suraksha.EmergencyContact
import com.mr.suraksha.R
import com.mr.suraksha.SplashActivity
import com.mr.suraksha.User
import com.mr.suraksha.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private lateinit var  btnsubmit: Button
    private lateinit var edtNum1: EditText
    private lateinit var edtNum2: EditText
    private lateinit var txt_name1: TextView
    private lateinit var txt_name2: TextView
    val RequestPermissionCode = 1
    data class ContactInfo(val name: String, val number: String)

    private lateinit var context: Context
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

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        context = requireContext()

        edtNum1 = binding.edtContact1
        edtNum2 = binding.edtContact2
        txt_name1 = binding.txtName1
        txt_name2 = binding.txtName2

        btnsubmit = binding.btnSubmit

        edtNum1.setOnClickListener(View.OnClickListener {
            pickContact1.launch(null)
        })
        edtNum2.setOnClickListener(View.OnClickListener {
            pickContact2.launch(null)
        })

        var user = User(context)


        if (!user.contact1.equals("") && !user.contact2.equals("")){
            edtNum1.setText(user.contact1)
            edtNum2.setText(user.contact2)
        }
        EnableRuntimePermission()

        btnsubmit.setOnClickListener {
            val phone1 = edtNum1.text.toString()
            val phone2 = edtNum2.text.toString()
            val name1 = txt_name1.text.toString()
            val name2 = txt_name2.text.toString()
            if (!TextUtils.isEmpty(phone1) && !TextUtils.isEmpty(phone2)) {
                var user = User(context)
                user.contact1 = phone1.trim { it <= ' ' }
                user.contact2 = phone2.trim { it <= ' ' }
                user.name1 = name1.trim { it <= ' ' }
                user.name2 = name2.trim { it <= ' ' }
                // Use the NavController to navigate to the DashboardFragment
                findNavController().navigate(R.id.action_navigation_setting_to_navigation_home)
            } else {
                Toast.makeText(context, "Please enter the details", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }


    fun EnableRuntimePermission() {
        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            requestPermission()
        }
    }
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(context,
            Manifest.permission.READ_CONTACTS
        )
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(context as Activity, arrayOf<String>(Manifest.permission.READ_CONTACTS), RequestPermissionCode)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(context, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showMessageOKCancel("You need to allow access to both the permissions") { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.SEND_SMS
                                ), RequestPermissionCode)
                            }
                        }
                        return
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun displayContactInfo(contactUri: android.net.Uri): EmergencyContact.ContactInfo? {

        val cursor = context.contentResolver.query(contactUri, null, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhoneNumber > 0) {
                    val cursor2 = context.contentResolver.query(
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
                            return EmergencyContact.ContactInfo(contactName, number)
                        }
                    }
                }
            }
        }

        return null
    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}