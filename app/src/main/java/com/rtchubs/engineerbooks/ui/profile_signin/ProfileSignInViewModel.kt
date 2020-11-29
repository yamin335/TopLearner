package com.rtchubs.engineerbooks.ui.profile_signin

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.registration.City
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import com.rtchubs.engineerbooks.models.registration.Gender
import com.rtchubs.engineerbooks.models.registration.Upazilla
import com.rtchubs.engineerbooks.repos.RegistrationRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants.serverConnectionErrorMessage
import com.rtchubs.engineerbooks.util.asFile
import com.rtchubs.engineerbooks.util.asFilePart
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileSignInViewModel @Inject constructor(private val application: Application, private val repository: RegistrationRepository) : BaseViewModel(application) {
    val cities = arrayListOf(City(1, "Dhaka"), City(2, "Khulna"), City(3, "Chittagong"), City(4, "Sylhet"))

    val allUpazilla = mapOf(1 to arrayListOf(
        Upazilla(1, 1, "Dhaka NCC"),
        Upazilla(2, 1, "Dhaka SCC"),
        Upazilla(3, 1, "Gazipur")),
        2 to arrayListOf(
            Upazilla(1, 2, "Khulna Sadar"),
            Upazilla(2, 2, "Rupsha"),
            Upazilla(3, 2, "Batiaghata")),
        3 to arrayListOf(
            Upazilla(1, 3, "Cox's Bazar"),
            Upazilla(2, 3, "Bandarban"),
            Upazilla(3, 3, "Rangunia")),
        4 to arrayListOf(
            Upazilla(1, 4, "Sylhet Sadar"),
            Upazilla(2, 4, "Srimangal"),
            Upazilla(3, 4, "Moulovi Bazar")))

    val allGender = arrayListOf(Gender(1, "Male"), Gender(2, "Female"), Gender(3, "Others"))

    fun registerNewUser(mobileNumber: String, otp: String, password: String, fullName: String,
                        mobileOperator: String, deviceId: String,
                        deviceName: String, deviceModel: String, nidNumber: String, nidFrontImage: Uri?,
                        nidBackImage: Uri?, userImage: MultipartBody.Part?): LiveData<DefaultResponse> {
        val response: MutableLiveData<DefaultResponse> = MutableLiveData()
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {

                val frontImagePart = nidFrontImage?.asFile(application)?.asFilePart("NidImageFront")
                val backImagePart = nidBackImage?.asFile(application)?.asFilePart("NidImageBack")
                //val userImagePart = userImage?.asFile(application)?.asFilePart("UserImage")

                when (val apiResponse = ApiResponse.create(repository.registrationRepo(mobileNumber, otp, password, fullName,
                    mobileOperator, deviceId, deviceName, deviceModel, nidNumber, frontImagePart, backImagePart, userImage))) {
                    is ApiSuccessResponse -> {
                        response.postValue(apiResponse.body)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        response.postValue(Gson().fromJson(apiResponse.errorMessage, DefaultResponse::class.java))
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }

        return response
    }

    fun getUpazzilla(cityId: Int): ArrayList<Upazilla> {
        return allUpazilla[cityId] ?: ArrayList()
    }

    fun getAllGenders(): ArrayList<Gender> {
        return allGender
    }
}