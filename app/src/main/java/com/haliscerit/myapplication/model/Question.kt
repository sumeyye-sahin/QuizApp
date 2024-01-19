package com.haliscerit.myapplication.model

data class Question(
    var question: String? = "",
    var option1: String? = "",
    var option2: String? = "",
    var option3: String? = "",
    var option4: String? = "",
    var ans: String? = "",
    var id: String? = "",
    var category: String? = "",
    var exp: String? = ""
)
