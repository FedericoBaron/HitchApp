package com.example.hitchapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hitchapp.repositories.UserRepository
import com.parse.LogInCallback

class SignupActivityViewModel: ViewModel(){
    protected var mRepo: UserRepository? = null

    fun init() {
        mRepo = UserRepository.getInstance()
    }

    fun signupUser(username: String, password: String, loginCallBack: LogInCallback) {
        mRepo?.signupUser(username, password, loginCallBack);
    }


    companion object {
        const val TAG = "SignupActivityViewModel"
    }
}