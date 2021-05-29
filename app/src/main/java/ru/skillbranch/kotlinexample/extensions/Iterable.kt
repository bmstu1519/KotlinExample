package ru.skillbranch.kotlinexample.extensions

fun String.findPhone(): String = this.replace("[^+\\d]".toRegex(), "")
