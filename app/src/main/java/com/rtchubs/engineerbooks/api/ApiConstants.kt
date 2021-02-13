package com.rtchubs.engineerbooks.api

import com.rtchubs.engineerbooks.api.Api.ACCOUNT_REPO
import com.rtchubs.engineerbooks.api.Api.ADMIN_API_ROOT_URL
import com.rtchubs.engineerbooks.api.Api.API_REPO
import com.rtchubs.engineerbooks.api.Api.API_VERSION
import com.rtchubs.engineerbooks.api.Api.AUTH_REPO
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_BANK
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_BANK_INFO
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_BOOKS
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_CARD
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_CHAPTERS
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_CLASS
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_COMMISSION
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_CONNECT
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_DISTRICT
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_INQUIRE
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_LOGIN
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_LOGO
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_NOTICES
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_ORDER
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_PDF
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_PROFILE
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_PROFILE_IMAGES
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_PROFILE_INFO
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_PROFILE_PHOTOS
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_REGISTER
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_REQUEST_OTP
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_RESET_PIN
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_SOMADHAN
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_TRANSACTION
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_UPAZILLA
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_UPDATE_PROFILE
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_UPLOADS
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_VERIFY_OTP
import com.rtchubs.engineerbooks.api.Api.DIRECTORY_VIDEOS
import com.rtchubs.engineerbooks.api.Api.PUBLIC_REPO
import com.rtchubs.engineerbooks.api.Api.SALES_REPO

object Api {
    const val PROTOCOL = "https"
    const val API_ROOT = "backend.engineersmath.com"
    const val ADMIN_API_ROOT = "adminbackend.engineersmath.com"
    const val API_ROOT_URL = "$PROTOCOL://$API_ROOT/"
    const val ADMIN_API_ROOT_URL = "$PROTOCOL://$ADMIN_API_ROOT/"
    const val API_REPO = "api"
    const val AUTH_REPO = "auth"
    const val PUBLIC_REPO = "public"
    const val ACCOUNT_REPO = "account"
    const val SALES_REPO = "sales"
    const val API_VERSION = "v1"
    const val DIRECTORY_UPLOADS = "uploads"
    const val DIRECTORY_INQUIRE = "checkuser"
    const val DIRECTORY_REQUEST_OTP = "sendotp"
    const val DIRECTORY_VERIFY_OTP = "verifyotp"
    const val DIRECTORY_RESET_PIN = "resetpin"
    const val DIRECTORY_PROFILE_PHOTOS = "uploadProfilePhotos"
    const val DIRECTORY_REGISTER = "register"
    const val DIRECTORY_DISTRICT = "getCities"
    const val DIRECTORY_UPAZILLA = "getUpazilasby"
    const val DIRECTORY_CLASS = "getClasses"
    const val DIRECTORY_UPDATE_PROFILE = "updateprofile"
    const val DIRECTORY_PROFILE_INFO = "info"
    const val DIRECTORY_BOOKS = "books"
    const val DIRECTORY_PROFILE_IMAGES = "profilephotos"
    const val DIRECTORY_TRANSACTION = "transactions"
    const val DIRECTORY_ORDER = "createorder"
    const val DIRECTORY_LOGIN = "login"
    const val DIRECTORY_CHAPTERS = "chapters"
    const val DIRECTORY_LOGO = "logo"
    const val DIRECTORY_PDF = "pdf"
    const val DIRECTORY_VIDEOS = "videos"
    const val DIRECTORY_COMMISSION = "commission"
    const val DIRECTORY_NOTICES = "getnotices/null"
    const val DIRECTORY_SOMADHAN = "somadhan"

    const val DIRECTORY_CONNECT = "connect"
    const val DIRECTORY_BANK_INFO = "bankinformation"
    const val DIRECTORY_CARD = "banklink"
    const val DIRECTORY_BANK = "cardlink"
    const val DIRECTORY_PROFILE = "profile"
    const val ContentType = "Content-Type: application/json"
}

