package com.rick.callssms.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rick.callssms.MainActivity
import com.rick.callssms.R
import com.rick.callssms.databinding.FragmentPhoneBinding

class PhoneFragment: Fragment() {

    private var _binding: FragmentPhoneBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            backspace.setOnClickListener { removeDigit() }
            zero.setOnClickListener { addDigit("0") }
            one.setOnClickListener { addDigit("1") }
            two.setOnClickListener { addDigit("2") }
            three.setOnClickListener { addDigit("3") }
            four.setOnClickListener { addDigit("4") }
            five.setOnClickListener { addDigit("5") }
            six.setOnClickListener { addDigit("6") }
            seven.setOnClickListener { addDigit("7") }
            eight.setOnClickListener { addDigit("8") }
            nine.setOnClickListener { addDigit("9") }
            star.setOnClickListener { addDigit("*") }
            hash.setOnClickListener { addDigit("#") }
            callBtn.setOnClickListener {
                val number = phoneNumber.text
                if (number.isNotBlank()) mainActivity.callNumber(number.toString())
                else Toast.makeText(mainActivity, getString(R.string.no_number), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addDigit(digit: String) {
        val previousNumber = binding.phoneNumber.text.toString()
        val newNumber = previousNumber + digit
        binding.phoneNumber.text = newNumber
    }

    private fun removeDigit() {
        val previousNubmer = binding.phoneNumber.text.toString()
        if (previousNubmer.isEmpty()) return
        val newNumber = previousNubmer.take(previousNubmer.length - 1)
        binding.phoneNumber.text = newNumber
    }
}