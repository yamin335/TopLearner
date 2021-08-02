package com.engineersapps.eapps.util

import java.util.concurrent.atomic.AtomicInteger

object AtomicNumberGenerator {
    private val numberGenerator = AtomicInteger(0)
    fun getUniqueNumber(): Int {
        return numberGenerator.incrementAndGet()
    }
}