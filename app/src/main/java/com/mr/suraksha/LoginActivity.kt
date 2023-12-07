package com.mr.suraksha


import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import com.chaos.view.PinView
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mr.suraksha.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    private lateinit var headTextView: TextView
    private lateinit var textU: TextView
    private lateinit var edtMobile: TextInputEditText
    private lateinit var btnGo:Button
    private lateinit var mVerificationId: String
    lateinit var mobile: String
    lateinit var code: String

    var CREDENTIAL_PICKER_REQUEST = 1

    //variable for FirebaseAuth class
    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        headTextView = binding.topText
        edtMobile = binding.edtMobile
        btnGo = binding.btnGo

        btnGo.setOnClickListener {
                mobile = edtMobile.text!!.trim().toString()
                if (mobile.isNotEmpty()) {
                    if (mobile.length == 10) {
                        mobile = "+91$mobile"
                        val options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(mobile)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)

                    } else {
                        Toast.makeText(this, "Please Enter correct Number", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show()

                }
            }
        }


    private fun phoneSelection() {
        // To retrieve the Phone Number hints, first, configure
        // the hint selector dialog by creating a HintRequest object.
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        // Then, pass the HintRequest object to
        // credentialsClient.getHintPickerIntent()
        // to get an intent to prompt the user to
        // choose a phone number.
        val credentialsClient = Credentials.getClient(applicationContext, options)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, Bundle()
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {

            // get data from the dialog which is of type Credential
            val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)

            // set the received data t the text view
            credential?.apply {
                mobile = credential.id
                mobile = credential.id
                val newString = mobile.replace("+91", "")
                edtMobile.setText(newString)

            }

        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show()
        }
    }


private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(this , "Authenticate Successfully" , Toast.LENGTH_SHORT).show()
                sendToMain()
            } else {
                // Sign in failed, display a message and update the UI
                Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
                // Update UI
            }

        }
}

private fun sendToMain(){
    startActivity(Intent(this , SplashActivity::class.java))
    finish()
}
private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        // This callback will be invoked in two situations:
        // 1 - Instant verification. In some cases the phone number can be instantly
        //     verified without needing to send or enter a verification code.
        // 2 - Auto-retrieval. On some devices Google Play services can automatically
        //     detect the incoming verification SMS and perform verification without
        //     user action.
        signInWithPhoneAuthCredential(credential)
    }

    override fun onVerificationFailed(e: FirebaseException) {
        // This callback is invoked in an invalid request for verification is made,
        // for instance if the the phone number format is not valid.

        if (e is FirebaseAuthInvalidCredentialsException) {
            // Invalid request
            Log.d("TAG", "onVerificationFailed: ${e.toString()}")
        } else if (e is FirebaseTooManyRequestsException) {
            // The SMS quota for the project has been exceeded
            Log.d("TAG", "onVerificationFailed: ${e.toString()}")
        }
    }

    override fun onCodeSent(
        verificationId: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        // The SMS verification code has been sent to the provided phone number, we
        // now need to ask the user to enter the code and then construct a credential
        // by combining the code with a verification ID.
        // Save verification ID and resending token so we can use them later

        val intent = Intent(this@LoginActivity, OTPActivity::class.java)
        intent.putExtra("OTP" , verificationId)
        intent.putExtra("resendToken" , token)
        intent.putExtra("phoneNumber" , mobile)
        startActivity(intent)

    }
}


override fun onStart() {
    super.onStart()
    if (mAuth.currentUser != null){
        startActivity(Intent(this , MainActivity::class.java))
        finish()
    }
}

}