package com.example.hitchapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.hitchapp.R
import com.example.hitchapp.fragments.HomeFragment
import com.example.hitchapp.fragments.MyRidesFragment
import com.example.hitchapp.fragments.ProfileFragment
import com.example.hitchapp.models.User
import com.example.hitchapp.viewmodels.SignupActivityViewModel
import com.parse.ParseFile
import com.parse.SaveCallback
import com.parse.SignUpCallback
import java.io.File

class SignupActivity : AppCompatActivity() {
    private var photoFile: File? = null
    private val photoFileName = "photo.jpg"
    private val user = User()
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var etEmail: EditText? = null
    private var btnSignup: Button? = null
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var btnUploadStudentId: Button? = null
    private var etCollege: EditText? = null
    private var mSignupActivityViewModel: SignupActivityViewModel? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etEmail = findViewById(R.id.etEmail)
        btnSignup = findViewById(R.id.btnSignup)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        btnUploadStudentId = findViewById(R.id.btnUploadStudentId)
        etCollege = findViewById(R.id.etCollege)

        // Init ViewModel
        mSignupActivityViewModel = ViewModelProviders.of(this).get(SignupActivityViewModel::class.java)
        mSignupActivityViewModel?.init()

        // Listens for signup button click
        listenBtnSignupClick()

        // Listens for upload student id button click
        listenBtnUploadStudentId()
    }

    // Listens for upload student id button click
    private fun listenBtnUploadStudentId() {
        btnUploadStudentId?.setOnClickListener { launchCamera() }
    }

    // Listens for signup button click
    private fun listenBtnSignupClick() {
        btnSignup?.setOnClickListener {
            val username = etUsername?.text.toString()
            val password = etPassword?.text.toString()
            val email = etEmail?.text.toString()
            val firstName = etFirstName?.text.toString()
            val lastName = etLastName?.text.toString()
            val college = etCollege?.text.toString()
            signupUser(username, password, email, firstName, lastName, college)
        }
    }

    private fun signupUser(username: String, password: String, email: String, firstName: String, lastName: String, college: String) {

        // Gets callback to handle results in the activity
        val signUpCallback = SignUpCallback{ e ->
            if(e == null){
                // Hooray! Let them use the app now.
                goMainActivity()
                Toast.makeText(this@SignupActivity, "Success!", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.e(TAG, "Unsuccessful signup", e)
                Toast.makeText(this@SignupActivity, "Issue with signup", Toast.LENGTH_SHORT).show()
                return@SignUpCallback
            }
        }

        // Attempts to sign up new user
        mSignupActivityViewModel?.signupUser(username, password, email, firstName, lastName, college, signUpCallback)

    }

    private fun goMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)

        // Can't go back to login if you finish activity
        finish()

    }

    private fun launchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        val fileProvider = FileProvider.getUriForFile(this, "com.example.hitchapp.fileprovider", photoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.packageManager) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile?.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                val photo = ParseFile(photoFile)

                val saveCallback = SaveCallback { e ->
                    if(e == null){
                        Log.i(TAG, "Picture saved!")
                        Toast.makeText(this, "Picture saved!", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
                    }
                }
                mSignupActivityViewModel?.savePhoto(photo, saveCallback)

            } else { // Result was a failure

            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    companion object {
        const val TAG = "SignupActivity"
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42
    }
}