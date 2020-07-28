package com.example.hitchapp.fragments
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.hitchapp.R
import com.example.hitchapp.viewmodels.ComposeFragmentViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.parse.ParseGeoPoint
import com.parse.SaveCallback
import java.text.SimpleDateFormat
import java.util.*


open class ComposeFragment : Fragment() {
    protected var etFrom: EditText? = null
    protected var etTo: EditText? = null
    protected var etDepartureTime: EditText? = null
    protected var etDepartureDate: EditText? = null
    protected var etPrice: EditText? = null
    protected var btnPost: Button? = null
    protected var switchPricePerParticipant: Switch? = null
    private val REQUEST_CODE = 20
    private var toSelected = false
    private var mComposeFragmentViewModel: ComposeFragmentViewModel? = null
    protected var fromLocation: ParseGeoPoint? = null
    protected var departureDate: Date? = null
    protected var newCal: Calendar = Calendar.getInstance()

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireUI()

        // Init ViewModel
        mComposeFragmentViewModel = ViewModelProviders.of(this).get(ComposeFragmentViewModel::class.java)
        mComposeFragmentViewModel?.init()

        editDepartureDateListener()
        editDepartureTimeListener()
        editFromListener()
        editToListener()
        btnPostRideListener()

    }

    protected fun wireUI(){
        etFrom = view?.findViewById(R.id.etFrom);
        etTo = view?.findViewById(R.id.etTo);
        etPrice = view?.findViewById(R.id.etPrice);
        etDepartureTime = view?.findViewById(R.id.etDepartureTime)
        etDepartureDate = view?.findViewById(R.id.etDepartureDate)
        btnPost = view?.findViewById(R.id.btnPost)
        switchPricePerParticipant = view?.findViewById(R.id.switchPricePerParticipant)
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

            saveRide(from, to, price, departureDate, departureTime)
        })
    }


    // Save ride to the backend
    private fun saveRide(from: String, to: String, price: String, departureDate: Date?, departureTime: String) {
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

        mComposeFragmentViewModel?.saveRide(from, to, price, departureDate, departureTime, fromLocation, saveRideCallback)

        // Changes to home fragment
        val fragment: Fragment = HomeFragment()
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit()
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
            if (resultCode == RESULT_OK) {
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

    private fun editDepartureDateListener(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Edit departure time button clicked
        etDepartureDate?.setOnClickListener(View.OnClickListener {
            val dpd = context?.let { it1 ->
                DatePickerDialog(it1, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Set to textView
                    //departureDate = Date(year, month, dayOfMonth)

                    newCal.set(Calendar.YEAR, year)
                    newCal.set(Calendar.MONTH, monthOfYear)
                    newCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    departureDate = newCal.time
                    var month = monthOfYear +1
                    etDepartureDate?.setText("" + month + "/" + dayOfMonth + "/" + year)
                }, year, month, day)
            }

            // show dialog
            dpd?.show()

            // Set colors of OK and CANCEL for datepicker
            dpd?.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setBackgroundColor(resources.getColor(R.color.colorNegative))
            dpd?.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setBackgroundColor(resources.getColor(R.color.colorPositive))
        })
    }

    private fun editDepartureTimeListener(){
        etDepartureTime?.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                newCal.set(Calendar.HOUR_OF_DAY, hour)
                newCal.set(Calendar.MINUTE, minute)
                departureDate = newCal.time
                // Sets AM or PM accordingly
                var amOrPm: String = if(hour >= 12)
                    "PM"
                else{
                    "AM"
                }
                etDepartureTime?.setText("@" + SimpleDateFormat("HH:mm").format(departureDate) + amOrPm)
            }
            val dpd = TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            dpd.show()
            dpd?.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setBackgroundColor(resources.getColor(R.color.colorNegative))
            dpd?.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setBackgroundColor(resources.getColor(R.color.colorPositive))
        }
    }

    companion object {
        private const val TAG = "ComposeFragment"
    }
}