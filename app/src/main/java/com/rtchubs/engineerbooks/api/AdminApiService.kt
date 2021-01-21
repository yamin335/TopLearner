package com.rtchubs.engineerbooks.api

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.Api.ContentType
import com.rtchubs.engineerbooks.models.chapter.ChapterResponse
import com.rtchubs.engineerbooks.models.common.MyAccountListResponse
import com.rtchubs.engineerbooks.models.home.AllBookResponse
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.home.ClassWiseBookResponse
import com.rtchubs.engineerbooks.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.engineerbooks.models.payment_account_models.BankOrCardListResponse
import com.rtchubs.engineerbooks.models.registration.*
import com.rtchubs.engineerbooks.models.transactions.AdminPayHistoryResponse
import com.rtchubs.engineerbooks.models.transactions.PayInvoiceResponse
import com.rtchubs.engineerbooks.models.transactions.TransactionHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface AdminApiService {
    @POST(ApiEndPoint.UPLOAD_PHOTOES)
    suspend fun uploadProfilePhotos(@Body partFormData: RequestBody): Response<ProfileImageUploadResponse>

    @GET(ApiEndPoint.CHAPTERS)
    suspend fun getChapters(@Path("bookID") bookID: String?): Response<ChapterResponse>

    @GET(ApiEndPoint.ALL_BOOKS)
    suspend fun getAllBooks(): Response<AllBookResponse>

//    @POST(ApiEndPoint.ADMIN_TRANSACTION)
//    suspend fun adminTransactionHistory(@Body jsonString: String): Response<AdminPayHistoryResponse>
}
