package com.rtchubs.engineerbooks.models.bkash

import java.io.Serializable

data class BKashCheckout(var amount: String, var intent: String, var version: String): Serializable
