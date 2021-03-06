package com.rick.callssms

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.Telephony
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rick.callssms.databinding.ActivityMainBinding
import com.rick.callssms.ui.CallLogEvent
import com.rick.callssms.ui.CommunicationViewModel
import com.rick.callssms.ui.SMS
import com.rick.callssms.ui.ViewSMS
import layout.SendSMS

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val communicationViewModel: CommunicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerReceiver(broadcastReceiver, IntentFilter("SMS_RECEIVED"))

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.phoneFragment, R.id.callLogFragment, R.id.smsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCallLogs()
            READ_SMS_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getTexts()

        }
    }

    fun callNumber(number: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Intent(Intent.ACTION_CALL).also {
                it.data = Uri.parse("tel:$number")
                startActivity(it)
            }
        } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 0)
    }

    fun getCallLogs() {
        val readStoragePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val readCallLogPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)

        if (readStoragePermission != PackageManager.PERMISSION_GRANTED || readCallLogPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CALL_LOG
                ),
                PERMISSIONS_REQUEST_CODE
            )
            return
        }

        val cursor = application.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE
        )

        val callLog = mutableListOf<CallLogEvent>()

        cursor?.use {
            val number = it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val type = it.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val date = it.getColumnIndexOrThrow(CallLog.Calls.DATE)
            while (it.moveToNext()) {
                val phoneNumber = cursor.getString(number)
                val callType = cursor.getString(type)
                //TODO: fix call date
//                val callDate = cursor.getString(date)
                val callDateString = date.toString()
                val direction = when (callType.toInt()) {
                    CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> "MISSED"
                    else -> null
                }
                val entry = CallLogEvent(direction, phoneNumber, callDateString)
                callLog.add(entry)
            }
        }
        cursor?.close()

        communicationViewModel.callLog.value = callLog
    }

    fun showCallLogPopup(view: View, phoneNumber: String) {
        PopupMenu(this, view).apply {
            inflate(R.menu.call_log)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.make_call -> {
                        callNumber(phoneNumber)
                        true
                    }
                    R.id.send_sms -> {
                        openDialog(SendSMS(phoneNumber))
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
            show()
        }
    }

    fun openDialog(dialog: DialogFragment) =
        dialog.show(supportFragmentManager, "")

    fun getTexts() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                READ_SMS_REQUEST_CODE
            )
            return
        }
        val cursor = application.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            "date DESC"
        )
        val texts = mutableListOf<SMS>()

        cursor?.use {
            val senderColumn = it.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS)
            val bodyColumn = it.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY)

            while (it.moveToNext()) {
                val sender = it.getString(senderColumn) ?: getString(R.string.error_sender)
                val body = it.getString(bodyColumn) ?: getString(R.string.error_body)
                val sms = SMS(sender, body)
                texts.add(sms)
            }
        }
        cursor?.close()
        communicationViewModel.texts.value = texts.take(20)
    }

    fun showSMSPopup(view: View, text: SMS) {
        PopupMenu(this, view).apply {
            inflate(R.menu.sms)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.viewMessage -> {
                        openDialog(ViewSMS(text))
                        true
                    }
                    R.id.reply -> {
                        openDialog(SendSMS(text.sender))
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }

    fun sendSMS(number: String, message: String): Boolean {
        if (number.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_sending_sms), Toast.LENGTH_LONG).show()
            return false
        }
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val smsManager = SmsManager.getDefault()
            if (message.length > 160) {
                val messages: ArrayList<String> = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(number, null, messages, null, null)
            } else smsManager.sendTextMessage(number, null, message, null, null)
            true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 0)
            false
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            getTexts()
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
        const val READ_SMS_REQUEST_CODE = 2
    }
}