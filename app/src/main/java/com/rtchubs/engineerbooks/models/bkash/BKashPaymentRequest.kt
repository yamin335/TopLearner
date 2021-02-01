package com.rtchubs.engineerbooks.models.bkash

import java.io.Serializable

data class BKashPaymentRequest(var amount: String, var intent: String): Serializable
