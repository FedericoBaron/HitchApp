package com.example.hitchapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.hitchapp.R
import com.example.hitchapp.activities.LoginActivity
import com.example.hitchapp.models.Car
import com.example.hitchapp.models.User
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.File

class MyProfileFragment : Fragment() {
    private var photoFile: File? = null
    private val photoFileName = "photo.jpg"
    private var currentUser: User? = null
    private var btnLogout: Button? = null
    private var car: Car? = null
    //private var view: View? = null

    // User stuff
    private var profilePicture: ImageView? = null
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var etEmail: EditText? = null
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etCollege: EditText? = null
    private var etBiography: EditText? = null
    private var btnUpdateProfile: Button? = null
    private var btnChangePassword: Button? = null
    private var switchDriver: Switch? = null

    // Car stuff
    private var etCarCapacity: EditText? = null
    private var btnSaveDriverProfile: Button? = null
    private var etCarModel: EditText? = null
    private var etCarMaker: EditText? = null
    private var etCarYear: EditText? = null
    private var etLicensePlate: EditText? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //this.view = view

        // Gets the person who's logged in
        currentUser = ParseUser.getCurrentUser() as User

        // Connects frontend to backend
        wireUI()

        // Sets up the profile
        setProfileInfo()

        // Listener for update profile
        updateProfileListener()

        // Listener for password change
        changePasswordListener()

        // Listens for profile picture click
        updateProfilePicListener()

        // Listens for switch to driver
        switchDriverListener()

        // Listens for update to driver profile
        saveDriverProfileListener()

