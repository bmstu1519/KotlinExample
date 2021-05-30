package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import ru.skillbranch.kotlinexample.extensions.findPhone
import ru.skillbranch.kotlinexample.extensions.fullNameToPair
import ru.skillbranch.kotlinexample.extensions.md5
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import kotlin.IllegalArgumentException

class User private constructor(
    val firstName: String,
    val lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null
){
    val userInfo: String
    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize()

    private val initials: String
    get() = listOfNotNull(firstName,lastName)
        .map { it.first().toUpperCase() }
        .joinToString (" ")

    private var phone: String? = null
        set(value){
            value?.let {
                if (phoneErrors(value)) {
                    field = value.findPhone()
                } else
                    throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
            }

        }

    private var _login: String? = null
    internal var login: String
        get() = _login!!
        set(value) {
            _login = value.toLowerCase()
        }

    private var salt: String? = null
    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    constructor(
        firstName: String,
        lastName: String?,
        email: String?,
        password: String
    ): this(firstName, lastName, email = email, meta = mapOf("auth" to "password")){
        println("Secondary mail constructor")
        passwordHash = encrypt(password)

    }
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ): this(firstName, lastName, rawPhone = rawPhone,meta = mapOf("auth" to "sms")){
        println("Secondary phone constructor")
        updateAccessCode()
    }

    constructor(
        firstName: String,
        lastName: String?,
        email : String?,
        salt : String?,
        passwordHash : String,
        rawPhone: String?
    ): this(firstName, lastName, email, rawPhone, meta = mapOf("src" to "csv")){
        this.salt = salt
        this.passwordHash = passwordHash
    }

    fun updateAccessCode(){
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        println(code)
        sendAccessCodeToUser(phone!!,code)
    }

    init {
        println("First init block, primary constructor was called")

        if (salt == null)
            salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()

        check(!firstName.isBlank()) {"First Name must not be blank"}
        check(email.isNullOrBlank() || rawPhone.isNullOrBlank()) {"Email or phone must not be blank"}

        phone = rawPhone
        login = email ?: phone!!

        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: $email
            phone: $phone
            meta: $meta
            """.trimIndent()
    }

    fun checkPassword(pass: String) = encrypt(pass) == passwordHash //проверка введеного пароля пользователем

    fun changePassword(oldPass: String, newPass: String){
        if(checkPassword(oldPass)) passwordHash = encrypt(newPass)
        else throw IllegalArgumentException("New pass does not match the current pass")
    }

    private fun encrypt(password: String): String = salt.plus(password).md5()

    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        return StringBuilder().apply{
            repeat(6){
                (possible.indices).random().also{index ->
                    append(possible[index])
                }
            }
        }.toString()

    }

    private fun sendAccessCodeToUser(phone: String, code: String) {
        println("..... sending access code: $code on $phone")
    }

    companion object Factory{
        fun makeUser(
            fullName: String,
            email: String? = null,
            password: String? = null,
            phone: String? = null
        ): User{
            val (firstName, lastName) = fullName.fullNameToPair()

            return when{
                !phone.isNullOrBlank() -> User(firstName, lastName, rawPhone = phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() -> User(firstName, lastName, email, password)
                else -> throw IllegalArgumentException("Email or phone cant be null or blank")
            }
        }
        fun phoneErrors(rawPhone: String) : Boolean =
            rawPhone.findPhone().matches("\\+\\d{11}".toRegex())
    }
}