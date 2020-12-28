package com.rtchubs.engineerbooks.models.transactions

data class TransactionHistoryResponse(val code: Number?, val data: TransactionHistoryData?, val msg: String?)

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
