package com.rtchubs.engineerbooks.api

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.Api.ContentType
import com.rtchubs.engineerbooks.models.common.MyAccountListResponse
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.home.ClassWiseBookResponse
import com.rtchubs.engineerbooks.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.engineerbooks.models.payment_account_models.BankOrCardListResponse
import com.rtchubs.engineerbooks.models.registration.*
import com.rtchubs.engineerbooks.models.transactions.PayInvoiceResponse
import com.rtchubs.engineerbooks.models.transactions.TransactionHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface MediaApiService {

    @POST(ApiEndPoint.UPLOAD_PHOTOES)
    suspend fun uploadProfilePhotos(@Body partFormData: RequestBody): Response<ProfileImageUploadResponse>

}
