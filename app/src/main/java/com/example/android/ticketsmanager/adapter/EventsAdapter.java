package com.example.android.ticketsmanager.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.ticketsmanager.R;
import com.example.android.ticketsmanager.db.EventInfo;
import com.example.android.ticketsmanager.utils.NetworkState;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private List<EventInfo> events;
    private NetworkState networkState;
    private NetworkStateItemViewHolder networkHolder;
    
    public EventsAdapter(){
        events = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if(
                position == getItemCount() - 1 &&
                networkState != null &&
                networkState == NetworkState.LOADING) {
            return TYPE_PROGRESS;
        }
        else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<EventInfo> events){
        if(events.isEmpty()){
            this.events.clear();
            notifyDataSetChanged();
            return;
        }

        EventInfo.Comparator comparator = new EventInfo.Comparator(this.events, events);
        DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(comparator);
        this.events = events;
        notifyDataSetChanged();
        productDiffResult.dispatchUpdatesTo(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if(viewType == TYPE_ITEM) {
            View listItem = inflater.inflate(R.layout.event_list_item, parent, false);
            return new EventViewHolder(listItem);
        }

        View loadItem = inflater.inflate(R.layout.network_state_item, parent, false);
        return new NetworkStateItemViewHolder(loadItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EventViewHolder){
            bindEventList((EventViewHolder)holder, position);
        }
        else {
            bindProgress((NetworkStateItemViewHolder)holder);
        }
    }

    private void bindEventList(EventViewHolder holder, int position){
        EventInfo event = events.get(position);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMMM dd", Locale.ENGLISH);

        holder.textView.setText(event.getEventName());
        Date startDate = event.getStartDate();
        if(startDate != null) {
            holder.eventDate.setText(dateFormat.format(startDate.getTime()));
            holder.eventTime.setText(timeFormat.format(startDate.getTime()));
        }
        else {
            holder.eventTime.setText("TBA");
        }
        holder.eventLocation.setText(
                String.format("%s\n%s, %s",
                        event.getLocationName(),
                        event.getCountryName(),
                        event.getCityName()
                )
        );

        List<com.example.android.ticketsmanager.db.Image> images = event.getImages();
        if(images.isEmpty()){
            holder.eventImage.setImageResource(R.drawable.no_image_available);
        }
        else {

            String imageUrl = images.get(0).getUrl();

            int nImages = images.size();

            for(int i = 0; i < nImages; ++i) {
                com.example.android.ticketsmanager.db.Image current = images.get(i);

                if(current.getWidth() == 1024) {
                    imageUrl = current.getUrl();
                    break;
                }
            }

            Picasso picasso = Picasso.get();
            picasso.load(imageUrl)
                    .placeholder(R.drawable.no_image_available)
                    .into(holder.eventImage);
        }
    }

    private void bindProgress(NetworkStateItemViewHolder holder){
        networkHolder = holder;
        
        if(networkState == null || networkState == NetworkState.LOADING) {
            holder.progressBar.setVisibility(View.VISIBLE);
            return;
        }
        
        if(networkState == NetworkState.FAIL) {
            holder.errorMsgView.setVisibility(View.VISIBLE);
            holder.errorMsgView.setText(networkState.getMsg());
        } else {
            holder.errorMsgView.setVisibility(View.GONE);
        }
    }

    public void clearList() {
        events.clear();
        networkHolder = null;
        notifyDataSetChanged();
    }

    public void setNetworkState(NetworkState networkState) {
        this.networkState = networkState;
        if(this.networkState == NetworkState.FAIL){
            updateNetworkHolder();
        }
    }

    private void updateNetworkHolder() {
        if(networkHolder != null){
            networkHolder.progressBar.setVisibility(View.INVISIBLE);

            networkHolder.tryAgainButton.setVisibility(View.VISIBLE);
            networkHolder.errorMsgView.setVisibility(View.VISIBLE);
            networkHolder.errorMsgView.setText(networkState.getMsg());
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final TextView eventDate;
        private final TextView eventLocation;
        private final ImageView eventImage;
        private final TextView eventTime;

        public EventViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.eventTitleTextView);
            eventDate = itemView.findViewById(R.id.eventDateTextView);
            eventImage = itemView.findViewById(R.id.eventImageView);
            eventLocation = itemView.findViewById(R.id.eventLocationTextView);
            eventTime = itemView.findViewById(R.id.eventTimeTextView);
        }
    }

    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar progressBar;
        private TextView errorMsgView;
        private Button tryAgainButton;

        public NetworkStateItemViewHolder(View itemView) {
            super(itemView);

            tryAgainButton = itemView.findViewById(R.id.tryAgainButton);
            progressBar = itemView.findViewById(R.id.progressBarView);
            errorMsgView = itemView.findViewById(R.id.errorMsgTextView);
        }
    }
}
