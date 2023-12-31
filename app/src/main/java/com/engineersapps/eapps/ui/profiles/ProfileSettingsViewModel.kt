package com.engineersapps.eapps.ui.profiles

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.registration.*
import com.engineersapps.eapps.models.transactions.PaymentStatusData
import com.engineersapps.eapps.repos.MediaRepository
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.repos.TransactionRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants.serverConnectionErrorMessage
import com.engineersapps.eapps.util.toFile
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class ProfileSettingsViewModel @Inject constructor(
    private val application: Application,
    private val repository: RegistrationRepository,
    private val mediaRepository: MediaRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel(application) {

    var profileBitmap: Bitmap? = null
    var nidFrontBitmap: Bitmap? = null
    var nidBackBitmap: Bitmap? = null

    var selectedGender: Gender? = null
    var selectedClass: AcademicClass? = null
    var firstName = ""
    var lastName = ""
    var fatherName = ""
    var institute = ""
    var roll = ""
    var email = ""
    var address = ""

    val allDistricts: MutableLiveData<List<District>> by lazy {
        MutableLiveData<List<District>>()
    }
    val allUpazilla: MutableLiveData<List<Upazilla>> by lazy {
        MutableLiveData<List<Upazilla>>()
    }
    val allGender = arrayListOf(Gender(1, "Male"), Gender(2, "Female"), Gender(3, "Others"))
    val allAcademicClass: MutableLiveData<List<AcademicClass>> by lazy {
        MutableLiveData<List<AcademicClass>>()
    }

    val allImageUrls: MutableLiveData<ProfileImageUploadData> by lazy {
        MutableLiveData<ProfileImageUploadData>()
    }

    val registrationResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
    }

    val profileUpdateResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
    }

    val userProfileInfo: MutableLiveData<InquiryAccount> by lazy {
        MutableLiveData<InquiryAccount>()
    }

    val partnerPaymentStatus: MutableLiveData<PaymentStatusData> by lazy {
        MutableLiveData<PaymentStatusData>()
    }

    fun getDistricts() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.getDistrictRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allDistricts.postValue(apiResponse.body.data?.districts)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }

    fun getUpazilla(districtID: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.getUpazillaRepo(districtID))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allUpazilla.postValue(apiResponse.body.data?.upazilas)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }

    fun getAcademicClass() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.getAcademicClassRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allAcademicClass.postValue(apiResponse.body.data?.classes)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }

    fun uploadProfileImagesToServer(mobile: String, folder: String) {
        if (checkNetworkStatus(true)) {
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                profileBitmap?.let {
                    val profileImageFile = it.toFile(context)
                    val profileImagePart = profileImageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    addFormDataPart("profilepic", profileImageFile.name, profileImagePart)
                }
                nidFrontBitmap?.let {
                    val nidFrontImageFile = it.toFile(context)
                    val nidFrontImagePart = nidFrontImageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    addFormDataPart("nidfront", nidFrontImageFile.name, nidFrontImagePart)

//                    val profileImagePart = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    addFormDataPart("nidfront", it.name, profileImagePart)
                }
                nidBackBitmap?.let {
                    val nidBackImageFile = it.toFile(context)
                    val nidBackImagePart = nidBackImageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    addFormDataPart("nidback", nidBackImageFile.name, nidBackImagePart)

//                    val profileImagePart = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    addFormDataPart("nidback", it.name, profileImagePart)
                }
                addFormDataPart("mobile", mobile)
                addFormDataPart("folder", folder)
//                nidFrontBitmap?.let {
//                    val profileImagePart = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    addFormDataPart("nidfront", it.name, profileImagePart)
//                }
//                nidBackBitmap?.let {
//                    val profileImagePart = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    addFormDataPart("nidback", it.name, profileImagePart)
//                }
            }.build()

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
                allImageUrls.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(mediaRepository.uploadProfilePhotosRepo(requestBody))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allImageUrls.postValue(apiResponse.body.data)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        allImageUrls.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        allImageUrls.postValue(null)
                    }
                }
            }
        }
    }

    fun registerNewUser(inquiryAccount: InquiryAccount) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.registerUserRepo(inquiryAccount))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        registrationResponse.postValue(apiResponse.body.data)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }

    fun updateUserProfile(inquiryAccount: InquiryAccount) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                profileUpdateResponse.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.updateUserProfileRepo(inquiryAccount))) {
                    is ApiSuccessResponse -> {
                        profileUpdateResponse.postValue(apiResponse.body.data)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        profileUpdateResponse.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        profileUpdateResponse.postValue(null)
                    }
                }
            }
        }
    }

    fun getUserProfileInfo(mobileNumber: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
                userProfileInfo.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.getUserInfo(mobileNumber))) {
                    is ApiSuccessResponse -> {
                        userProfileInfo.postValue(apiResponse.body.data?.Account)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        userProfileInfo.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        userProfileInfo.postValue(null)
                    }
                }
            }
        }
    }

    fun getPartnerPaymentStatus(mobile: String?, cityID: Int?, upazillaID: Int?) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(transactionRepository.partnerPaymentStatusRepo(mobile, cityID, upazillaID))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        partnerPaymentStatus.postValue(apiResponse.body.data)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }
}