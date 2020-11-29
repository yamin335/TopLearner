package com.rtchubs.engineerbooks.models.registration

data class City(val id: Int, val name: String?)

data class Upazilla(val id: Int, val cityId: Int, val name: String?)

data class Gender(val id: Int, val name: String?)