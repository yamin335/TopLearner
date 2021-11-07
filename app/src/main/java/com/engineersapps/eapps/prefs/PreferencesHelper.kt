package com.engineersapps.eapps.prefs

import android.content.SharedPreferences
import com.engineersapps.eapps.api.ProfileInfo
import com.engineersapps.eapps.api.TokenInformation
import com.engineersapps.eapps.models.home.PaidBook
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.transactions.CreateOrderBody


interface PreferencesHelper {

    var shouldClearBackStackOfHomeNav: Boolean

    val preference: SharedPreferences

    var isDeviceTimeChanged: Boolean

    var falseOTPCounter: Int

    var isRegistered: Boolean

    var isTermsAccepted: Boolean

    var pinNumber: String?

    var mobileNo: String?

    var operator: String?

    var deviceId: String?

    var deviceName: String?

    var deviceModel: String?

    var isLoggedIn: Boolean

    var accessToken: String?

    var refreshToken: String?

    var phoneNumber: String?

    var userId: Int

    var userRole: String?

    var accessTokenExpiresIn: Long

    val isAccessTokenExpired: Boolean

    var pendingCoursePurchase: CreateOrderBody?

    fun getAccessTokenHeader(): String

    fun getAuthHeader(token: String?): String

    fun logoutUser()

    fun saveToken(tokenInformation: TokenInformation)

    fun saveUserProfile(profile: ProfileInfo)

    fun getToken(): TokenInformation

    fun saveUser(user: InquiryAccount?)

    fun getUser(): InquiryAccount

    fun savePaidBook(book: PaidBook)

    fun getPaidBook(): PaidBook

    var validityLimiterMap: MutableMap<String, Long>?

    var language: String?
}
