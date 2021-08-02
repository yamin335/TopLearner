package com.engineersapps.eapps.repos

import com.engineersapps.eapps.api.ApiService
import com.engineersapps.eapps.models.common.MyAccountListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopUpRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun myAccountListRepo(token:String): Response<MyAccountListResponse> {
        return withContext(Dispatchers.IO) {
            apiService.myAccountList(token)
        }
    }

//    suspend fun addBankRepo(bankId: Int, accountNumber: String, token: String): Response<AddCardOrBankResponse> {
//        val jsonObjectBody = JsonObject().apply {
//            addProperty("bankId", bankId)
//            addProperty("accountNumber", accountNumber)
//        }
//
//        return withContext(Dispatchers.IO) {
//            apiService.addBankAccount(jsonObjectBody, token)
//        }
//    }
//
//    suspend fun addCardRepo(bankId: Int, cardNumber: String, expireMonth: Int, expireYear: Int, token: String): Response<AddCardOrBankResponse> {
//        val jsonObjectBody = JsonObject().apply {
//            addProperty("bankId", bankId)
//            addProperty("cardNumber", cardNumber)
//            addProperty("expireMonth", expireMonth)
//            addProperty("expireYear", expireYear)
//        }
//
//        return withContext(Dispatchers.IO) {
//            apiService.addCardAccount(jsonObjectBody, token)
//        }
//    }
}