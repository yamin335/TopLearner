package com.rtchubs.engineerbooks.repos

import com.rtchubs.engineerbooks.api.AdminApiService
import com.rtchubs.engineerbooks.api.ApiService
import com.rtchubs.engineerbooks.models.AdSliderResponse
import com.rtchubs.engineerbooks.models.registration.ProfileImageUploadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(private val adminApiService: AdminApiService, private val apiService: ApiService) {

    suspend fun uploadProfilePhotosRepo(requestBody: RequestBody): Response<ProfileImageUploadResponse> {
        return withContext(Dispatchers.IO) {
            adminApiService.uploadProfilePhotos(requestBody)
        }
    }

    suspend fun getAdsRepo(): Response<AdSliderResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getAds()
        }
    }
}