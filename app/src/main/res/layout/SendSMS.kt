package layout

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.callssms.MainActivity
import com.rick.callssms.databinding.SendSmsBinding

class SendSMS(private val number: String?) : DialogFragment() {

    private var _binding: SendSmsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mActivity = activity as MainActivity
        val inflater = mActivity.layoutInflater
        _binding = SendSmsBinding.inflate(inflater)
        val builder = AlertDialog.Builder(mActivity)
            .setView(binding.root)
        if (number != null){
            val editable: Editable = SpannableStringBuilder(number)
            binding.number.text = editable
        }
        binding.also {
            it.cancelBtn.setOnClickListener {
                if (mActivity.sendSMS(binding.number.text.toString(), binding.body.text.toString())) dismiss()
            }
            return builder.create()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}