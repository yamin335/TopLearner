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


data class AdminPayHistoryResponse(val code: String?, val status: String?, val message: String?, val data: AdminPayHistoryData?)

data class AdminPayHistoryData(val sales: AdminPaySales?, val total: AdminPayTotal?)

data class AdminPayHistory(val id: Int?, val student_id: Int?, val partner_id: Int?, val status: String?,
                           val uuid: String?, val taxType: Any?, val customer_note: Any?, val tax_type_total: Any?,
                           val discount_type_total: Any?, val sub_total: Any?, val grand_total: Int?,
                           val paid_amount: Int?, val due_amount: Int?, val created_at: String?,
                           val updated_at: String?, val student_mobile: String?, val partner_mobile: Any?,
                           val upazila: String?, val city: String?, val discount: Int?, val book_id: Int?,
                           val book_name: String?, val date: String?, val invoice_id: String?, val reference_id: String?,
                           val student_name: String?, val payemt_reference_id: String?, val institute: String?,
                           val city_id: Int?, val upazila_id: Int?, val class_id: Int?)

data class AdminPaySales(val current_page: Int?, val data: List<AdminPayHistory>?, val first_page_url: String?,
                 val from: Int?, val last_page: Int?, val last_page_url: String?, val next_page_url: Any?,
                 val path: String?, val per_page: Int?, val prev_page_url: Any?, val to: Int?, val total: Int?)

data class AdminPayTotal(val amount: Int?, val vat: Int?, val discount: Int?, val total: Int?)