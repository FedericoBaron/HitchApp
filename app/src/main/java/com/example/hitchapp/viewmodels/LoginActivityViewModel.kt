package com.example.hitchapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hitchapp.repositories.UserRepository
import com.parse.LogInCallback

class LoginActivityViewModel : ViewModel(){

    protected var mRepo: UserRepository? = null

    fun init() {
        mRepo = UserRepository.instance
    }

    fun loginUser(username: String, password: String, loginCallBack: LogInCallback) {
        mRepo?.loginUser(username, password, loginCallBack);
    }


    companion object {
        const val TAG = "LoginActivityViewModel"
    }


}