package ismartdev.mn.checkin.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import ismartdev.mn.checkin.R;
import ismartdev.mn.checkin.model.Events;
import ismartdev.mn.checkin.util.Constants;
import ismartdev.mn.checkin.util.EventsViewHolder;
import ismartdev.mn.checkin.util.FirebaseRecyclerAdapter;

public class EventsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    FirebaseRecyclerAdapter<Events, EventsViewHolder> mAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EventsFragment newInstance(int columnCount) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.events).orderByChild("online").getRef();

        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(true);
            mAdapter = new FirebaseRecyclerAdapter<Events, EventsViewHolder>(Events.class, R.layout.fragment_event, EventsViewHolder.class, ref) {
                @Override
                protected void populateViewHolder(EventsViewHolder viewHolder, Events model, int position) {
                    viewHolder.setViews(getActivity(), model);
                }
            };
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


}
