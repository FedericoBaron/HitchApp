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
import com.example.hitchapp.fragments.EditRideFragment
import com.example.hitchapp.fragments.MessagesFragment
import com.example.hitchapp.fragments.ProfileFragment
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.parse.ParseObject
import java.text.SimpleDateFormat

class MyRidesAdapter(private val context: Context, private val rides: MutableList<Ride>) : RecyclerView.Adapter<MyRidesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_ride, parent, false)
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

    fun getItem(position: Int): Ride? {
        return rides[position]
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

        private val dateFor: SimpleDateFormat? = SimpleDateFormat("MM/dd/yyyy")
        private val tvFirstName: TextView = itemView.findViewById(R.id.tvFirstName)
        private val tvLastName: TextView = itemView.findViewById(R.id.tvLastName)
        private val ivProfilePicture: ImageView = itemView.findViewById(R.id.ivProfilePicure)
        private val tvFrom: TextView = itemView.findViewById(R.id.tvFrom)
        private val tvTo: TextView = itemView.findViewById(R.id.tvTo)
        private val tvDepartureTime: TextView = itemView.findViewById(R.id.tvDepartureTime)
        private val tvDepartureDate: TextView = itemView.findViewById(R.id.tvDepartureDate)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        //private val btnChat: Button = itemView.findViewById(R.id.btnChat)
        private val tvState: TextView = itemView.findViewById(R.id.tvState)
        //private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val tvSeatsAvailable: TextView = itemView.findViewById(R.id.tvSeatsAvailable)
        private val tvPerPerson: TextView = itemView.findViewById(R.id.tvPerPerson)



        fun bind(ride: Ride) {
            // Bind the ride data to the view elements

            val user = ride.driver as User
            tvFirstName.text = user.firstName
            tvLastName.text = user.lastName
            tvFrom.text = ride.from
            tvTo.text = ride.to
            tvDepartureTime.text = ride.departureTime
            tvDepartureDate.text = dateFor?.format(ride.departureDate)
            tvPrice.text = ride.price.toString()
            tvState.text = ride.state
            val profile = user.profilePicture
            if (profile != null) {
                Glide.with(context)
                        .load(profile.url)
                        .fitCenter()
                        .circleCrop()
                        .into(ivProfilePicture)
            }

            // fetch car data
            try {
                (ride.driver as User?)?.car?.fetch<ParseObject>()
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't fetch car", e)
            }
            var seatsAvailableText = ride.seatsAvailable.toString() + "/" + (ride?.driver as User?)?.car?.carCapacity.toString()
            tvSeatsAvailable.text = seatsAvailableText

            // If price is per person then show it
            if (ride.pricePerParticipant) {
                tvPerPerson.visibility = View.VISIBLE
            } else {
                tvPerPerson.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {}

        // When someone's profile pic gets clicked you get taken to their profile
        private fun profilePicListener() {
            ivProfilePicture.setOnClickListener { v ->
                Log.i(TAG, "clicked on profile pic")
                val position = adapterPosition
                // Make sure the position is valid i.e actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // Get the ride at the position, this won't work if the class is static
                    val ride = rides[position]
                    val bundle = Bundle()
                    val user = ride.driver as User
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

        init {

            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this)

            // Listens for driver profile pic clicked
            profilePicListener()

            // Chat button listener
            //btnChatClickListener()

            //btnEditClickListener()
        }
    }

    companion object {
        private const val TAG = "RidesAdapter"
    }

}