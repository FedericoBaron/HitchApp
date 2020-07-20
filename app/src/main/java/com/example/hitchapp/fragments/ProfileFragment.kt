package com.example.hitchapp.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.hitchapp.R
import com.example.hitchapp.models.Car
import com.example.hitchapp.models.User
import com.parse.ParseObject

class ProfileFragment : Fragment() {
    private var user: User? = null
    private var car: Car? = null

    // User stuff
    private var ivProfilePicture: ImageView? = null
    private var tvFirstName: TextView? = null
    private var tvLastName: TextView? = null
    private var tvCollege: TextView? = null
    private var tvBiography: TextView? = null

    // Car stuff
    private var tvCarModel: TextView? = null
    private var tvCarMaker: TextView? = null
    private var tvCarYear: TextView? = null
    private var tvCarInfo: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Unwrap the user passed in via bundle, using its simple name as a key
        //driver = Parcels.unwrap(getArguments().getParcelable("user"));
        val bundle = this.arguments
        user = bundle?.getParcelable<Parcelable>("user") as User?

        // Connects frontend to backend
        wireUI()

        // Sets up the profile
        setProfileInfo()
    }

    // Sets variables to views
    private fun wireUI() {
        tvFirstName = view?.findViewById(R.id.tvFirstName)
        tvLastName = view?.findViewById(R.id.tvLastName)
        tvCollege = view?.findViewById(R.id.tvCollege)
        ivProfilePicture = view?.findViewById(R.id.ivProfilePicture)
        tvBiography = view?.findViewById(R.id.tvBiography)

        // Driver Profile
        tvCarMaker = view?.findViewById(R.id.tvCarMaker)
        tvCarModel = view?.findViewById(R.id.tvCarModel)
        tvCarYear = view?.findViewById(R.id.tvCarYear)
        tvCarInfo = view?.findViewById(R.id.tvCarInfo)
    }

    private fun setProfileInfo() {
        // Sets info to match that user
        //Log.i(TAG, user?.college)
        tvCollege?.text = user?.college
        tvFirstName?.text = user?.firstName
        tvLastName?.text = user?.lastName
        tvBiography?.text = user?.biography
        val image = user?.profilePicture
        if (image != null) {
            Log.i(TAG, "here$image")
            context?.let {
                ivProfilePicture?.let { it1 ->
                    Glide.with(it)
                            .load(image.url)
                            .fitCenter()
                            .circleCrop()
                            .into(it1)
                }
            }
        }
        if (user?.car == null) {
            tvCarMaker?.visibility = View.GONE
            tvCarModel?.visibility = View.GONE
            tvCarYear?.visibility = View.GONE
            tvCarInfo?.visibility = View.GONE
        } else {
            tvCarMaker?.visibility = View.VISIBLE
            tvCarModel?.visibility = View.VISIBLE
            tvCarYear?.visibility = View.VISIBLE
            tvCarInfo?.visibility = View.VISIBLE
            car = user?.car

            // fetch car data
            try {
                car?.fetch<ParseObject>()
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't fetch car", e)
            }
            tvCarModel?.text = car?.getCarModel()
            tvCarMaker?.text = car?.getCarMaker()
            tvCarYear?.text = car?.getCarYear().toString()
        }
    }

    companion object {
        private const val TAG = "DriverProfileFragment"
    }
}