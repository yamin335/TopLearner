package com.engineersapps.eapps.repos

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.engineersapps.eapps.api.AdminApiService
import com.engineersapps.eapps.api.ApiService
import com.engineersapps.eapps.models.LiveClassScheduleResponse
import com.engineersapps.eapps.models.OfferResponse
import com.engineersapps.eapps.models.chapter.ChapterResponse
import com.engineersapps.eapps.models.faq.AllFaqResponse
import com.engineersapps.eapps.models.home.ClassWiseBookResponse
import com.engineersapps.eapps.models.home.CourseCategoryResponse
import com.engineersapps.eapps.models.my_course.MyCourseListRequest
import com.engineersapps.eapps.models.my_course.MyCourseListResponse
import com.engineersapps.eapps.models.my_course.SingleBookResponse
import com.engineersapps.eapps.models.notice_board.NoticeResponse
import com.engineersapps.eapps.models.payment_account_models.AddCardOrBankResponse
import com.engineersapps.eapps.models.payment_account_models.BankOrCardListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(@Named("auth") private val authApiService: ApiService,
                                         private val apiService: ApiService, private val adminApiService: AdminApiService) {
    
    suspend fun allBookRepo(mobile: String, class_id: Int): Response<ClassWiseBookResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("mobile", mobile)
            addProperty("class_id", class_id)
        }.toString()

        return withContext(Dispatchers.IO) {
            authApiService.getBooks(jsonObjectBody)
        }
    }

    suspend fun allFreeBookRepo(): Response<ClassWiseBookResponse> {
        return withContext(Dispatchers.IO) {
            authApiService.getAllFreeBooks()
        }
    }

    suspend fun singleBookRepo(bookId: Int?): Response<SingleBookResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("id", bookId)
        }.toString()

        return withContext(Dispatchers.IO) {
            authApiService.getBook(jsonObjectBody)
        }
    }

//    suspend fun adminPanelBookRepo(): Response<AllBookResponse> {
//
//        return withContext(Dispatchers.IO) {
//            adminApiService.getAllBooks()
//        }
//    }

    suspend fun allCourseRepo(): Response<CourseCategoryResponse> {
        return withContext(Dispatchers.IO) {
            authApiService.getAllCourse()
        }
    }

    suspend fun myCoursesRepo(request: MyCourseListRequest): Response<MyCourseListResponse> {
        return withContext(Dispatchers.IO) {
            authApiService.getAllMyCourses(Gson().toJson(request))
        }
    }

    suspend fun allFaqRepo(): Response<AllFaqResponse> {
        return withContext(Dispatchers.IO) {
            authApiService.getAllFaqs()
        }
    }

    suspend fun requestBankListRepo(type:String,token:String): Response<BankOrCardListResponse> {
        return withContext(Dispatchers.IO) {
            apiService.requestBankList(type,token)
        }
    }

    suspend fun addBankRepo(bankId: Int, accountNumber: String, token: String): Response<AddCardOrBankResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("bankId", bankId)
            addProperty("accountNumber", accountNumber)
        }

        return withContext(Dispatchers.IO) {
            apiService.addBankAccount(jsonObjectBody, token)
        }
    }

    suspend fun addCardRepo(bankId: Int, cardNumber: String, expireMonth: Int, expireYear: Int, token: String): Response<AddCardOrBankResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("bankId", bankId)
            addProperty("cardNumber", cardNumber)
            addProperty("expireMonth", expireMonth)
            addProperty("expireYear", expireYear)
        }

        return withContext(Dispatchers.IO) {
            apiService.addCardAccount(jsonObjectBody, token)
        }
    }

    suspend fun noticeRepo(mobile: String?): Response<NoticeResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("mobile", mobile)
        }

        return withContext(Dispatchers.IO) {
            authApiService.getNotices(jsonObjectBody.toString())
        }
    }

    suspend fun offerRepo(cityId: Int?, upazillaId: Int?): Response<OfferResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("city_id", cityId)
            addProperty("upazila_id", upazillaId)
        }

        return withContext(Dispatchers.IO) {
            authApiService.getOffers(jsonObjectBody.toString())
        }
    }

    suspend fun getChaptersRepo(bookID: Int?): Response<ChapterResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("book_id", bookID)
        }
        return withContext(Dispatchers.IO) {
            authApiService.getChapters(jsonObjectBody.toString())
        }
    }

    suspend fun liveClassScheduleRepo(classID: Int?): Response<LiveClassScheduleResponse> {
        val jsonObjectBody = JsonObject().apply {
            addProperty("class_id", classID)
        }
        return withContext(Dispatchers.IO) {
            authApiService.getClassSchedule(jsonObjectBody.toString())
        }
    }
}