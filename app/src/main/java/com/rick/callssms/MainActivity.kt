package com.rick.callssms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rick.callssms.databinding.ActivityMainBinding
import com.rick.callssms.ui.CallLogEvent
import com.rick.callssms.ui.CommunicationViewModel
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val communicationViewModel: CommunicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.phoneFragment, R.id.callLogFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun callNumber(number: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_CALL).also {
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
                val callDate = cursor.getString(date)
                val callDateString = LocalDateTime.now()
                val direction = when (callType.toInt()) {
                    CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> "MISSED"
                    else -> null
                }
                val entry = CallLogEvent(direction, phoneNumber, callDateString.toString())
                callLog.add(entry)
            }
        }
        cursor?.close()

        communicationViewModel.callLog.value = callLog
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCallLogs()
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
    }
}