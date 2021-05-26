package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User{
        return User.makeUser(fullName, email=email, password=password)
            .also { user -> if (map.containsKey(user.login))
                throw IllegalArgumentException("A user with this email already exists")
                else map[user.login] = user }
    }

    fun loginUser (login: String, password: String) : String?{
        return map[login.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
        map.clear()
    }

}