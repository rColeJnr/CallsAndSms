package com.rick.callssms.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rick.callssms.MainActivity
import com.rick.callssms.R

class CallLogAdapter(activity: MainActivity
): RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    inner class CallLogViewHolder(view: View): RecyclerView.ViewHolder(view){
        internal var direction = view.findViewById<ImageView>(R.id.callDirection)
        internal var phoneNumber = view.findViewById<TextView>(R.id.number)
        internal var callDate = view.findViewById<TextView>(R.id.date)
        internal var callBack = view.findViewById<ImageButton>(R.id.callBack)

        init {
            view.setOnLongClickListener {
                //TODO
                return@setOnLongClickListener true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        return CallLogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_call_log, parent, false))
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}
