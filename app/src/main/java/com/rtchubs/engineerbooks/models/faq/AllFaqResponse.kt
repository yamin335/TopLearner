package com.rtchubs.engineerbooks.models.faq

data class AllFaqResponse(val code: Int?, val message: String?, val data: AllFaqResponseData?)

data class AllFaqResponseData(val faqs: List<Faq>?)

data class Faq(val id: Int?, val created_at: String?, val updated_at: String?,
               val question: String?, val answer: String?, val course_id: Any?, var isExpanded: Boolean = false)