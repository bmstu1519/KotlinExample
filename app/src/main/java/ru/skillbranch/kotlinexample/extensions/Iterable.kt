package ru.skillbranch.kotlinexample.extensions

import java.math.BigInteger
import java.security.MessageDigest

fun String.findPhone(): String = this.replace("[^+\\d]".toRegex(), "")

fun String.fullNameToPair(delimiter: String = " "): Pair<String, String?>{
    return this.split(delimiter)
        .filter { it.isNotBlank() }
        .run{
            when(size){
                1 -> first() to null
                2-> first() to last()
                else -> throw IllegalArgumentException("only first name and last name or both," +
                        " current split result ${this@fullNameToPair}")
            }
        }
}

fun String.md5() : String{
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(toByteArray()) // 16 byte
    val hexString = BigInteger(1, digest).toString(16)
    return hexString.padStart(32,'0')
}
