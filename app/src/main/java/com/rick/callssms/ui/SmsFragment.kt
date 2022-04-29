package com.rick.callssms.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rick.callssms.MainActivity
import com.rick.callssms.databinding.FragmentSmsBinding
import layout.SendSMS

class SmsFragment: Fragment() {

    private var _binding: FragmentSmsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommunicationViewModel by activityViewModels()
    private lateinit var mActivity: MainActivity
    private lateinit var smsAdapter: SMSAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSmsBinding.inflate(inflater, container, false)
        mActivity = activity as MainActivity

        smsAdapter = SMSAdapter(mActivity)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.callLog.layoutManager = LinearLayoutManager(activity)
        binding.callLog.adapter = smsAdapter

        mActivity.getTexts()

        viewModel.texts.observe(viewLifecycleOwner){ texts ->
            texts?.let {
                smsAdapter.texts = it
                smsAdapter.notifyDataSetChanged()
            }
        }

        binding.fab.setOnClickListener {
            mActivity.openDialog(SendSMS(null))
        }
    }
}

