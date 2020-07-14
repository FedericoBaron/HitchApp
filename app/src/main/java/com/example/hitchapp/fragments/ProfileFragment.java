package com.example.hitchapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hitchapp.R;
import com.example.hitchapp.activities.LoginActivity;
import com.example.hitchapp.models.Car;
import com.example.hitchapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private User currentUser;
    private Button btnLogout;
    private Car car;
    private View view;

    // User stuff
    private ImageView profilePicture;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etCollege;
    private EditText etBiography;
    private Button btnUpdateProfile;
    private Button btnChangePassword;
    private Switch switchDriver;

    // Car stuff
    private EditText etCarCapacity;
    private Button btnSaveDriverProfile;
    private EditText etCarModel;
    private EditText etCarMaker;
    private EditText etCarYear;
    private EditText etLicensePlate;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        // Gets the person who's logged in
        currentUser = (User) ParseUser.getCurrentUser();

        // Connects frontend to backend
        wireUI();

        // Sets up the profile
        setProfileInfo();

        // Listener for update profile
        updateProfileListener();

        // Listener for password change
        changePasswordListener();

        // Listens for profile picture click
        updateProfilePicListener();

        // Listens for switch to driver
        switchDriverListener();

        // Listens for update to driver profile
        saveDriverProfileListener();

        // Listens for logout button click
        logoutListener();
    }

    // Sets variables to views
    private void wireUI(){
        // Buttons
        btnLogout = view.findViewById(R.id.btnLogout);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSaveDriverProfile = view.findViewById(R.id.btnSaveDriverProfile);

        // Profile
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etCollege = view.findViewById(R.id.etCollege);
        profilePicture = view.findViewById(R.id.profilePicture);
        etBiography = view.findViewById(R.id.etBiography);
        switchDriver = view.findViewById(R.id.switchDriver);

        // Driver Profile
        etCarCapacity = view.findViewById(R.id.etCarCapacity);
        etCarMaker = view.findViewById(R.id.etCarMaker);
        etLicensePlate = view.findViewById(R.id.etLicensePlate);
        etCarModel = view.findViewById(R.id.etCarModel);
        etCarYear = view.findViewById(R.id.etCarYear);
    }

    // If button update profile is clicked, the changes get saved in Parse
    private void updateProfileListener() {
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setUsername(etUsername.getText().toString());
                currentUser.setEmail(etEmail.getText().toString());
                currentUser.setCollege(etCollege.getText().toString());
                currentUser.setFirstName(etFirstName.getText().toString());
                currentUser.setLastName(etLastName.getText().toString());
                currentUser.setBiography(etBiography.getText().toString());
                currentUser.setIsDriver(switchDriver.isChecked());

                save();
            }
        });
    }

    private void changePasswordListener() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setPassword(etPassword.getText().toString());
                save();
            }
        });
    }

    private void updateProfilePicListener(){

        profilePicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchCamera();
            }

        });
    }

    private void saveDriverProfileListener(){
        btnSaveDriverProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the user has no car object and is driver then make car object
                if(switchDriver.isChecked() && currentUser.getCar() == null){
                    car = new Car();
                }

                // Fills out car object with info
                try {
                    car.setCarCapacity(Integer.parseInt(etCarCapacity.getText().toString()));
                    car.setCarMaker(etCarMaker.getText().toString());
                    car.setCarModel(etCarModel.getText().toString());
                    car.setCarYear(Integer.parseInt(etCarYear.getText().toString()));
                    car.setLicensePlate(etLicensePlate.getText().toString());
                    currentUser.setCar(car);
                    save();
                } catch(Exception e){
                    Log.e(TAG, "Some input field is empty for driver profile!");
                    Toast.makeText(getContext(), "Make sure to fill out all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void switchDriverListener(){
        switchDriver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Sets isDriver to be true or false based on isChecked
                currentUser.setIsDriver(isChecked);

                if(isChecked){
                    setVisibleDriverProfile();
                }
                else{
                    setGoneDriverProfile();
                }

            }
        });
    }

    private void setVisibleDriverProfile(){
        btnSaveDriverProfile.setVisibility(View.VISIBLE);
        etCarCapacity.setVisibility(View.VISIBLE);
        etCarMaker.setVisibility(View.VISIBLE);
        etCarModel.setVisibility(View.VISIBLE);
        etCarYear.setVisibility(View.VISIBLE);
        etLicensePlate.setVisibility(View.VISIBLE);
    }

    private void setGoneDriverProfile(){
        btnSaveDriverProfile.setVisibility(View.GONE);
        etCarCapacity.setVisibility(View.GONE);
        etCarMaker.setVisibility(View.GONE);
        etCarModel.setVisibility(View.GONE);
        etCarYear.setVisibility(View.GONE);
        etLicensePlate.setVisibility(View.GONE);
    }

    private void logoutListener(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

                // Go to login screen
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);

                // Prevent people from going back after logging out
                getActivity().finish();
            }
        });
    }

    // Saves currentUser into backend
    private void save(){
        currentUser.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "update save was successful!");
                Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                setProfileInfo();
            }
        });
    }

    private void setProfileInfo(){
        // Sets info to match that user
        etUsername.setText(currentUser.getUsername());
        etEmail.setText(currentUser.getEmail());
        etCollege.setText(currentUser.getCollege());
        etFirstName.setText(currentUser.getFirstName());
        etLastName.setText(currentUser.getLastName());
        switchDriver.setChecked(currentUser.getIsDriver());
        etBiography.setText(currentUser.getBiography());
        ParseFile image = currentUser.getProfilePicture();
        if(image != null) {
            Log.i(TAG, "here" + image);
            Glide.with(getContext())
                    .load(image.getUrl())
                    .fitCenter()
                    .circleCrop()
                    .into(profilePicture);
        }

        car = currentUser.getCar();
        if(switchDriver.isChecked() && car != null){
            setVisibleDriverProfile();

            // fetch car data
            try {
                car.fetch();
            } catch(Exception e){
                Log.e(TAG, "Couldn't fetch car", e);
            }

            etCarCapacity.setText(String.valueOf(car.getCarCapacity()));
            etCarModel.setText(car.getCarModel());
            etCarMaker.setText(car.getCarMaker());
            etCarYear.setText(String.valueOf(car.getCarYear()));
            etLicensePlate.setText(car.getLicensePlate());
        }
        else{
            setGoneDriverProfile();
            switchDriver.setChecked(false);
        }
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.hitchapp.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                profilePicture.setImageBitmap(takenImage);
                currentUser.setProfilePicture(new ParseFile(photoFile));
                save();

            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}
