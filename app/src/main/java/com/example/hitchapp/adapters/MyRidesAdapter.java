package com.example.hitchapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hitchapp.R;
import com.example.hitchapp.fragments.ProfileFragment;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.parse.ParseFile;

import java.util.List;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder> {

    private static final String TAG = "RidesAdapter";
    private Context context;
    private List<Ride> rides;

    public MyRidesAdapter(Context context, List<Ride> rides) {
        this.context = context;
        this.rides = rides;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.bind(ride);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        rides.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Ride> list) {
        rides.addAll(list);
        notifyDataSetChanged();
    }

    public void setAll(List<Ride> list) {
        clear();
        addAll(list);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvFirstName;
        private TextView tvLastName;
        private ImageView ivProfilePicture;
        private TextView tvFrom;
        private TextView tvTo;
        private TextView tvDepartureTime;
        private TextView tvDepartureDate;
        private TextView tvPrice;
        protected Button btnRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFirstName = itemView.findViewById(R.id.tvFirstName);
            tvLastName = itemView.findViewById(R.id.tvLastName);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvDepartureDate = itemView.findViewById(R.id.tvDepartureDate);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicure);

            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);

            // Listens for driver profile pic clicked
            profilePicListener();

        }

        public void bind(Ride ride) {
            // Bind the ride data to the view elements
            User user = (User) ride.getDriver();
            tvFirstName.setText(user.getFirstName());
            tvLastName.setText(user.getLastName());
            tvFrom.setText(ride.getFrom());
            tvTo.setText(ride.getTo());
            tvDepartureTime.setText(ride.getDepartureTime());
            tvDepartureDate.setText(ride.getDepartureDate());
            tvPrice.setText(String.valueOf(ride.getPrice()));

            ParseFile profile = user.getProfilePicture();
            if (profile != null) {
                Glide.with(context)
                        .load(profile.getUrl())
                        .fitCenter()
                        .circleCrop()
                        .into(ivProfilePicture);
            }
        }

        @Override
        public void onClick(View v) {

        }

        // When someone's profile pic gets clicked you get taken to their profile
        private void profilePicListener() {
            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "clicked on profile pic");
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the ride at the position, this won't work if the class is static
                        Ride ride = rides.get(position);
                        Bundle bundle = new Bundle();
                        User user = (User) ride.getDriver();
                        Log.i(TAG, String.valueOf(user));
                        bundle.putParcelable("user", user);
                        Fragment fragment = new ProfileFragment();
                        fragment.setArguments(bundle);
                        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContainer, fragment)
                                .addToBackStack(TAG)
                                .commit();
                    }
                }
            });
        }


    }
}
