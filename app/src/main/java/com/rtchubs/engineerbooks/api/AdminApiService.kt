package com.rtchubs.engineerbooks.api

import com.rtchubs.engineerbooks.models.chapter.ChapterResponse
import com.rtchubs.engineerbooks.models.home.AllBookResponse
import com.rtchubs.engineerbooks.models.notice_board.NoticeResponse
import com.rtchubs.engineerbooks.models.registration.ProfileImageUploadResponse
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

//    @POST(ApiEndPoint.ADMIN_TRANSACTION)
//    suspend fun adminTransactionHistory(@Body jsonString: String): Response<AdminPayHistoryResponse>
}
