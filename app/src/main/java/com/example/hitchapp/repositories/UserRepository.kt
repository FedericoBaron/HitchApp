package com.example.hitchapp.repositories

import com.example.hitchapp.models.User
import com.parse.*

class UserRepository {
    fun loginUser(username: String?, password: String?, loginCallBack: LogInCallback?) {
        ParseUser.logInInBackground(username, password, loginCallBack)
    }

    fun signupUser(user: User, signUpCallback: SignUpCallback?) {
        user.signUpInBackground(signUpCallback)
    }

    fun savePhoto(photo: ParseFile, saveCallback: SaveCallback?) {
        photo.saveInBackground(saveCallback)
    }

    companion object {
        const val TAG = "UserRepository"
        var instance: UserRepository? = null
            get() {
                if (field == null) {
                    field = UserRepository()
                }
                return field
            }
            private set
    }
}