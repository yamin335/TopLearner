package com.rtchubs.engineerbooks.models.transactions

data class TransactionHistoryResponse(val code: Int?, val data: TransactionHistoryData?, val msg: String?)

data class TransactionHistoryData(val transactions: List<Transaction>?)

data class Transaction(val udid: String?, val createdAt: String?, val updatedAt: String?,
                       val StudentID: Int?, val PartnerID: Int?, val StudentMobile: String?,
                       val PaidAmount: Int?, val DueAmount: Int?, val discount: Int?,
                       val GrandTotal: Int?, val institute: String?, val upazila: String?,
                       val UpazilaID: Int?, val CityID: Int?, val city: String?,
                       val InvoiceID: String?, val ReferenceID: String?,
                       val PayemtReferenceID: String?, val ClassID: Int?,
                       val BookID: Int?, val StudentName: String?, val BookName: String?,
                       val Date: String?, val Table: String?)

data class PartnerTransactionResponse(val code: Int?, val data: PartnerTransactionData?, val msg: String?)

data class PartnerTransactionData(val payments: List<PartnerTransaction>?)

data class PartnerTransaction(val udid: String?, val createdAt: String?, val updatedAt: String?,
                              val partner_id: Int?, val partner_name: String?,
                              val partner_mobile: String?, val payment_method: String?, val payamount: Int?,
                              val InvoiceID: String?, val remarks: String?,
                              val TransactionID: String?, val PayemtReferenceID: String?,
                              val upazila_id: Int?, val city_id: Int?,
                              val PaymentDate: String?, val Table: String?)

data class PaymentStatusResponse(val code: Int?, val data: PaymentStatusData?, val msg: String?)

data class PaymentStatusData(val totalamountdue: Int?, val totalamountearns: Int?, val totalamountpaid: Int?)