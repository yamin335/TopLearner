package com.engineersapps.eapps.models.registration

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class DistrictResponse(val code: Number?, val data: DistrictData?, val msg: String?)

data class DistrictData(val districts: List<District>?)

data class District(val id: String?, val name: String?, val bnname: String?)

data class UpazillaResponse(val code: Int?, val data: UpazillaData?, val msg: String?)

data class UpazillaData(val upazilas: List<Upazilla>?)

data class Upazilla(val id: String?, val name: String?, val bnname: String?)

data class Gender(val id: Int, val name: String?)

data class AcademicClassResponse(val code: Int?, val data: AcademicClassData?, val msg: String?)

@Entity(tableName = "all_classes")
data class AcademicClass(@PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: String,
                         val name: String?, val bnname: String?): Serializable

data class AcademicClassData(val classes: List<AcademicClass>?)

data class UserRegistrationResponse(val code: Int?, val data: UserRegistrationData?, val message: String?)

data class UserRegistrationData(val Account: InquiryAccount?, val Token: Token?)

data class Token(val AccessToken: String?, val RefreshToken: String?, val AccessUUID: String?, val RefreshUUID: String?, val AtExpires: Long?, val RtExpires: Long?)

data class ProfileImageUploadResponse(val code: Int?, val status: String?, val message: String?, val data: ProfileImageUploadData?)

data class ProfileImageUploadData(val profilepic: String?, val nidfront: String?, val nidback: String?, val folder: String?)

data class OTPVerifyResponse(val code: Number?, val data: UserRegistrationData?, val msg: String?)