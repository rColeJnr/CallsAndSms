package com.rick.callssms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.widget.Toast

class SMSBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle["pdus"] as Array<*>? // Protocao data units,binary data blocks for SMS
                val format = bundle.getString("format")
                val messages: Array<SmsMessage?> = arrayOfNulls(pdus!!.size)

                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                    if (messages.isNotEmpty()) Toast.makeText(
                        context,
                        context.getString(R.string.new_sms_received, messages[0]?.messageBody),
                        Toast.LENGTH_SHORT
                    ).show()
                    context.sendBroadcast(Intent("SMS_RECEIVED"))
                }
            }
        }
    }

}