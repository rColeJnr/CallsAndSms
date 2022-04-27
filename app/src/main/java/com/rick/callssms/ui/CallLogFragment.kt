package com.rick.callssms.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rick.callssms.MainActivity
import com.rick.callssms.databinding.FragmentCallLogBinding

class CallLogFragment: Fragment() {

    private var _binding: FragmentCallLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var callLogAdapter: CallLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCallLogBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        callLogAdapter = CallLogAdapter(mainActivity)

        return binding.root
    }

}

