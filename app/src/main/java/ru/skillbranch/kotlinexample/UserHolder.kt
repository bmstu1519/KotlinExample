package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import ru.skillbranch.kotlinexample.extensions.findPhone
import ru.skillbranch.kotlinexample.extensions.fullNameToPair

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User{
        return User.makeUser(fullName, email=email, password=password)
            .also { user -> if(map.containsKey(user.login))
            throw IllegalArgumentException("A user with this email already exists")
        else map[user.login] = user }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User{
        return User.makeUser(fullName, phone = rawPhone).also {
                user -> if(map.containsKey(user.login))
            throw IllegalArgumentException("A user with this phone already exists")
            else map[user.login] = user }
    }
    fun requestAccessCode(rawPhone: String){
        map[cleanLogin(rawPhone)]?.updateAccessCode()
    }

    fun loginUser (login: String, password: String) : String?{
        return map[cleanLogin(login)]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }
    fun cleanLogin(login: String): String = login.trim().let {
            if (it.replace("[^0-9]".toRegex(), "").length == 11)
                it.findPhone()
            else it

    }
// Полное имя пользователя; email; соль:хеш пароля; телефон
// Пример: " John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;"
    fun importUsers(list: List<String>): List<User> {
        val userList = mutableListOf<User>()

        for (value in list){
            val fullString: List<String> = value.trim().split(";")
            val (firstName,lastName) = fullString[0].fullNameToPair()
            val email = if (fullString[1].isEmpty()) null else fullString[1]
            //порядок диструктивного присваивания важен, т.к salt, каки lastName может быть null
            val (passwordHash,salt) = fullString[2].fullNameToPair(":")
            val rawPhone: String? = if (fullString[3].isEmpty()) null else fullString[3]

            userList.add(User(firstName,lastName,email,salt,passwordHash,rawPhone).also {
                map[it.login] = it
            })
        }
    return userList
    }
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
        map.clear()
    }
}




