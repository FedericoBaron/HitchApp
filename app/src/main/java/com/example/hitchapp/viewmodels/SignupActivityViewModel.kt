package com.example.hitchapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.User
import com.example.hitchapp.repositories.UserRepository
import com.parse.ParseFile
import com.parse.SaveCallback
import com.parse.SignUpCallback

class SignupActivityViewModel: ViewModel(){
    protected var mRepo: UserRepository? = null
    private val user = User()

    fun init() {
        mRepo = UserRepository.getInstance()
    }

    fun signupUser(username: String, password: String, email: String, firstName: String, lastName: String, college: String, signUpCallback: SignUpCallback) {
        // Set core properties
        user.username = username.toLowerCase()
        user.setPassword(password)
        user.email = email
        user.firstName = firstName
        user.lastName = lastName
        user.college = college

        mRepo?.signupUser(user, signUpCallback)
    }

    fun savePhoto(photo: ParseFile, saveCallback: SaveCallback) {
        mRepo?.savePhoto(photo, saveCallback)
        user.collegeId = photo
    }


    companion object {
        const val TAG = "SignupActivityViewModel"
    }
}