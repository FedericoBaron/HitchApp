package com.example.hitchapp.fragments
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.hitchapp.R
import com.example.hitchapp.models.Post
import com.example.hitchapp.models.User
import com.parse.ParseUser
import java.text.SimpleDateFormat
import java.util.*


class ComposeFragment : Fragment() {
    private var etFrom: EditText? = null
    private var etTo: EditText? = null
    private var etDepartureTime: EditText? = null
    private var etDepartureDate: EditText? = null
    private var etPrice: EditText? = null
    private var btnPost: Button? = null
    private var switchPricePerParticipant: Switch? = null

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
        etFrom = view.findViewById(R.id.etFrom);
        etTo = view.findViewById(R.id.etTo);
        etPrice = view.findViewById(R.id.etPrice);
        etDepartureTime = view.findViewById(R.id.etDepartureTime)
        etDepartureDate = view.findViewById(R.id.etDepartureDate)
        btnPost = view.findViewById(R.id.btnPost)
        switchPricePerParticipant = view.findViewById(R.id.switchPricePerParticipant)

        editDepartureDateListener()
        editDepartureTimeListener()
        btnPostListener()

    }

    // Listens for when the post ride button gets clicked
    private fun btnPostListener(){
        btnPost?.setOnClickListener(View.OnClickListener {

            // Define variables to check if empty
            val from = etFrom?.text.toString()
            val to = etTo?.text.toString()
            val price = etPrice?.text.toString()
            val departureDate = etDepartureDate?.text.toString()
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
            if (departureDate.isEmpty()) {
                Toast.makeText(context, "departure time cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (departureTime.isEmpty()) {
                Toast.makeText(context, "departure time cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }


            // Gets the person who's logged in
            val currentUser = ParseUser.getCurrentUser() as User

            // Save post to the backend
            savePost(from, to, price, departureDate, departureTime, currentUser)
        })
    }

    private fun editDepartureDateListener(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Edit departure time button clicked
        etDepartureDate?.setOnClickListener(View.OnClickListener {
            val dpd = context?.let { it1 ->
                DatePickerDialog(it1, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    // Set to textView
                    etDepartureDate?.setText("" + mDay + "/" + mMonth + "/" + mYear)
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

                // Sets AM or PM accordingly
                var amOrPm = ""
                amOrPm = if(cal.get(hour) >= 12)
                    "PM"
                else{
                    "AM"
                }
                etDepartureTime?.setText("@" + SimpleDateFormat("HH:mm").format(cal.time) + amOrPm)
            }
            val dpd = TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            dpd.show()
            dpd?.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setBackgroundColor(resources.getColor(R.color.colorNegative))
            dpd?.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setBackgroundColor(resources.getColor(R.color.colorPositive))
        }
    }


    // Adds post to the database
    private fun savePost(from: String, to: String, price: String, departureDate: String, departureTime: String, currentUser: ParseUser) {
        val post = Post()
        post.price = price.toInt()
        post.from = from
        post.to = to
        post.driver = currentUser
        post.departureDate = departureDate
        post.departureTime = departureTime

        post.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "Post save was successful!")

            // Empties all edit text forms for next time
            etFrom?.setText("")
            etPrice?.setText("")
            etTo?.setText("")
            etDepartureDate?.setText("")
            etDepartureTime?.setText("")

            // Changes to home fragment
            val fragment: Fragment = HomeFragment()
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit()

        }
    }

    companion object {
        private const val TAG = "ComposeFragment"
    }
}