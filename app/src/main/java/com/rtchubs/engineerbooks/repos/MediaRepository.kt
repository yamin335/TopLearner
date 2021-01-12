package com.rtchubs.engineerbooks.repos

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.ApiService
import com.rtchubs.engineerbooks.api.MediaApiService
import com.rtchubs.engineerbooks.models.chapter.ChapterResponse
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
class MediaRepository @Inject constructor(private val mediaApiService: MediaApiService) {

    suspend fun uploadProfilePhotosRepo(requestBody: RequestBody): Response<ProfileImageUploadResponse> {
        return withContext(Dispatchers.IO) {
            mediaApiService.uploadProfilePhotos(requestBody)
        }
    }

    suspend fun getChaptersRepo(bookID: String?): Response<ChapterResponse> {
        return withContext(Dispatchers.IO) {
            mediaApiService.getChapters(bookID)
        }
    }
}