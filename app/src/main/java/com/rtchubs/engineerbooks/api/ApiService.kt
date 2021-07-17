package com.rtchubs.engineerbooks.api

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.Api.ContentType
import com.rtchubs.engineerbooks.models.AdSliderResponse
import com.rtchubs.engineerbooks.models.LiveClassScheduleResponse
import com.rtchubs.engineerbooks.models.OfferResponse
import com.rtchubs.engineerbooks.models.bkash.BKashPaymentUrlResponse
import com.rtchubs.engineerbooks.models.chapter.ChapterResponse
import com.rtchubs.engineerbooks.models.common.MyAccountListResponse
import com.rtchubs.engineerbooks.models.faq.AllFaqResponse
import com.rtchubs.engineerbooks.models.home.ClassWiseBookResponse
import com.rtchubs.engineerbooks.models.home.CourseCategoryResponse
import com.rtchubs.engineerbooks.models.my_course.MyCourseListResponse
import com.rtchubs.engineerbooks.models.my_course.SingleBookResponse
import com.rtchubs.engineerbooks.models.notice_board.NoticeResponse
import com.rtchubs.engineerbooks.models.payment.CoursePaymentResponse
import com.rtchubs.engineerbooks.models.payment.PromoCodeResponse
import com.rtchubs.engineerbooks.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.engineerbooks.models.payment_account_models.BankOrCardListResponse
import com.rtchubs.engineerbooks.models.registration.*
import com.rtchubs.engineerbooks.models.transactions.PartnerTransactionResponse
import com.rtchubs.engineerbooks.models.transactions.PayInvoiceResponse
import com.rtchubs.engineerbooks.models.transactions.PaymentStatusResponse
import com.rtchubs.engineerbooks.models.transactions.TransactionHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface ApiService {

    @POST(ApiEndPoint.ALL_COURSE)
    suspend fun getAllCourse(): Response<CourseCategoryResponse>

    @POST(ApiEndPoint.PROMO_CODE)
    suspend fun verifyPromoCode(@Body jsonString: String): Response<PromoCodeResponse>

    @POST(ApiEndPoint.INQUIRE)
    suspend fun inquire(@Body jsonString: String): Response<InquiryResponse>

    @POST(ApiEndPoint.REQUEST_OTP)
    suspend fun requestOTPCode(@Body jsonString: String): Response<InquiryResponse>

    @POST(ApiEndPoint.VERIFY_OTP)
    suspend fun verifyOTPCode(@Body jsonString: String): Response<InquiryResponse>

    @POST(ApiEndPoint.RESET_PIN)
    suspend fun resetPin(@Body jsonString: String): Response<InquiryResponse>

    @POST(ApiEndPoint.REGISTER)
    suspend fun registerUser(@Body jsonString: String): Response<UserRegistrationResponse>

    @POST(ApiEndPoint.LOGIN)
    suspend fun loginUser(@Body jsonString: String): Response<UserRegistrationResponse>

    @POST(ApiEndPoint.PROFILE_UPDATE)
    suspend fun updateUserProfile(@Body jsonString: String): Response<UserRegistrationResponse>

    @POST(ApiEndPoint.PROFILE_INFO)
    suspend fun getUserInfo(@Body jsonString: String): Response<UserRegistrationResponse>

    @POST(ApiEndPoint.BOOKS)
    suspend fun getBooks(@Body jsonString: String): Response<ClassWiseBookResponse>

    @POST(ApiEndPoint.ALL_FREE_BOOKS)
    suspend fun getAllFreeBooks(): Response<ClassWiseBookResponse>

    @POST(ApiEndPoint.BOOK)
    suspend fun getBook(@Body jsonString: String): Response<SingleBookResponse>

    @POST(ApiEndPoint.CHAPTERS)
    suspend fun getChapters(@Body jsonString: String): Response<ChapterResponse>

    @GET(ApiEndPoint.DISTRICT)
    suspend fun getDistrict(): Response<DistrictResponse>

    @GET(ApiEndPoint.UPAZILLA)
    suspend fun getUpazilla(@Path("districtID") districtID: String): Response<UpazillaResponse>

    @GET(ApiEndPoint.ACADEMIC_CLASS)
    suspend fun getAcademicClasses(): Response<AcademicClassResponse>

    @POST(ApiEndPoint.CREATE_ORDER)
    suspend fun createOrder(@Body jsonString: String): Response<PayInvoiceResponse>

    @POST(ApiEndPoint.PURCHASE_COURSE)
    suspend fun purchaseCourse(@Body jsonString: String): Response<CoursePaymentResponse>

    @POST(ApiEndPoint.MY_COURSES)
    suspend fun getAllMyCourses(@Body body: String?): Response<MyCourseListResponse>

    @POST(ApiEndPoint.ALL_FAQS)
    suspend fun getAllFaqs(): Response<AllFaqResponse>

    @POST(ApiEndPoint.TRANSACTION)
    suspend fun transactionHistory(@Body jsonString: String): Response<TransactionHistoryResponse>

    @POST(ApiEndPoint.CLASS_SCHEDULE)
    suspend fun getClassSchedule(@Body jsonString: String): Response<LiveClassScheduleResponse>

    @POST(ApiEndPoint.OFFER)
    suspend fun getOffers(@Body jsonString: String): Response<OfferResponse>

    @POST(ApiEndPoint.NOTICE)
    suspend fun getNotices(@Body jsonString: String): Response<NoticeResponse>

    @POST(ApiEndPoint.PARTNER_TRANSACTION)
    suspend fun partnerTransactionHistory(@Body jsonString: JsonObject): Response<PartnerTransactionResponse>

    @POST(ApiEndPoint.PARTNER_PAYMENT_STATUS)
    suspend fun partnerPaymentStatus(@Body jsonString: JsonObject): Response<PaymentStatusResponse>

    @GET(ApiEndPoint.SLIDER_ADS)
    suspend fun getAds(): Response<AdSliderResponse>

    @POST(ApiEndPoint.BKASH_PAY)
    suspend fun bkashPaymentUrl(@Body jsonString: JsonObject): Response<BKashPaymentUrlResponse>


    @Multipart
    @POST(ApiEndPoint.REQUESTOTP)
    suspend fun requestOTP(
        @Part("PhoneNumber") mobileNumber: RequestBody,
        @Part("HasGivenConsent") hasGivenConsent: RequestBody
    ): Response<DefaultResponse>

    @Multipart
    @POST(ApiEndPoint.REGISTRATION)
    suspend fun registration(
        @Part("MobileNumber") mobileNumber: RequestBody,
        @Part("Otp") otp: RequestBody,
        @Part("Pin") password: RequestBody,
        @Part("FullName") fullName: RequestBody,
        @Part("MobileOperator") mobileOperator: RequestBody,
        @Part("DeviceId") deviceId: RequestBody,
        @Part("DeviceName") deviceName: RequestBody,
        @Part("DeviceModel") deviceModel: RequestBody,
        @Part userImage: MultipartBody.Part?,
        @Part("NidNumber") nidNumber: RequestBody,
        @Part nidFrontImage: MultipartBody.Part?,
        @Part nidBackImage: MultipartBody.Part?
    ): Response<DefaultResponse>


    @FormUrlEncoded
    @POST(ApiEndPoint.CONNECT_TOKEN)
    suspend fun connectToken(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String,
        @Field("scope") scope: String,
        @Field("device_id") deviceID: String,
        @Field("device_name") deviceName: String,
        @Field("device_model") deviceModel: String,
        @Field("client_id") clientID: String,
        @Field("client_secret") clientSecret: String,
        @Field("otp") otp: String
    ): Response<String>


    @GET(ApiEndPoint.GET_BANK_LIST)
    suspend fun requestBankList(
        @Query("type") type: String,
        @Header("Authorization") token: String
    ): Response<BankOrCardListResponse>

    @Headers(ContentType)
    @POST(ApiEndPoint.ADD_CARD)
    suspend fun addBankAccount(
        @Body jsonObject: JsonObject,
        @Header("Authorization") token: String
    ): Response<AddCardOrBankResponse>

    @Headers(ContentType)
    @POST(ApiEndPoint.ADD_BANK)
    suspend fun addCardAccount(
        @Body jsonObject: JsonObject,
        @Header("Authorization") token: String
    ): Response<AddCardOrBankResponse>

    @GET(ApiEndPoint.MY_ACCOUNT_LIST)
    suspend fun myAccountList(
        @Header("Authorization") token: String
    ): Response<MyAccountListResponse>

}
