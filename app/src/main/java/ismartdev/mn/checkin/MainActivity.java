package ismartdev.mn.checkin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import ismartdev.mn.checkin.fragment.EventsFragment;
import ismartdev.mn.checkin.model.Events;
import ismartdev.mn.checkin.model.Place;
import ismartdev.mn.checkin.util.CircleImageView;
import ismartdev.mn.checkin.util.Constants;
import ismartdev.mn.checkin.util.QrCodeReader;

public class MainActivity extends BaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private DatabaseReference mDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataRef = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_qr_scan_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).setOrientationLocked(false).setCaptureActivity(QrCodeReader.class).initiateScan();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, R.string.error_qrcode_read, Toast.LENGTH_LONG).show();
                Log.d("MainActivity", "Cancelled scan");
            } else {
                Log.d("MainActivity", "Scanned" + result.getContents());
                Query query = mDataRef.child(Constants.events).orderByChild(Constants.qr_code)

                            .startAt( result.getContents()).endAt(result.getContents());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Events events = dataSnapshot.getValue(Events.class);
                        if (events != null) {
                            Log.e("dasda", events.description);
//                            createCheckInDialog(place);

                        } else
                            Toast.makeText(getApplicationContext(), R.string.no_place, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), R.string.error_try_again, Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    private void createCheckInDialog(final Place place) {
//        final Dialog dialog = new Dialog(MainActivity.this);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(R.layout.place_dialog);
//        dialog.setCanceledOnTouchOutside(true);
//        TextView nameText = (TextView) dialog.findViewById(R.id.place_name);
//        ImageView coverImage = (ImageView) dialog.findViewById(R.id.place_coverImage);
//        CircleImageView profileImage = (CircleImageView) dialog.findViewById(R.id.place_profileImage);
//        final EditText place_text = (EditText) dialog.findViewById(R.id.place_text);
//        Button place_checkIn = (Button) dialog.findViewById(R.id.place_checkIn);
//
//        Picasso.with(MainActivity.this).load(place.profile_image).into(profileImage);
//        Picasso.with(MainActivity.this).load(place.cover_image).into(coverImage);
//        nameText.setText(place.name);
//
//        place_checkIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle params = new Bundle();
//                params.putString("place", place.id);
//                params.putString("message", place_text.getText() + "");
//                new GraphRequest(
//                        AccessToken.getCurrentAccessToken(),
//                        "/me/feed",
//                        params,
//                        HttpMethod.POST,
//                        new GraphRequest.Callback() {
//                            public void onCompleted(GraphResponse response) {
//                                Toast.makeText(MainActivity.this, response.getRawResponse(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                ).executeAsync();
//            }
//        });
//        dialog.show();
//    }
//

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return EventsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//                case 2:
//                    return "SECTION 3";
            }
            return null;
        }
    }
}
