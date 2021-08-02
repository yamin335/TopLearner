package com.engineersapps.eapps.api

import com.engineersapps.eapps.models.registration.ProfileImageUploadResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * REST API access points
 */
interface AdminApiService {
    @POST(ApiEndPoint.UPLOAD_PHOTOES)
    suspend fun uploadProfilePhotos(@Body partFormData: RequestBody): Response<ProfileImageUploadResponse>

//    @GET(ApiEndPoint.ALL_BOOKS)
//    suspend fun getAllBooks(): Response<AllBookResponse>
//
//    @POST(ApiEndPoint.ALL_COURSE)
//    suspend fun getAllCourse(): Response<CourseCategoryResponse>
}
