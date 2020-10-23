package com.rtchubs.engineerbooks.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.Book
import com.rtchubs.engineerbooks.models.PaymentMethod
import com.rtchubs.engineerbooks.models.SubBook
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import com.rtchubs.engineerbooks.repos.HomeRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper,
    private val application: Application,
    private val repository: HomeRepository
) : BaseViewModel(application) {
    val defaultResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
//    val doctorList: List<Book>
//        get() = listOf(
//            Book(
//                "0",
//                "Top Rated",
//                listOf(
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_1
//                    ),
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_2
//                    ),
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_3
//                    )
//                )
//            ),
//            Book(
//                "1",
//                "Favourites",
//                listOf(
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_4
//                    ),
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_13
//                    ),
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_6
//                    )
//                )
//            ),
//            Book(
//                "2",
//                "Top Rated",
//                listOf(
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_7
//                    ),
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_8
//                    ),
//                    SubBook(
//                        "1",
//                        "Angelica Hale",
//                        "Dhaka",
//                        "",
//                        R.drawable.book_9
//                    )
//                )
//            )
//        )


    val paymentMethodList: List<PaymentMethod>
        get() = listOf(
            /* PaymentMethod(
                 "0",
                 "\u2022\u2022\u2022\u2022 4122",R.drawable.maestro

             ),
             PaymentMethod(
                 "1",
                 "\u2022\u2022\u2022\u2022 9120",R.drawable.visa
             ),*/
            PaymentMethod(
                "-1",
                "Add Payment Method", R.drawable.plus
            )
        )


    val slideDataList = listOf<SlideData>(
        SlideData(R.drawable.slider_image_1, "Ads1", "Easy, Fast and Secure Way"),
        SlideData(R.drawable.slider_image_1, "Ads2", "Easy, Fast and Secure Way"),
        SlideData(R.drawable.slider_image_1, "Ads3", "Easy, Fast and Secure Way"),
        SlideData(R.drawable.slider_image_1, "Ads4", "Easy, Fast and Secure Way"),
        SlideData(R.drawable.slider_image_1, "Ads5", "Easy, Fast and Secure Way")
    )

    inner class SlideData(
        var slideImage: Int,
        var textTitle: String,
        var descText: String
    )


    fun requestBankList(type:String) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            Log.e("token", preferencesHelper.getAccessTokenHeader())
            viewModelScope.launch(handler) {
                when (val apiResponse =
                    ApiResponse.create(repository.requestBankListRepo(type,preferencesHelper.getAccessTokenHeader()))) {
                    is ApiSuccessResponse -> {
                        var d=DefaultResponse(apiResponse.body.toString(), "", "", true)
                        defaultResponse.postValue(d)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        Log.e("error", apiResponse.errorMessage)
                        defaultResponse.postValue(
                            Gson().fromJson(
                                apiResponse.errorMessage,
                                DefaultResponse::class.java
                            )
                        )
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }
}