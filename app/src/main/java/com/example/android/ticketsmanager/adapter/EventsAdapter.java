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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnTryAgainDispatcher {
        void tryAgain();
    }

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private final OnTryAgainDispatcher tryAgainDispatcher;
    private List<EventInfo> events;
    private NetworkStateItemViewHolder networkStateItemViewHolder;

    public EventsAdapter(OnTryAgainDispatcher tryAgainDispatcher){
        this.tryAgainDispatcher = tryAgainDispatcher;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() || events.get(position) == null) {
            return TYPE_PROGRESS;
        }
        else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    public void setEvents(List<EventInfo> events){
        int eventsCount = getItemCount();

        if(eventsCount  == 0){
            this.events = events;
            notifyDataSetChanged();
            return;
        }

        int lastItemIdx = getItemCount() - 1;

        if(this.events.get(eventsCount  - 1) == null){
            this.events.remove(lastItemIdx);
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

        if(viewType == TYPE_PROGRESS){
            return new NetworkStateItemViewHolder(inflater.inflate(R.layout.network_state_item, parent, false));
        }

        View listItem = inflater.inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof NetworkStateItemViewHolder){
            bindProgress((NetworkStateItemViewHolder) holder);
            return;
        }

        EventViewHolder eventHolder = (EventViewHolder)holder;

        EventInfo event = events.get(position);
        eventHolder.textView.setText(event.getEventName());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMMM dd", Locale.ENGLISH);

        Date startDate = event.getStartDate();
        if(startDate != null) {
            eventHolder.eventDate.setText(dateFormat.format(startDate.getTime()));
            eventHolder.eventTime.setText(timeFormat.format(startDate.getTime()));
        }
        else {
            eventHolder.eventTime.setText("TBA");
        }

        eventHolder .eventLocation.setText(
                String.format("%s\n%s, %s",
                        event.getLocationName(),
                    event.getCountryName(),
                        event.getCityName()
                )
        );

        List<com.example.android.ticketsmanager.db.Image> images = event.getImages();
        if(images.isEmpty()){
            eventHolder .eventImage.setImageResource(R.drawable.no_image_available);
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
                    .into(eventHolder .eventImage);
        }
    }

    private void bindProgress(NetworkStateItemViewHolder holder){
        networkStateItemViewHolder = holder;
        holder.progressBar.setVisibility(View.VISIBLE);
    }

    public void clearList() {
        events.clear();
        notifyDataSetChanged();
    }

    public void setNetworkState(NetworkState state){
        int itemsCount = getItemCount();
        if(itemsCount != 0 && events.get(itemsCount - 1) != null && state == NetworkState.RUNNING){
            events.add(null);
            notifyItemInserted(itemsCount);
        }
        else if(state == NetworkState.FAILED && networkStateItemViewHolder != null){
            networkStateItemViewHolder.showErrorState();
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

            tryAgainButton.setOnClickListener(
                    v -> {
                        showOnLoading();
                        tryAgainDispatcher.tryAgain();
                    }
            );
        }

        public void showOnLoading(){
            progressBar.setVisibility(View.VISIBLE);
            errorMsgView.setVisibility(View.INVISIBLE);
            tryAgainButton.setVisibility(View.INVISIBLE);
        }

        public void showErrorState(){
            progressBar.setVisibility(View.INVISIBLE);
            errorMsgView.setVisibility(View.VISIBLE);
            tryAgainButton.setVisibility(View.VISIBLE);
        }
    }
}
