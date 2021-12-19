package com.engineersapps.eapps.models.transactions

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class PayInvoiceResponse(val code: Int?, val data: PayInvoiceData?, val msg: String?)

data class PayInvoiceData(val salesinvoice: SalesInvoice?)

data class SalesInvoice(val udid: String?, val createdAt: String?, val updatedAt: String?,
                        val StudentID: Int?, val PartnerID: Int?, val StudentMobile: String?,
                        val PaidAmount: Int?, val DueAmount: Int?, val discount: Int?,
                        val GrandTotal: Int?, val institute: String?, val upazila: String?,
                        val UpazilaID: Int?, val CityID: Int?, val city: String?,
                        val InvoiceID: String?, val ReferenceID: String?,
                        val PayemtReferenceID: String?, val ClassID: Int?, val BookID: Int?,
                        val StudentName: String?, val BookName: String?, val Date: String?,
                        val Table: String?): Serializable

@Entity(tableName = "pending_my_courses")
data class CreateOrderBody(var StudentID: Int = 0, var StudentMobile: String = "",
                           var GrandTotal: Int = 0, var PaidAmount: Int = 0,
                           var DueAmount: Int = 0, var discount: Int = 0,
                           var institute: String = "", var upazila: String = "",
                           var city: String = "", var UpazilaID: Int = 0,
                           var CityID: Int = 0, var InvoiceID: String = "",
                           var ReferenceID: String = "", var PayemtReferenceID: String = "",
                           var book_id: Int = 0, var ClassID: Int = 0,
                           var StudentName: String = "", var bookname: String = "",
                           @PrimaryKey(autoGenerate = false) var TransactionID: String = "",
                           var coursename: String = "", var bookthumb: String = "",
                           var promo_code: String = "", var PartnerID: Int = 0,
                           val duration: Int? = 0, val course_id: Int?, val total_amount: Int?,
                           val paid_amount: Int?, val remaindays: Int?): Serializable

data class PaymentStoreBody(val mobile: String?, val amount: Int?, val course_id: Int?,
                            val coursename: String?, val duration : Int?, val class_id : Int?,
                            val StudentID: Int?, val PartnerID: Int?, val partnerMobile: String?,
                            val discount: Int?, val upazila: String?, val UpazilaID: Int?,
                            val CityID: Int?, val City: String?, val StudentName: String?,
                            val promo_code: String?, val InvoiceID: String?)

data class PaymentStoreResponse(val code: Int?, val data: PaymentStoreResponseData?, val msg: String?)

data class PaymentStoreValue(val status: String?, val mobile: String?, val invoice_refer: String?,
                          val error_reason: String?, val pay_url: String?, val qr_image_pay_url: String?,
                          val GatewayPageURL: String?, val failedreason: String?, val sessionkey: String?,
                          val amount: Int?, val coursename: String?, val uuid: String?, val tran_id: String?,
                          val StudentID: Int?, val PartnerID: Int?, val discount: Int?, val upazila: String?,
                          val UpazilaID: Int?, val CityID: Int?, val course_id: Int?, val remaindays: Int?,
                          val city: String?, val InvoiceID: String?, val ClassID: Int?, val StudentName: String?,
                          val promo_code: String?, val duration: Int?)

data class PaymentStoreResponseData(val createresponse: PaymentStoreValue?)