        // Listens for logout button click
        logoutListener()
    }

    // Sets variables to views
    private fun wireUI() {
        // Buttons
        btnLogout = view?.findViewById(R.id.btnLogout)
        btnUpdateProfile = view?.findViewById(R.id.btnUpdateProfile)
        btnChangePassword = view?.findViewById(R.id.btnChangePassword)
        btnSaveDriverProfile = view?.findViewById(R.id.btnSaveDriverProfile)

        // Profile
        etUsername = view?.findViewById(R.id.etUsername)
        etPassword = view?.findViewById(R.id.etPassword)
        etEmail = view?.findViewById(R.id.etEmail)
        etFirstName = view?.findViewById(R.id.etFirstName)
        etLastName = view?.findViewById(R.id.etLastName)
        etCollege = view?.findViewById(R.id.etCollege)
        profilePicture = view?.findViewById(R.id.profilePicture)
        etBiography = view?.findViewById(R.id.etBiography)
        switchDriver = view?.findViewById(R.id.switchDriver)

        // Driver Profile
        etCarCapacity = view?.findViewById(R.id.etCarCapacity)
        etCarMaker = view?.findViewById(R.id.etCarMaker)
        etLicensePlate = view?.findViewById(R.id.etLicensePlate)
        etCarModel = view?.findViewById(R.id.etCarModel)
        etCarYear = view?.findViewById(R.id.etCarYear)
    }

    // If button update profile is clicked, the changes get saved in Parse
    private fun updateProfileListener() {
        btnUpdateProfile?.setOnClickListener {
            currentUser?.username = etUsername?.text.toString()
            currentUser?.email = etEmail?.text.toString()
            currentUser?.college = etCollege?.text.toString()
            currentUser?.firstName = etFirstName?.text.toString()
            currentUser?.lastName = etLastName?.text.toString()
            currentUser?.biography = etBiography?.text.toString()
            currentUser?.isDriver = switchDriver?.isChecked
            save()
        }
    }

    private fun changePasswordListener() {
        btnChangePassword?.setOnClickListener {
            currentUser?.setPassword(etPassword?.text.toString())
            save()
        }
    }

    private fun updateProfilePicListener() {
        profilePicture?.setOnClickListener { launchCamera() }
    }

    private fun saveDriverProfileListener() {
        btnSaveDriverProfile?.setOnClickListener { // If the user has no car object and is driver then make car object
            if (switchDriver?.isChecked == true && currentUser?.car == null) {
                car = Car()
            }

            // Fills out car object with info
            try {
                car?.carCapacity = etCarCapacity?.text.toString().toInt()
                car?.carMaker = etCarMaker?.text.toString()
                car?.carModel = etCarModel?.text.toString()
                car?.carYear = etCarYear?.text.toString().toInt()
                car?.licensePlate = etLicensePlate?.text.toString()
                currentUser?.car = car
                save()
            } catch (e: Exception) {
                Log.e(TAG, "Some input field is empty for driver profile!")
                Toast.makeText(context, "Make sure to fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchDriverListener() {
        switchDriver?.setOnCheckedChangeListener { buttonView, isChecked -> // Sets isDriver to be true or false based on isChecked
            currentUser?.isDriver = isChecked
            if (isChecked) {
                setVisibleDriverProfile()
            } else {
                setGoneDriverProfile()
            }
        }
    }

    private fun setVisibleDriverProfile() {
        btnSaveDriverProfile?.visibility = View.VISIBLE
        etCarCapacity?.visibility = View.VISIBLE
        etCarMaker?.visibility = View.VISIBLE
        etCarModel?.visibility = View.VISIBLE
        etCarYear?.visibility = View.VISIBLE
        etLicensePlate?.visibility = View.VISIBLE
    }

    private fun setGoneDriverProfile() {
        btnSaveDriverProfile?.visibility = View.GONE
        etCarCapacity?.visibility = View.GONE
        etCarMaker?.visibility = View.GONE
        etCarModel?.visibility = View.GONE
        etCarYear?.visibility = View.GONE
        etLicensePlate?.visibility = View.GONE
    }

    private fun logoutListener() {
        btnLogout?.setOnClickListener {
            ParseUser.logOut()
            val currentUser = ParseUser.getCurrentUser() // this will now be null

            // Go to login screen
            val i = Intent(context, LoginActivity::class.java)
            startActivity(i)

            // Prevent people from going back after logging out
            activity?.finish()
        }
    }

    // Saves currentUser into backend
    private fun save() {
        currentUser?.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(context, "Update unsuccessful!", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "update save was successful!")
            Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
            setProfileInfo()
        }
    }

    private fun setProfileInfo() {
        // Sets info to match that user
        etUsername?.setText(currentUser?.username)
        etEmail?.setText(currentUser?.email)
        etCollege?.setText(currentUser?.college)
        etFirstName?.setText(currentUser?.firstName)
        etLastName?.setText(currentUser?.lastName)
        switchDriver?.isChecked = (currentUser?.isDriver == true)
        etBiography?.setText(currentUser?.biography)
        val image = currentUser?.profilePicture
        if (image != null) {
            Log.i(TAG, "here$image")
            context?.let {
                profilePicture?.let { it1 ->
                    Glide.with(it)
                            .load(image.url)
                            .fitCenter()
                            .circleCrop()
                            .into(it1)
                }
            }
        }
        car = currentUser?.car
        if (switchDriver?.isChecked == true && car != null) {
            setVisibleDriverProfile()

            // fetch car data
            try {
                car?.fetch<ParseObject>()
                etCarCapacity?.setText(car?.carCapacity.toString())
                etCarModel?.setText(car?.carModel)
                etCarMaker?.setText(car?.carMaker)
                etCarYear?.setText(car?.carYear.toString())
                etLicensePlate?.setText(car?.licensePlate)
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't fetch car", e)
            }
        } else {
            setGoneDriverProfile()
            switchDriver?.isChecked = false
        }
    }

    private fun launchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        val fileProvider = context?.let { FileProvider.getUriForFile(it, "com.example.hitchapp.fileprovider", photoFile!!) }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile?.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                profilePicture?.setImageBitmap(takenImage)
                currentUser?.profilePicture = ParseFile(photoFile)
                save()
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    companion object {
        private const val TAG = "ProfileFragment"
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42
    }
}