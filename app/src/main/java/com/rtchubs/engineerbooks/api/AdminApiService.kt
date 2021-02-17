package com.rtchubs.engineerbooks.api

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.models.LiveClassScheduleResponse
import com.rtchubs.engineerbooks.models.chapter.ChapterResponse
import com.rtchubs.engineerbooks.models.home.AllBookResponse
import com.rtchubs.engineerbooks.models.notice_board.NoticeResponse
import com.rtchubs.engineerbooks.models.registration.ProfileImageUploadResponse
import com.rtchubs.engineerbooks.models.transactions.PartnerTransactionResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @GET(ApiEndPoint.NOTICES)
    suspend fun getAllNotices(): Response<NoticeResponse>

    @GET(ApiEndPoint.CLASS_SCHEDULE)
    suspend fun getClassSchedule(@Path("classTypeID") classTypeID: Int?): Response<LiveClassScheduleResponse>

    @POST(ApiEndPoint.PARTNER_TRANSACTION)
    suspend fun partnerTransactionHistory(@Body jsonString: JsonObject): Response<PartnerTransactionResponse>

//    @POST(ApiEndPoint.ADMIN_TRANSACTION)
//    suspend fun adminTransactionHistory(@Body jsonString: String): Response<AdminPayHistoryResponse>
}
