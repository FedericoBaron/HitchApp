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
import com.example.hitchapp.models.Request;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private static final String TAG = "RequestsAdapter";
    private Context context;
    private List<Request> requests;

    public RequestsAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Request> list) {
        requests.addAll(list);
        notifyDataSetChanged();
    }

    public void setAll(List<Request> list) {
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
        protected Button btnAccept;
        protected Button btnDecline;

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
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);

            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);

            // Listens for driver profile pic clicked
            profilePicListener();

            // Listens when accept is clicked
            btnAcceptListener();

            // Listens when decline is clicked
            btnDeclineListener();

        }

        public void bind(Request request) {
            // Bind the ride data to the view elements
            User requester = (User) request.getRequester();
            Ride ride = (Ride) request.getRide();
            try {
                requester.fetch();
                ride.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvFirstName.setText(requester.getFirstName());
            tvLastName.setText(requester.getLastName());
            tvFrom.setText(ride.getFrom());
            tvTo.setText(ride.getTo());
            tvDepartureTime.setText(ride.getDepartureTime());
            tvDepartureDate.setText(ride.getDepartureDate());
            tvPrice.setText(String.valueOf(ride.getPrice()));

            ParseFile profile = requester.getProfilePicture();
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

        private void btnAcceptListener(){
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        Request request = requests.get(position);
                        Ride ride = request.getRide();
                        JSONArray participants = ride.getParticipants();
                        participants.put(request.getRequester());
                        ride.setParticipants(participants);
                        requests.remove(request);
                        request.deleteInBackground();
                        save(ride);
                        notifyItemRemoved(position);
                    }
                }
            });
        }

        private void btnDeclineListener(){
            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        Request request = requests.get(position);
                        requests.remove(request);
                        request.deleteInBackground();
                        notifyItemRemoved(position);
                    }
                }
            });
        }

        private void save(Ride ride) {
            ride.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving", e);
                        //Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "update save was successful!");

                }
            });
        }

        // When someone's profile pic gets clicked you get taken to their profile
        private void profilePicListener() {
            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the ride at the position, this won't work if the class is static
                        Request request = requests.get(position);
                        Bundle bundle = new Bundle();
                        User requester = (User) request.getRequester();
                        Log.i(TAG, String.valueOf(requester));
                        bundle.putParcelable("user", requester);
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
