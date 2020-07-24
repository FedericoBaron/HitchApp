package com.example.hitchapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.hitchapp.R
import com.example.hitchapp.viewmodels.LoginActivityViewModel
import com.parse.LogInCallback
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnSignup: Button? = null
    private var mLoginActivityViewModel: LoginActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Init ViewModel
        mLoginActivityViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel::class.java)
        mLoginActivityViewModel?.init()

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity()
        }
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignup = findViewById(R.id.btnSignup)
        listeners()


    }

    private fun listeners() {

        // listens for login button click
        btnLogin?.setOnClickListener {
            val username = etUsername?.text.toString().toLowerCase()
            val password = etPassword?.text.toString()
            loginUser(username, password)
        }

        // listens for signup button click
        btnSignup?.setOnClickListener { goSignupActivity() }
    }

    // Tries to login user
    private fun loginUser(username: String, password: String) {
        Log.i(TAG, "Attempting to login user $username")
        val loginCallBack = LogInCallback{ user, e ->
            if(e == null){
                goMainActivity()
                Toast.makeText(this@LoginActivity, "Success!", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.e(TAG, "Issue with login", e)
                Toast.makeText(this@LoginActivity, "Issue with login", Toast.LENGTH_SHORT).show()
                return@LogInCallback
            }
        }

        mLoginActivityViewModel?.loginUser(username, password, loginCallBack)
    }

    private fun goMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)

        // Can't go back to login if you finish activity
        finish()
    }

    private fun goSignupActivity() {
        val i = Intent(this, SignupActivity::class.java)
        startActivity(i)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}