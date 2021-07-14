package com.rtchubs.engineerbooks.models.faq

data class AllFaqResponse(val code: Int?, val msg: String?, val data: AllFaqResponseData?)

data class AllFaqResponseData(val coursefaqs: List<Faq>?)

data class Faq(val question: String?, val answer: String?, val course_id: Int?, var isExpanded: Boolean = false)