package com.rtchubs.engineerbooks.models.bkash

import java.io.Serializable

data class BKashPaymentResponse(var paymentID: String, var transactionID: String, var amount: String): Serializable
