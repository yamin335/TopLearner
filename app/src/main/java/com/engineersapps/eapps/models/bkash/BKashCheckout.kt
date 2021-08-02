package com.engineersapps.eapps.models.bkash

import java.io.Serializable

data class BKashCheckout(var amount: String, var intent: String, var version: String): Serializable
