package ru.skillbranch.kotlinexample

import org.junit.After
import org.junit.Assert
import org.junit.Test

class SimpleTest {

    @After
    fun after(){
        UserHolder.clearHolder()
    }
    @Test
    fun errors(){
        val holder = UserHolder
//        val login = "+7 (917) 971-11-11".replace("[^+\\d]".toRegex(), "")
//        println("$login "+ login.matches("\\+\\d{11}".toRegex()))
//        val error = holder.phoneErrors("+7 (917) 971-11-11")
//        println(error)
    }

    @Test
    fun register_user_success() {
        val holder = UserHolder
        val user = holder.registerUser("John Doe", "John_Doe@unknown.com","testPass")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: john_doe@unknown.com
            fullName: John Doe
            initials: J D
            email: John_Doe@unknown.com
            phone: null
            meta: {auth=password}
        """.trimIndent()

        Assert.assertEquals(expectedInfo, user.userInfo)
    }
}