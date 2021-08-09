package com.engineersapps.eapps.ui

import android.app.Activity
import android.content.*
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.engineersapps.eapps.R
import com.engineersapps.eapps.ui.otp_signin.OtpSignInFragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import dagger.android.support.DaggerAppCompatActivity

interface OTPHandlerCallback {
    fun onStartOTPListener()
}

class LoginActivity : DaggerAppCompatActivity(), LoginHandlerCallback, OTPHandlerCallback, NavigationHost {
    private val SMS_CONSENT_REQUEST = 2

    // Set to an unused request code
    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get consent intent
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
                        } catch (e: ActivityNotFoundException) {
                            // Handle the exception ...
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // ...
            SMS_CONSENT_REQUEST ->
                // Obtain the phone number from the result
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Get SMS message content
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    // Extract one-time code from the message and complete verification
                    // `message` contains the entire text of the SMS message, so you will need
                    // to parse the string.
                    val oneTimeCode = parseOneTimeCode(message) // define this function

                    //for this demo we will display it instead
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

                    val otpSignInFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment as? OtpSignInFragment
                    otpSignInFragment?.updateOTP(oneTimeCode)
                } else {
                    // Consent denied. User can type OTC manually.
                }
        }
    }

    private fun smsTask() {
        //Start listening for SMS User Consent broadcasts from senderPhoneNumber
        //The sender number being used was configured in my emulator, you can use your own number
        val task = SmsRetriever.getClient(this).startSmsUserConsent("01708404121")
        task.addOnCompleteListener { listener: Task<Void?> ->
            if (listener.isSuccessful) {
                // Task completed successfully
            } else {
                // Task failed with an exception
                val exception = listener.exception
                exception!!.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null)
    }

    override fun onPause() {
        super.onPause()
        //to prevent IntentReceiver leakage unregister
        unregisterReceiver(smsVerificationReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null)

//        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment)
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            Log.e(TAG, "onDestinationChanged: "+destination.label);
//
//        }

//        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
//        registerReceiver(smsVerificationReceiver, intentFilter)

//        // Start listening for SMS User Consent broadcasts from senderPhoneNumber
//        // The Task<Void> will be successful if SmsRetriever was able to start
//        // SMS User Consent, and will error if there was an error starting.
//        val task = SmsRetriever.getClient(this).startSmsUserConsent("+8801956494964" /* OTP sender mobile number or null */)
//
//        // Listen for success/failure of the start Task. If in a background thread, this
//        // can be made blocking using Tasks.await(task, [timeout]);
//        // Listen for success/failure of the start Task. If in a background thread, this
//        // can be made blocking using Tasks.await(task, [timeout]);
//        task.addOnSuccessListener{
//            val ss = ""
//        }
//
//        task.addOnFailureListener {
//            // Failed to start retriever, inspect Exception for more details
//            // ...
//            val ss = ""
//        }
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))
    }

    override fun onLoggedIn() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
    }

    private fun parseOneTimeCode(message: String?): String? {
        //simple number extractor
        val patternMatcher = "([0-9]){4}".toRegex()
        val result = patternMatcher.find(message ?: "", 0)
        return result?.value
    }

    override fun onStartOTPListener() {
        smsTask()
    }
}