object ApiEndPoint {
    /* Registration */
    const val INQUIRE = "$API_VERSION/$AUTH_REPO/${DIRECTORY_INQUIRE}"
    const val REQUEST_OTP = "$API_VERSION/$AUTH_REPO/${DIRECTORY_REQUEST_OTP}"
    const val VERIFY_OTP = "$API_VERSION/$AUTH_REPO/${DIRECTORY_VERIFY_OTP}"
    const val RESET_PIN = "$API_VERSION/$ACCOUNT_REPO/$DIRECTORY_RESET_PIN"
    const val REGISTER = "$API_VERSION/$AUTH_REPO/${DIRECTORY_REGISTER}"
    const val DISTRICT = "$API_VERSION/$PUBLIC_REPO/${DIRECTORY_DISTRICT}"
    const val UPAZILLA = "$API_VERSION/$PUBLIC_REPO/${DIRECTORY_UPAZILLA}/{districtID}"
    const val ACADEMIC_CLASS = "$API_VERSION/$PUBLIC_REPO/${DIRECTORY_CLASS}"
    const val PROFILE_UPDATE = "$API_VERSION/$ACCOUNT_REPO/${DIRECTORY_UPDATE_PROFILE}"
    const val PROFILE_INFO = "$API_VERSION/$ACCOUNT_REPO/${DIRECTORY_PROFILE_INFO}"
    const val BOOKS = "$API_VERSION/$ACCOUNT_REPO/${DIRECTORY_BOOKS}"
    const val UPLOAD_PHOTOES = "$API_REPO/${DIRECTORY_PROFILE_PHOTOS}"
    const val PROFILE_IMAGES = "$ADMIN_API_ROOT_URL$DIRECTORY_PROFILE_IMAGES"
    const val TRANSACTION = "$API_VERSION/$SALES_REPO/${DIRECTORY_TRANSACTION}"
    const val CREATE_ORDER = "$API_VERSION/$SALES_REPO/${DIRECTORY_ORDER}"
    const val LOGIN = "$API_VERSION/$AUTH_REPO/${DIRECTORY_LOGIN}"
    const val CHAPTERS = "$API_REPO/$DIRECTORY_CHAPTERS/{bookID}"
    const val LOGO = "$ADMIN_API_ROOT_URL$PUBLIC_REPO/$DIRECTORY_UPLOADS/$DIRECTORY_LOGO"
    const val VIDEOS = "$ADMIN_API_ROOT_URL$PUBLIC_REPO/$DIRECTORY_UPLOADS/$DIRECTORY_VIDEOS"
    const val PDF = "$ADMIN_API_ROOT_URL$PUBLIC_REPO/$DIRECTORY_UPLOADS/$DIRECTORY_PDF"
    const val SOMADHAN = "$ADMIN_API_ROOT_URL$PUBLIC_REPO/$DIRECTORY_UPLOADS/$DIRECTORY_SOMADHAN"
    const val ALL_BOOKS = "$API_REPO/$DIRECTORY_BOOKS"
    const val NOTICES = "$API_REPO/$DIRECTORY_NOTICES"
    const val PARTNER_TRANSACTION = "$API_VERSION/$SALES_REPO/$DIRECTORY_COMMISSION"

    const val REQUESTOTP = "/$AUTH_REPO/$API_VERSION/${DIRECTORY_INQUIRE}/request-otp"
    const val REGISTRATION = "/$AUTH_REPO/$API_VERSION/${DIRECTORY_INQUIRE}"
    const val CONNECT_TOKEN = "/$AUTH_REPO/$API_VERSION/${DIRECTORY_CONNECT}/token"
    const val GET_BANK_LIST = "/$AUTH_REPO/$API_VERSION/${DIRECTORY_BANK_INFO}/bank-list"
    const val ADD_BANK = "/$AUTH_REPO/$API_VERSION/${DIRECTORY_BANK}"
    const val ADD_CARD = "/$AUTH_REPO/$API_VERSION/${DIRECTORY_CARD}"
    const val MY_ACCOUNT_LIST = "/$AUTH_REPO/$API_VERSION/${DIRECTORY_PROFILE}/accounts"
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
