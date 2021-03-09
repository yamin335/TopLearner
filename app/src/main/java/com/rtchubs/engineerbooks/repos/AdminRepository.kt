package com.rtchubs.engineerbooks.repos

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.AdminApiService
import com.rtchubs.engineerbooks.models.transactions.PartnerTransactionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(private val adminApiService: AdminApiService) {

    suspend fun adminTransactionsRepo(mobileNumber: String): Response<PartnerTransactionResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("partnermobile", mobileNumber)
        }

        return withContext(Dispatchers.IO) {
            adminApiService.partnerTransactionHistory(jsonObject)
        }
    }
}