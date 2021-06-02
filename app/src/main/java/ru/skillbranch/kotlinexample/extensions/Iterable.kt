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


fun <T>List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T>{
    val mutableList = this.toMutableList()
    var removeElement: T
    //в it сохраняется удаленный элемент
    do {
        removeElement = mutableList.removeAt(mutableList.size - 1)
    //до тех пор пока в списке listOf(1, 2, 3,4,5).dropLastUntil { it == 2 } не будет найден эл-т
    }while (!predicate.invoke(removeElement)) //{ removeElement == 2 }

    return mutableList

}
