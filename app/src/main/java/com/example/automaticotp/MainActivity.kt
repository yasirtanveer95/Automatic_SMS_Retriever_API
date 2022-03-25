package com.example.automaticotp

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        /* Change to Any Language*/
//        Locale.setDefault(Locale("ur", "PK"))
//        resources.configuration.setLocale(Locale("ur", "PK"))
//        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
//        //applicationContext.createConfigurationContext(resources.configuration)
//        /*  ............... */
        setContentView(R.layout.activity_main)
        /*this will also create a Release Hash key*/
        val appSignatureHashHelper = AppSignatureHashHelper(this)
        Log.i("TAG", "HashKey=" + appSignatureHashHelper?.appSignatures[0])
        et_otp.text =
            Editable.Factory.getInstance().newEditable("HashKey: " + appSignatureHashHelper?.appSignatures[0])
        btn_start.setOnClickListener {
            startlistener()
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(updateBaseContextLocale(context))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val locale = Locale("ur")
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResourcesLocale(context, locale)
            return updateResourcesLocaleLegacy(context, locale)
        }
        return updateResourcesLocaleLegacy(context, locale)
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResourcesLocale(context: Context, locale: Locale): Context? {
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context? {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.getConfiguration()
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.getDisplayMetrics())
        return context
    }

    private fun startlistener() {
// Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        val client = SmsRetriever.getClient(this /* context */)

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task: Task<Void> = client.startSmsRetriever()

// Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);

// Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(OnSuccessListener<Void?> {
            Toast.makeText(this, "Task Started", Toast.LENGTH_SHORT).show()
            // Successfully started retriever, expect broadcast intent
            // ...
        })

        task.addOnFailureListener(OnFailureListener {
            Toast.makeText(this, "Task Failed", Toast.LENGTH_SHORT).show()
            // Failed to start retriever, inspect Exception for more details
            // ...
        })
    }


    /**
     * BroadcastReceiver to wait for SMS messages. This can be registered either
     * in the AndroidManifest or at runtime.  Should filter Intents on
     * SmsRetriever.SMS_RETRIEVED_ACTION.
     */
}

class MySMSBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS ->           // Get SMS message contents
                {
                    val message: String? = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                CommonStatusCodes.TIMEOUT -> {
                    Toast.makeText(context, "timeout", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}