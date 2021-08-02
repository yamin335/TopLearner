package com.engineersapps.eapps.repos

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.engineersapps.eapps.api.ApiService
import com.engineersapps.eapps.models.bkash.BKashPaymentUrlResponse
import com.engineersapps.eapps.models.payment.CoursePaymentRequest
import com.engineersapps.eapps.models.payment.CoursePaymentResponse
import com.engineersapps.eapps.models.payment.PromoCodeResponse
import com.engineersapps.eapps.models.transactions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun purchaseCourseRepo(request: CoursePaymentRequest): Response<CoursePaymentResponse> {
        val jsonObject = Gson().toJson(request) ?: ""

        return withContext(Dispatchers.IO) {
            authApiService.purchaseCourse(jsonObject)
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

    suspend fun promoCodeRepo(promoCode: String?): Response<PromoCodeResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("code", promoCode)
        }.toString()

        return withContext(Dispatchers.IO) {
            authApiService.verifyPromoCode(jsonObject)
        }
    }

    suspend fun partnerTransactionsRepo(mobileNumber: String): Response<PartnerTransactionResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("partner_mobile", mobileNumber)
        }

        return withContext(Dispatchers.IO) {
            authApiService.partnerTransactionHistory(jsonObject)
        }
    }

    suspend fun partnerPaymentStatusRepo(mobileNumber: String?, cityID: Int?, upazillaID: Int?): Response<PaymentStatusResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("partner_mobile", mobileNumber)
            addProperty("city_id", cityID)
            addProperty("upazila_id", upazillaID)
        }

        return withContext(Dispatchers.IO) {
            authApiService.partnerPaymentStatus(jsonObject)
        }
    }

    suspend fun bkashPaymentUrlRepo(mobile: String?, amount: String?, invoiceNumber: String?): Response<BKashPaymentUrlResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("mobile", mobile)
            addProperty("amount", amount)
            addProperty("invoicenumber", invoiceNumber)
        }

        return withContext(Dispatchers.IO) {
            authApiService.bkashPaymentUrl(jsonObject)
        }
    }
}