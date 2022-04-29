package com.rick.callssms.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rick.callssms.MainActivity
import com.rick.callssms.R

class SMSAdapter (private val activity: MainActivity) : RecyclerView.Adapter<SMSAdapter.SMSViewHolder>(){

    var texts = listOf<SMS>()

    inner class SMSViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        internal var sender = view.findViewById<TextView>(R.id.sender)
        internal var body = view.findViewById<TextView>(R.id.body)

        init {
            view.isClickable = true
            view.setOnClickListener(this)
            view.setOnLongClickListener {
                //TODO. open sms options
                return@setOnLongClickListener true
            }
        }

        override fun onClick(p0: View?) {
            activity.openDialog(ViewSMS(texts(adapterPosition)))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder {
        return SMSViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sms_entry, parent, false))
    }

    override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
        val current = texts[position]

        holder.sender.text = current.sender
        holder.body.text = current.body
    }

    override fun getItemCount(): Int = texts.size
}