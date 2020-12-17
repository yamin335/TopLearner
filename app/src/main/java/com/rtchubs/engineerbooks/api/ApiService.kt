package com.rtchubs.engineerbooks.api

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.Api.ContentType
import com.rtchubs.engineerbooks.models.common.MyAccountListResponse
import com.rtchubs.engineerbooks.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.engineerbooks.models.payment_account_models.BankOrCardListResponse
import com.rtchubs.engineerbooks.models.registration.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface ApiService {

    @POST(ApiEndPoint.INQUIRE)
    suspend fun inquire(@Body jsonString: String): Response<InquiryResponse>

    @POST(ApiEndPoint.REQUEST_OTP)
    suspend fun requestOTPCode(@Body jsonString: String): Response<InquiryResponse>

    @POST(ApiEndPoint.VERIFY_OTP)
    suspend fun verifyOTPCode(@Body jsonString: String): Response<InquiryResponse>

    @POST(ApiEndPoint.PROFILE_PHOTOS)
    suspend fun uploadProfilePhotos(@Body partFormData: RequestBody): Response<ProfileImageUploadResponse>

    @POST(ApiEndPoint.REGISTER)
    suspend fun registerUser(@Body jsonString: String): Response<UserRegistrationResponse>

    @POST(ApiEndPoint.PROFILE_UPDATE)
    suspend fun updateUserProfile(@Body jsonString: String): Response<UserRegistrationResponse>

    @GET(ApiEndPoint.DISTRICT)
    suspend fun getDistrict(): Response<DistrictResponse>

    @GET(ApiEndPoint.UPAZILLA)
    suspend fun getUpazilla(@Path("districtID") districtID: String): Response<UpazillaResponse>

    @GET(ApiEndPoint.ACADEMIC_CLASS)
    suspend fun getAcademicClasses(): Response<AcademicClassResponse>

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
