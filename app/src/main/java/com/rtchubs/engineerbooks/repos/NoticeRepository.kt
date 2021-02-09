package com.rtchubs.engineerbooks.repos

import com.rtchubs.engineerbooks.api.AdminApiService
import com.rtchubs.engineerbooks.models.notice_board.NoticeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepository @Inject constructor(private val adminApiService: AdminApiService) {

    suspend fun noticeRepo(): Response<NoticeResponse> {

        return withContext(Dispatchers.IO) {
            adminApiService.getAllNotices()
        }
    }
}