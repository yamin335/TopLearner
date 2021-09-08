package com.engineersapps.eapps.models.transactions

data class PayInvoiceResponse(val code: Int?, val data: PayInvoiceData?, val msg: String?)

data class PayInvoiceData(val salesinvoice: Salesinvoice?)

data class Salesinvoice(val udid: String?, val createdAt: String?, val updatedAt: String?,
                        val StudentID: Int?, val PartnerID: Int?, val StudentMobile: String?,
                        val PaidAmount: Int?, val DueAmount: Int?, val discount: Int?,
                        val GrandTotal: Int?, val institute: String?, val upazila: String?,
                        val UpazilaID: Int?, val CityID: Int?, val city: String?,
                        val InvoiceID: String?, val ReferenceID: String?,
                        val PayemtReferenceID: String?, val ClassID: Int?, val BookID: Int?,
                        val StudentName: String?, val BookName: String?, val Date: String?, val Table: String?)

data class CreateOrderBody(var StudentID: Int = 0, var StudentMobile: String = "",
                           var GrandTotal: Int = 0, var PaidAmount: Int = 0,
                           var DueAmount: Int = 0, var discount: Int = 0,
                           var institute: String = "", var upazila: String = "",
                           var city: String = "", var UpazilaID: Int = 0,
                           var CityID: Int = 0, var InvoiceID: String = "",
                           var ReferenceID: String = "", var PayemtReferenceID: String = "",
                           var book_id: Int = 0, var ClassID: Int = 0,
                           var StudentName: String = "", var bookname: String = "",
                           var TransactionID: String = "", var coursename: String = "",
                           var bookthumb: String = "", var promo_code: String = "", var PartnerID: Int = 0)