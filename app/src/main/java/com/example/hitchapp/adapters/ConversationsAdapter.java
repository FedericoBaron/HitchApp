package com.example.hitchapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hitchapp.R;
import com.example.hitchapp.fragments.MessagesFragment;
import com.example.hitchapp.models.Conversation;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private static final String TAG = "ConversationsAdapter";
    private Context context;
    private List<Conversation> conversations;

    public ConversationsAdapter(Context context, List<Conversation> conversations){
        this.context = context;
        this.conversations = conversations;
        Log.i(TAG, "Constructor entered");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation);
        Log.i(TAG, "Bind entered");

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        conversations.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Conversation> list) {
        conversations.addAll(list);
        notifyDataSetChanged();
    }

    public void setAll(List<Conversation> list){
        clear();
        addAll(list);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvGroupName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            Log.i(TAG, "I'm in here");

            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);

        }

        public void bind(Conversation conversation) {
            Log.i(TAG, "im in here");
            tvGroupName.setText(conversation.getGroupName());
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "OnClick adapter");
            // Gets item position
            int position = getAdapterPosition();

            // Make sure the position is valid i.e actually exists in the view
            if(position != RecyclerView.NO_POSITION) {
                // Get the post at the position, this won't work if the class is static
                Conversation conversation = conversations.get(position);

                Bundle bundle = new Bundle();
                JSONArray messages = conversation.getMessages();
                bundle.putParcelable("messages", Parcels.wrap(messages));
                Fragment fragment = new MessagesFragment();
                fragment.setArguments(bundle);
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .commit();
            }
        }
    }

}
