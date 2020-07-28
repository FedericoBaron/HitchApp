package com.example.hitchapp.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.hitchapp.R
import com.example.hitchapp.models.Ride
import com.example.hitchapp.viewmodels.ComposeFragmentViewModel
import com.example.hitchapp.viewmodels.EditRideFragmentViewModel
import com.parse.SaveCallback
import java.text.SimpleDateFormat
import java.util.*

class EditRideFragment: ComposeFragment() {

    private var ride: Ride? = null
    private var dateFor = SimpleDateFormat("MM/dd/yyyy")
    private var mEditRideFragmentViewModel: EditRideFragmentViewModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_ride, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init ViewModel
        mEditRideFragmentViewModel = ViewModelProviders.of(this).get(EditRideFragmentViewModel::class.java)
        mEditRideFragmentViewModel?.init()
        // Unwrap the user passed in via bundle, using its simple name as a key
        //driver = Parcels.unwrap(getArguments().getParcelable("user"));
        val bundle = this.arguments
        ride = bundle?.getParcelable<Parcelable>("ride") as Ride?

        if(ride?.pricePerParticipant == true)
            switchPricePerParticipant?.isChecked = true

        etFrom?.setText(ride?.from)
        etTo?.setText(ride?.to)
        etDepartureTime?.setText(ride?.departureTime)
        etDepartureDate?.setText(dateFor.format(ride?.departureDate))
        etPrice?.setText(ride?.price.toString())
        departureDate = ride?.departureDate

        btnPostRideListener()

    }

    // Listens for when the post ride button gets clicked
    private fun btnPostRideListener(){
        btnPost?.setOnClickListener(View.OnClickListener {

            // Define variables to check if empty
            val from = etFrom?.text.toString()
            val to = etTo?.text.toString()
            val price = etPrice?.text.toString()
            //val departureDate = etDepartureDate?.text.toString()
            val departureDate = departureDate
            val departureTime = etDepartureTime?.text.toString()
            val isPerPerson = switchPricePerParticipant?.isChecked

            // check if any of the input fields are still empty
            if (from.isEmpty()) {
                Toast.makeText(context, "from cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (to.isEmpty()) {
                Toast.makeText(context, "to cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (price.isEmpty()) {
                Toast.makeText(context, "price cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (departureDate == null) {
                Toast.makeText(context, "departure date cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (departureTime.isEmpty()) {
                Toast.makeText(context, "departure time cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            ride?.let { it1 ->
                if (isPerPerson != null) {
                    saveRide(it1, from, to, price, departureDate,  departureTime, isPerPerson)
                }
            }
        })
    }


    // Save ride to the backend
    private fun saveRide(ride: Ride, from: String, to: String, price: String, departureDate: Date?, departureTime: String, isPerPerson: Boolean) {
        val saveRideCallback = SaveCallback {e ->
            if (e == null) {
                // Empties all edit text forms for next time
                etFrom?.setText("")
                etPrice?.setText("")
                etTo?.setText("")
                etDepartureDate?.setText("")
                etDepartureTime?.setText("")
                Log.i(ComposeFragmentViewModel.TAG, "Ride save was successful!")
                Toast.makeText(context, "Ride was posted!", Toast.LENGTH_SHORT).show()

            }
            else{
                Log.e(ComposeFragmentViewModel.TAG, "Error while saving", e)
                Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show()
            }
        }

        mEditRideFragmentViewModel?.saveRide(ride, from, to, price, departureDate, departureTime, fromLocation, isPerPerson, saveRideCallback)

        // Changes to home fragment
        val fragment: Fragment = MyRidesFragment()
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit()
    }

}