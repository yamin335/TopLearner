package com.rtchubs.engineerbooks.prefs

import android.content.SharedPreferences
import com.rtchubs.engineerbooks.api.ProfileInfo
import com.rtchubs.engineerbooks.api.TokenInformation
import com.rtchubs.engineerbooks.models.home.PaidBook
import com.rtchubs.engineerbooks.models.registration.InquiryAccount


interface PreferencesHelper {

    var shouldClearBackStackOfHomeNav: Boolean

    val preference: SharedPreferences

    var isDeviceTimeChanged: Boolean

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

    fun getAccessTokenHeader(): String

    fun getAuthHeader(token: String?): String

    fun logoutUser()

    fun saveToken(tokenInformation: TokenInformation)

    fun saveUserProfile(profile: ProfileInfo)

    fun getToken(): TokenInformation

    fun saveUser(user: InquiryAccount)

    fun getUser(): InquiryAccount

    fun savePaidBook(book: PaidBook)

    fun getPaidBook(): PaidBook

    var validityLimiterMap: MutableMap<String, Long>?

    var language: String?
}
