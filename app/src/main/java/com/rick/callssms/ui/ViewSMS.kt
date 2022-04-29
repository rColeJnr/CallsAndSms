package com.rick.callssms.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.callssms.MainActivity
import com.rick.callssms.databinding.ViewSmsBinding
import layout.SendSMS

class ViewSMS(private val sms: SMS): DialogFragment() {

    private var _binding: ViewSmsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mActivity = activity as MainActivity
        val inflater = mActivity.layoutInflater
        _binding = ViewSmsBinding.inflate(inflater)
        val builder = AlertDialog.Builder(mActivity)
            .setView(binding.root)
        binding.apply {
            sender.text = sms.sender
            body.text = sms.body
            cancelBtn.setOnClickListener {
                dismiss()
            }
            replyBtn.setOnClickListener {
                mActivity.openDialog(SendSMS(sms.sender))
                dismiss()
            }
        }
        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}