package com.rick.callssms.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommunicationViewModel: ViewModel() {

    var callLog = MutableLiveData<List<CallLogEvent>>()
}