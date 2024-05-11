package com.example.roplabda_bajnoksag;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> implements Filterable {
    private ArrayList<MatchItem> mMatchItemData;
    private ArrayList<MatchItem> mMatchItemDataAll;
    private Context mContext;
    private int lastPosition = -1;

    public MatchesAdapter(Context context, ArrayList<MatchItem> matchData) {
        this.mMatchItemData = matchData;
        this.mMatchItemDataAll = matchData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_match, parent, false));
    }

    @Override
    public void onBindViewHolder(MatchesAdapter.ViewHolder holder, int position) {
        MatchItem currentMatch = mMatchItemData.get(position);

        holder.bindTo(currentMatch);

        if(holder.getAbsoluteAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }

    }

    @Override
    public int getItemCount() {
        return mMatchItemData.size();
    }

    @Override
    public Filter getFilter() {return matchFilter;}

    private Filter matchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<MatchItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
              results.count = mMatchItemDataAll.size();
              results.values = mMatchItemDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(MatchItem item : mMatchItemDataAll) {
                    if(item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mMatchItemData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mMatchImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.matchTitle);
            mInfoText = itemView.findViewById(R.id.matchTime);
            mPriceText = itemView.findViewById(R.id.price);
            mMatchImage = itemView.findViewById(R.id.matchImage);


        }

        public void bindTo(MatchItem currentMatch) {
            mTitleText.setText(currentMatch.getName());
            mInfoText.setText(currentMatch.getInfo());
            mPriceText.setText(currentMatch.getPrice());

            Glide.with(mContext).load(currentMatch.getImageResource()).into(mMatchImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(v -> {
                Log.d("Activity", "Add to cart button clicked!");
                ((MatchListActivity)mContext).updateAlertIcon(currentMatch);
                itemView.findViewById(R.id.delete).setOnClickListener(view -> ((MatchListActivity) mContext).deleteItem(currentMatch));
            });
        }
    };
};



