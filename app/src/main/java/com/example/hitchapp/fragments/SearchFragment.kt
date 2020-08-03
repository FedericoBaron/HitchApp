package com.example.hitchapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.hitchapp.R
import com.example.hitchapp.adapters.MyRidesAdapter
import com.example.hitchapp.models.User
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import org.json.JSONArray
import java.lang.Exception
import java.util.*

class SearchFragment: Fragment() {
    private var etFrom: EditText? = null
    private var etTo: EditText? = null
    private var btnSearch: Button? = null
    private var etDistance: EditText? = null
    private var toSelected = false
    private val REQUEST_CODE = 20
    private var fromLocation: ParseGeoPoint? = null



    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Show toolbar menu items
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireUI()
        editFromListener()
        editToListener()
        btnSearchListener()
    }

    // Hide search from toolbar
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.miSearch)
        item.isVisible = false
    }

    private fun wireUI(){
        etFrom = view?.findViewById(R.id.etFrom)
        etTo = view?.findViewById(R.id.etTo)
        btnSearch = view?.findViewById(R.id.btnSearch)
        etDistance = view?.findViewById(R.id.etDistance)
    }

    private fun btnSearchListener(){
        btnSearch?.setOnClickListener(View.OnClickListener {

            // Define variables to check if empty
            val from = etFrom?.text.toString()
            val to = etTo?.text.toString()
            val distance = etDistance?.text.toString()

            // check if any of the input fields are still empty
            if (from.isEmpty()) {
                Toast.makeText(context, "from cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (to.isEmpty()) {
                Toast.makeText(context, "to cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            try{
                distance.toInt()
            }
            catch(e: Exception){
                Toast.makeText(context, "distance has to be a number", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            val bundle = Bundle()
            bundle.putInt("distance", distance.toInt())
            bundle.putParcelable("from", fromLocation)
            val fragment: Fragment = SearchResultsFragment()
            fragment.arguments = bundle
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .addToBackStack(TAG)
                    .commit()
        })
    }

    private fun editFromListener(){
        etFrom?.setOnClickListener {

            toSelected = false
            // Initialize the SDK
            context?.let { Places.initialize(it, getString(R.string.google_api_key)) }

            // Create a new Places client instance.
            val placesClient: PlacesClient? = context?.let { Places.createClient(it) }

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent = context?.let { it1 ->
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(it1)
            }
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun editToListener(){
        etTo?.setOnClickListener {

            toSelected = true
            // Initialize the SDK
            context?.let { Places.initialize(it, getString(R.string.google_api_key)) }

            // Create a new Places client instance.
            val placesClient: PlacesClient? = context?.let { Places.createClient(it) }
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent = context?.let { it1 ->
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(it1)
            }
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        //mComposeFragmentViewModel?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                if (place != null) {
                    Log.i(TAG, "Place: " + place.name + ", " + place.id + ", " + place.address + ", " + place.latLng)
                }
                val address = place?.address
                if(toSelected) {
                    etTo?.setText(address)
                }
                else {
                    etFrom?.setText(address)
                    fromLocation = place?.latLng?.latitude?.let { place?.latLng?.longitude?.let { it1 -> ParseGeoPoint(it, it1) } }
                }

                // do query with address
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status: Status? = data?.let { Autocomplete.getStatusFromIntent(it) }
                Log.i(TAG, status?.getStatusMessage())
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    companion object {
        private const val TAG = "SearchFragment"
    }

}
