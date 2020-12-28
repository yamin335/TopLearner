package com.rtchubs.engineerbooks.repos

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.ApiService
import com.rtchubs.engineerbooks.models.home.ClassWiseBookResponse
import com.rtchubs.engineerbooks.models.registration.*
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.models.transactions.PayInvoiceResponse
import com.rtchubs.engineerbooks.models.transactions.TransactionHistoryResponse
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(@Named("auth") private val authApiService: ApiService) {

    suspend fun createOrderRepo(createOrderBody: CreateOrderBody): Response<PayInvoiceResponse> {
        val jsonObject = Gson().toJson(createOrderBody) ?: ""

        return withContext(Dispatchers.IO) {
            authApiService.createOrder(jsonObject)
        }
    }

    suspend fun transactionsRepo(studentID: Int): Response<TransactionHistoryResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("StudentID", studentID)
        }.toString()

        return withContext(Dispatchers.IO) {
            authApiService.transactionHistory(jsonObject)
        }
    }
}