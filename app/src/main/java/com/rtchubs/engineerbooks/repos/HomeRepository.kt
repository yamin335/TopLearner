package com.rtchubs.engineerbooks.repos

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.AdminApiService
import com.rtchubs.engineerbooks.api.ApiService
import com.rtchubs.engineerbooks.models.home.AllBookResponse
import com.rtchubs.engineerbooks.models.home.ClassWiseBookResponse
import com.rtchubs.engineerbooks.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.engineerbooks.models.payment_account_models.BankOrCardListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(@Named("auth") private val authApiService: ApiService,
                                         private val apiService: ApiService, private val adminApiService: AdminApiService) {

    suspend fun allBookRepo(mobile: String, class_id: Int): Response<ClassWiseBookResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("mobile", mobile)
            addProperty("class_id", class_id)
        }.toString()

        return withContext(Dispatchers.IO) {
            authApiService.getBooks(jsonObjectBody)
        }
    }

    suspend fun adminPanelBookRepo(): Response<AllBookResponse> {

        return withContext(Dispatchers.IO) {
            adminApiService.getAllBooks()
        }
    }







    suspend fun requestBankListRepo(type:String,token:String): Response<BankOrCardListResponse> {
        return withContext(Dispatchers.IO) {
            apiService.requestBankList(type,token)
        }
    }

    suspend fun addBankRepo(bankId: Int, accountNumber: String, token: String): Response<AddCardOrBankResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("bankId", bankId)
            addProperty("accountNumber", accountNumber)
        }

        return withContext(Dispatchers.IO) {
            apiService.addBankAccount(jsonObjectBody, token)
        }
    }

    suspend fun addCardRepo(bankId: Int, cardNumber: String, expireMonth: Int, expireYear: Int, token: String): Response<AddCardOrBankResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("bankId", bankId)
            addProperty("cardNumber", cardNumber)
            addProperty("expireMonth", expireMonth)
            addProperty("expireYear", expireYear)
        }

        return withContext(Dispatchers.IO) {
            apiService.addCardAccount(jsonObjectBody, token)
        }
    }
}