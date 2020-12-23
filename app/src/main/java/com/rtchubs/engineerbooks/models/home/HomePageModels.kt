package com.rtchubs.engineerbooks.models.home

data class ClassWiseBookResponse(val code: Number?, val data: ClassWiseBookData?, val msg: String?)

data class ClassWiseBook(var id: Int, val udid: String?, val name: String?, val title: String?, val author: String?, var isPaid: Boolean?, val book_type_id: Number?)

data class ClassWiseBookData(val books: List<ClassWiseBook>?)