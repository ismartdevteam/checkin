package ismartdev.mn.checkin.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ismartdev.mn.checkin.R;
import ismartdev.mn.checkin.model.Events;

/**
 * Created by Ulzii on 11/13/2016.
 */

public class EventsViewHolder extends RecyclerView.ViewHolder {
    TextView nameText;
    TextView descText;
    TextView dateText;
    TextView statusText;
    ImageView eventImage;
    CircleImageView placeImage;

    public EventsViewHolder(View itemView) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.event_name);
        descText = (TextView) itemView.findViewById(R.id.event_desc);
        dateText = (TextView) itemView.findViewById(R.id.event_date);
        statusText = (TextView) itemView.findViewById(R.id.event_status);
        eventImage = (ImageView) itemView.findViewById(R.id.event_image);
        placeImage = (CircleImageView) itemView.findViewById(R.id.place_image);

    }


    public void setViews(Context context, Events events) {
        Picasso.with(context).load(events.place_image).into(placeImage);
        Log.e("image",events.image);
        Picasso.with(context).load(events.image).into(eventImage);
        nameText.setText(events.name);
        descText.setText(events.description);
        dateText.setText(events.start_date + " ~ " + events.expire_date);
        if (events.online) {
            statusText.setText(R.string.online);
            statusText.setBackgroundResource(R.drawable.online_stat);
        } else {
            statusText.setText(R.string.offline);
            statusText.setBackgroundResource(R.drawable.offline_stat);
        }
    }

}
