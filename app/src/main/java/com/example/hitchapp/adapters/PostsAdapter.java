package com.example.hitchapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hitchapp.R;
import com.example.hitchapp.fragments.DriverProfileFragment;
import com.example.hitchapp.models.Post;
import com.example.hitchapp.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String TAG = "PostsAdapter";
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public void setAll(List<Post> list){
        clear();
        addAll(list);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvFirstName;
        private TextView tvLastName;
        private ImageView ivProfilePicture;
        private TextView tvFrom;
        private TextView tvTo;
        private TextView tvDepartureTime;
        private TextView tvDepartureDate;
        private TextView tvPrice;

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

        public void bind(Post post) {
            // Bind the post data to the view elements
            User user = (User) post.getDriver();
            tvFirstName.setText(user.getFirstName());
            tvLastName.setText(user.getLastName());
            tvFrom.setText(post.getFrom());
            tvTo.setText(post.getTo());
            tvDepartureTime.setText(post.getDepartureTime());
            tvDepartureDate.setText(post.getDepartureDate());
            tvPrice.setText(String.valueOf(post.getPrice()));

            ParseFile profile = user.getProfilePicture();
            if(profile != null) {
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
        private void profilePicListener(){
            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"clicked on profile pic");
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if(position != RecyclerView.NO_POSITION) {
                        // Get the post at the position, this won't work if the class is static
                        Post post = posts.get(position);
                        Bundle bundle = new Bundle();
                        User user = (User) post.getDriver();
                        Log.i(TAG, String.valueOf(user));
                        bundle.putParcelable("user", user);
                        Fragment fragment = new DriverProfileFragment();
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
