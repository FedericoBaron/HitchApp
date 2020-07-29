package com.example.hitchapp.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hitchapp.R
import com.example.hitchapp.fragments.ProfileFragment
import com.example.hitchapp.models.Request
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.text.SimpleDateFormat

class RidesAdapter(private val context: Context, private val rides: MutableList<Ride>) : RecyclerView.Adapter<RidesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ride, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ride = rides[position]
        holder.bind(ride)
    }

    override fun getItemCount(): Int {
        return rides.size
    }

    // Clean all elements of the recycler
    fun clear() {
        rides.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(list: Collection<Ride?>) {
        rides.addAll(list as Collection<Ride>)
        notifyDataSetChanged()
    }

    fun setAll(list: List<Ride?>?) {
        clear()
        list?.let { addAll(it) }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val tvFirstName: TextView = itemView.findViewById(R.id.tvFirstName)
        private val tvLastName: TextView = itemView.findViewById(R.id.tvLastName)
        private val ivProfilePicture: ImageView = itemView.findViewById(R.id.ivProfilePicure)
        private val tvFrom: TextView = itemView.findViewById(R.id.tvFrom)
        private val tvTo: TextView = itemView.findViewById(R.id.tvTo)
        private val tvDepartureTime: TextView = itemView.findViewById(R.id.tvDepartureTime)
        private val tvDepartureDate: TextView = itemView.findViewById(R.id.tvDepartureDate)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvPerPerson: TextView = itemView.findViewById(R.id.tvPerPerson)
        private val tvSeatsAvailable: TextView = itemView.findViewById(R.id.tvSeatsAvailable)
        private var btnRequest: Button = itemView.findViewById(R.id.btnRequest)

        fun bind(ride: Ride) {
            val dateFor = SimpleDateFormat("MM/dd/yyyy")

            // Bind the ride data to the view elements
            val user = ride.driver as User?
            tvFirstName.text = user!!.firstName
            tvLastName.text = user.lastName
            tvFrom.text = ride.from
            tvTo.text = ride.to
            tvDepartureTime.text = ride.departureTime
            tvDepartureDate.text = dateFor.format(ride.departureDate)
            tvPrice.text = ride.price.toString()

            // fetch car data
            try {
                (ride.driver as User?)?.car?.fetch<ParseObject>()
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't fetch car", e)
            }
            var seatsAvailableText = ride.seatsAvailable.toString() + "/" + (ride?.driver as User?)?.car?.carCapacity.toString()
            tvSeatsAvailable.text = seatsAvailableText
            if (ParseUser.getCurrentUser().objectId == ride.driver!!.objectId) btnRequest.visibility = View.GONE else {
                btnRequest.visibility = View.VISIBLE
            }
            if (ride.pricePerParticipant) {
                tvPerPerson.visibility = View.VISIBLE
            } else {
                tvPerPerson.visibility = View.GONE
            }
            val profile = user.profilePicture
            if (profile != null) {
                Glide.with(context)
                        .load(profile.url)
                        .fitCenter()
                        .circleCrop()
                        .into(ivProfilePicture)
            }
        }

        override fun onClick(v: View) {}

        // When someone's profile pic gets clicked you get taken to their profile
        private fun profilePicListener() {
            ivProfilePicture.setOnClickListener { v ->
                val position = adapterPosition
                // Make sure the position is valid i.e actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // Get the ride at the position, this won't work if the class is static
                    val ride = rides[position]
                    val bundle = Bundle()
                    val user = ride.driver as User?
                    Log.i(TAG, user.toString())
                    bundle.putParcelable("user", user)
                    val fragment: Fragment = ProfileFragment()
                    fragment.arguments = bundle
                    (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(TAG)
                            .commit()
                }
            }
        }

        private fun btnRequestListener() {
            btnRequest.setOnClickListener(View.OnClickListener {
                Log.i(TAG, "clicked on request ride")
                val position = adapterPosition
                // Make sure the position is valid i.e actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // Get the ride at the position, this won't work if the class is static
                    val ride = rides[position]
                    val currentUser = ParseUser.getCurrentUser() as User
                    val participantList = ride.getList<User>("participants")
                    Log.i(TAG, participantList!!.size.toString())
                    for (i in participantList.indices) {
                        Log.i(TAG, "welcome part")
                        try {
                            participantList[i].fetch()
                            Log.i(TAG, participantList[i].objectId)
                            if (currentUser.objectId == participantList[i].objectId) {
                                Toast.makeText(context, "You are already a participant", Toast.LENGTH_SHORT).show()
                                Log.i(TAG, "WELCOME HOME")
                                return@OnClickListener
                            }
                        } catch (e: ParseException) {
                            Log.e(TAG, "exception fetching participants", e)
                        }
                    }
                    val query = ParseQuery.getQuery(Request::class.java)
                    query.whereEqualTo("ride", ride)
                    query.whereEqualTo("requester", currentUser)
                    query.include("ride")

                    // Finds the posts asynchronously
                    query.getFirstInBackground { `object`, e ->
                        if (`object` == null) {
                            val request = Request()
                            request.requester = currentUser
                            request.ride = ride
                            request.driver = ride.driver
                            save(request)
                        } else {
                            Toast.makeText(context, "You already requested to join this ride", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        private fun save(request: Request) {
            request.saveInBackground { e ->
                if (e != null) {
                    Log.e(TAG, "Error while saving", e)
                    //Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "update save was successful!")
                Toast.makeText(context, "You have requested to join this ride", Toast.LENGTH_SHORT).show()
            }
        }

        init {

            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this)

            // Listens for driver profile pic clicked
            profilePicListener()

            // Listens for when someone requests to join ride
            btnRequestListener()
        }
    }

    companion object {
        private const val TAG = "RidesAdapter"
    }

}