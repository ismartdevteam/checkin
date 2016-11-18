package ismartdev.mn.checkin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ismartdev.mn.checkin.model.HomeImage;
import ismartdev.mn.checkin.util.Constants;
import ismartdev.mn.checkin.util.Services;


public class FullscreenActivity extends BaseActivity {
    private static final String TAG = "FullscreenActivity";
    private DatabaseReference mDataRef;
    private List<HomeImage> homeImages;
    private SliderLayout imageSlider;
    private FirebaseAuth mAuth;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_fullscreen);
        imageSlider = (SliderLayout) findViewById(R.id.slider);

        //add default image
        final TextSliderView textSliderView = new TextSliderView(FullscreenActivity.this);
        textSliderView.image(R.drawable.vlvt)
                .setScaleType(BaseSliderView.ScaleType.Fit);
        imageSlider.addSlider(textSliderView);

        homeImages = new ArrayList<HomeImage>();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startMainAc();

        } else {

            mDataRef = FirebaseDatabase.getInstance().getReference();

            mDataRef.child(Constants.home_images).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("Count ", "" + dataSnapshot.getChildrenCount());

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        HomeImage image = postSnapshot.getValue(HomeImage.class);
                        homeImages.add(image);
                        Log.e("Get Data", image.image);
                        addImages(image.image);
                    }
                    imageSlider.setDuration(6000);
                    imageSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("The read failed: ", databaseError.getMessage());
                }
            });


            mCallbackManager = CallbackManager.Factory.create();

            LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
            loginButton.setPublishPermissions(Arrays.asList("publish_actions"));
            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);

                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                    Toast.makeText(FullscreenActivity.this, R.string.com_facebook_image_download_unknown_error, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                    if (error instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                    }

                }
            });
        }
    }

    private void startMainAc() {
        startActivity(new Intent(FullscreenActivity.this, MainActivity.class));
    }


    private void addImages(String url) {

        final TextSliderView textSliderView = new TextSliderView(FullscreenActivity.this);
        textSliderView.image(url)
                .setScaleType(BaseSliderView.ScaleType.Fit);
        imageSlider.addSlider(textSliderView);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FullscreenActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
//                            saveLikesAndFriendList(token);
                            startMainAc();
                        }

                        hideProgressDialog();
                    }
                });
    }

//    private void saveLikesAndFriendList( AccessToken token) {
//        GraphRequest request = GraphRequest.newMeRequest(
//                token,
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(
//                            JSONObject object,
//                            GraphResponse response) {
//                        Log.e("data", object.toString() + "");
//                        try {
//                            Services.addLikesToFirebase(object.getJSONObject("friends").getJSONArray("data"), mDataRef, getUid());
//                            startMainAc();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "likes,friends");
//        request.setParameters(parameters);
//        request.executeAsync();
//    }

}
