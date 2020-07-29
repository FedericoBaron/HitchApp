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
import java.text.SimpleDateFormat

class RequestsAdapter(private val context: Context, private val requests: MutableList<Request>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    // Clean all elements of the recycler
    fun clear() {
        requests.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(list: Collection<Request?>) {
        requests.addAll(list as Collection<Request>)
        notifyDataSetChanged()
    }

    fun setAll(list: List<Request?>?) {
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
        private var btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private var btnDecline: Button = itemView.findViewById(R.id.btnDecline)
        private var dateFor = SimpleDateFormat("MM/dd/yyyy")

        fun bind(request: Request) {
            // Bind the ride data to the view elements
            val requester = request.requester as User?
            val ride = request.ride
            try {
                requester!!.fetch()
                ride!!.fetch<ParseObject>()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            tvFirstName.text = requester!!.firstName
            tvLastName.text = requester.lastName
            tvFrom.text = ride!!.from
            tvTo.text = ride.to
            tvDepartureTime.text = ride.departureTime
            tvDepartureDate.text = dateFor.format(ride.departureDate)
            tvPrice.text = ride.price.toString()

            // Set profile picture with Glide
            val profile = requester.profilePicture
            if (profile != null) {
                Glide.with(context)
                        .load(profile.url)
                        .fitCenter()
                        .circleCrop()
                        .into(ivProfilePicture)
            }

            // If price is per person then show it
            if (ride.pricePerParticipant) {
                tvPerPerson.visibility = View.VISIBLE
            }
            // Otherwise don't show it
            else {
                tvPerPerson.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {}

        // Listener for accept button click
        private fun btnAcceptListener() {
            btnAccept.setOnClickListener {
                val position = adapterPosition
                // Make sure the position is valid i.e actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    val request = requests[position]
                    val ride = request.ride
                    val participants = ride!!.participants
                    participants!!.put(request.requester)
                    ride.participants = participants
                    ride.seatsAvailable--
                    requests.remove(request)
                    request.deleteInBackground()
                    save(ride)
                    notifyItemRemoved(position)
                }
            }
        }

        private fun btnDeclineListener() {
            btnDecline.setOnClickListener {
                val position = adapterPosition
                // Make sure the position is valid i.e actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    val request = requests[position]
                    requests.remove(request)
                    request.deleteInBackground()
                    notifyItemRemoved(position)
                }
            }
        }

        private fun save(ride: Ride?) {
            ride!!.saveInBackground { e ->
                if (e != null) {
                    Log.e(TAG, "Error while saving", e)
                    //Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "update save was successful!")
            }
        }

        // When someone's profile pic gets clicked you get taken to their profile
        private fun profilePicListener() {
            ivProfilePicture.setOnClickListener { v ->
                val position = adapterPosition
                // Make sure the position is valid i.e actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // Get the ride at the position, this won't work if the class is static
                    val request = requests[position]
                    val bundle = Bundle()
                    val requester = request.requester as User?
                    Log.i(TAG, requester.toString())
                    bundle.putParcelable("user", requester)
                    val fragment: Fragment = ProfileFragment()
                    fragment.arguments = bundle
                    (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(TAG)
                            .commit()
                }
            }
        }

        init {
            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this)

            // Listens for driver profile pic clicked
            profilePicListener()

            // Listens when accept is clicked
            btnAcceptListener()

            // Listens when decline is clicked
            btnDeclineListener()
        }
    }

    companion object {
        private const val TAG = "RequestsAdapter"
    }

}