package com.rtchubs.engineerbooks.api

import com.rtchubs.engineerbooks.api.Api.API_VERSION
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_BANK
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_BANK_INFO
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_CARD
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_CONNECT
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_INQUIRE
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_PROFILE
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_PROFILE_PHOTOS
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_REGISTER
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_REQUEST_OTP
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_VERIFY_OTP
import com.rtchubs.engineerbooks.api.Api.REPO

object Api {
    const val PROTOCOL = "https"
    const val API_ROOT = "backend.engineersmath.com"
    const val API_ROOT_URL = "$PROTOCOL://$API_ROOT/"
    const val REPO = "auth"
    const val API_VERSION = "v1"
    const val DIRECTORY_INQUIRE = "checkuser"
    const val DIRECTORY_REQUEST_OTP = "sendotp"
    const val DIRECTORY_VERIFY_OTP = "verifyotp"
    const val DIRECTORY_PROFILE_PHOTOS = "uploadProfilePhotos"
    const val DIRECTORY_REGISTER = "register"
    const val DIRECTORY_CONNECT = "connect"
    const val DIRECTORY_BANK_INFO = "bankinformation"
    const val DIRECTORY_CARD = "banklink"
    const val DIRECTORY_BANK = "cardlink"
    const val DIRECTORY_PROFILE = "profile"
    const val ContentType = "Content-Type: application/json"
}

object ApiEndPoint {
    /* Registration */
    const val INQUIRE = "$API_VERSION/$REPO/${DIRECTORY_INQUIRE}"
    const val REQUEST_OTP = "$API_VERSION/$REPO/${DIRECTORY_REQUEST_OTP}"
    const val VERIFY_OTP = "$API_VERSION/$REPO/${DIRECTORY_VERIFY_OTP}"
    const val PROFILE_PHOTOS = "$API_VERSION/$REPO/${DIRECTORY_PROFILE_PHOTOS}"
    const val REGISTER = "$API_VERSION/$REPO/${DIRECTORY_REGISTER}"
    const val REQUESTOTP = "/$REPO/$API_VERSION/${DIRECTORY_INQUIRE}/request-otp"
    const val REGISTRATION = "/$REPO/$API_VERSION/${DIRECTORY_INQUIRE}"
    const val CONNECT_TOKEN = "/$REPO/$API_VERSION/${DIRECTORY_CONNECT}/token"
    const val GET_BANK_LIST = "/$REPO/$API_VERSION/${DIRECTORY_BANK_INFO}/bank-list"
    const val ADD_BANK = "/$REPO/$API_VERSION/${DIRECTORY_BANK}"
    const val ADD_CARD = "/$REPO/$API_VERSION/${DIRECTORY_CARD}"
    const val MY_ACCOUNT_LIST = "/$REPO/$API_VERSION/${DIRECTORY_PROFILE}/accounts"
}

object ResponseCodes {
    const val CODE_SUCCESS = 200
    const val CODE_TOKEN_EXPIRE = 401
    const val CODE_UNAUTHORIZED = 403
    const val CODE_VALIDATION_ERROR = 400
    const val CODE_DEVICE_CHANGE = 451
    const val CODE_FIRST_LOGIN = 426
}

object ApiCallStatus {
    const val LOADING = 0
    const val SUCCESS = 1
    const val ERROR = 2
    const val EMPTY = 3
}
