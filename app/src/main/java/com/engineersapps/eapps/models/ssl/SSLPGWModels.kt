package com.engineersapps.eapps.models.ssl

data class SSLPaymentInquiryResponse(val APIConnect: String?, val no_of_trans_found: Int?, val element: List<SSLTransaction>?)

data class SSLTransaction(val currency: String?, val val_id: String?, val status: String?,
                            val validated_on: String?, val currency_type: String?, val currency_amount: String?,
                            val currency_rate: String?, val base_fair: String?, val value_a: String?,
                            val value_b: String?, val value_c: String?, val value_d: String?,
                            val discount_percentage: String?, val discount_remarks: String?,
                            val discount_amount: Double?, val tran_date: String?, val tran_id: String?,
                            val amount: String?, val store_amount: String?, val bank_tran_id: String?,
                            val card_type: String?, val risk_title: String?, val risk_level: String?,
                            val bank_gw: String?, val card_no: String?, val card_issuer: String?,
                            val card_brand: String?, val card_sub_brand: String?, val card_issuer_country: String?,
                            val card_issuer_country_code: String?, val gw_version: String?,
                            val emi_instalment: String?, val emi_amount: String?,
                            val emi_description: String?, val emi_issuer: String?, val error: String?)