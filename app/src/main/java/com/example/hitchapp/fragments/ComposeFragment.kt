package com.example.hitchapp.fragments
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
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
        btnPost = view.findViewById(R.id.btnPost)
        switchPricePerParticipant = view.findViewById(R.id.switchPricePerParticipant)

        editDepartureTimeListener()
        btnPostListener()

    }

    private fun btnPostListener(){
        btnPost?.setOnClickListener(View.OnClickListener {

            // Define variables to check if empty
            val from = etFrom?.text.toString()
            val to = etTo?.text.toString()
            val price = etPrice?.text.toString()
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
            if (departureTime.isEmpty()) {
                Toast.makeText(context, "departure time cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }


            // Gets the person who's logged in
            val currentUser = ParseUser.getCurrentUser() as User

            savePost(from, to, price, departureTime, currentUser)
        })
    }

    private fun editDepartureTimeListener(){
        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            etDepartureTime?.setText(sdf.format(cal.time))

        }

        etDepartureTime?.setOnClickListener(View.OnClickListener {
            context?.let { it1 ->
                DatePickerDialog(it1, dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })
    }

    private fun savePost(from: String, to: String, price: String, departureTime: String, currentUser: ParseUser) {
        val post = Post()
        post.price = price.toInt()
        post.from = from
        post.to = to
        post.driver = currentUser
        post.departureTime = departureTime
        post.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "Post save was successful!")
            // TODO switch to home screen

        }
    }

    companion object {
        private const val TAG = "ComposeFragment"
    }
}