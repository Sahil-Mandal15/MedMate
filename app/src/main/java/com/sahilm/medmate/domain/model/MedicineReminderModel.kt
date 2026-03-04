package com.sahilm.medmate.domain.model

import java.io.Serializable

data class MedicineReminderModel(
    var id: Int = 0,
    var medicineName: String,
    var description: String,
    var time: String,
    var date: String,
    var repeatDaily: Boolean = false,
    var isTaken: Boolean = false,
) : Serializable
