package com.rtchubs.engineerbooks.repos

import com.rtchubs.engineerbooks.api.AdminApiService
import com.rtchubs.engineerbooks.models.chapter.ChapterResponse
import com.rtchubs.engineerbooks.models.registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(private val adminApiService: AdminApiService) {

    suspend fun uploadProfilePhotosRepo(requestBody: RequestBody): Response<ProfileImageUploadResponse> {
        return withContext(Dispatchers.IO) {
            adminApiService.uploadProfilePhotos(requestBody)
        }
    }

    suspend fun getChaptersRepo(bookID: String?): Response<ChapterResponse> {
        return withContext(Dispatchers.IO) {
            adminApiService.getChapters(bookID)
        }
    }
}