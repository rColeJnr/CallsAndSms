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
import com.rick.callssms.databinding.FragmentCallLogBinding

class CallLogFragment : Fragment() {

    private var _binding: FragmentCallLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var callLogAdapter: CallLogAdapter

    private val viewModel: CommunicationViewModel by activityViewModels()

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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = callLogAdapter
        }

        mainActivity.getCallLogs()

        viewModel.callLog.observe(viewLifecycleOwner){ logs ->
            logs?.let {
                // take most recent 10 calls
                callLogAdapter.callLog = it.take(10)
                callLogAdapter.notifyDataSetChanged()
            }
        }
    }

}

