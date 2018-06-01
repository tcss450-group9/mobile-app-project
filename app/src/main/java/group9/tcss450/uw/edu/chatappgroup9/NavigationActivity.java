package group9.tcss450.uw.edu.chatappgroup9;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterContact;
import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterRequest;
import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterSearchResult;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;
import group9.tcss450.uw.edu.chatappgroup9.utils.ThemeUtil;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LandingFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        ContactsFragment.OnFragmentInteractionListener,
        FriendsFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static int mTheme = ThemeUtil.THEME_MEDITERRANEAN_BLUES;
    private String[] myDummyValue = {"Little_dog", "little_cat", "big_turtle", "myDummyValue", "African buffalo", "Meles meles"};
    private SharedPreferences mySharedPreference;
    private String myMemberId;
    private String TAG = "NavigationActivity";
    private ArrayList<String> myContactList;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 814;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private LatLng mySelectedLocation;
    private String myZipCode;
    private boolean searchWeatherByMap;
    private boolean searchWeatherByCurrentLocation;
    private boolean searchWeatherByZip;

//    private DataUpdateReciever mDataUpdateReceiver;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To change theme just put your theme id.
        int theme = ThemeUtil.getThemeId(mTheme);
        setTheme(theme);

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void leaveButtonOnClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mySharedPreference = getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        if (mySharedPreference != null) {
            String username = mySharedPreference.getString(getString(R.string.keys_shared_prefs_username),
                    "unknown user");
            myMemberId = mySharedPreference.getString(getString(R.string.keys_shared_prefs_memberid),
                    "-1");

//            Log.e("NavigationActivity", "username: " + username);
            TextView naviHeaderUsername = navigationView.getHeaderView(0).findViewById(R.id.navigationHeaderTextViewUsername);

//            Log.e("NavigationActivity", "header : " + naviHeaderUsername);
            naviHeaderUsername.setText(username);
            this.setTitle("Husky Mingle");
        }

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,
                        new LandingFragment(), getString(R.string.keys_landing_fragment_tag))
                        .commit();
            }
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        }

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        searchWeatherByCurrentLocation = true;
        searchWeatherByMap = false;
        searchWeatherByZip = false;

        startService(new Intent(this, NotificationIntentService.class));

    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
        getFriendList();
    }

    /**
     * , edit onPause and onResume to start and stop the service in the “background” or “foreground”:
     **/
    @Override
    protected void onResume() {
        super.onResume();

//// Check to see if the service should aleardy be running
//        if (mySharedPreference.getBoolean(getString(R.string.keys_sp_on), false)) {
//            //stop the service from the background
//            NotificationIntentService.stopServiceAlarm(this);
//            //restart but in the foreground
//            NotificationIntentService.startServiceAlarm(this, true);
//        }
//
//
////            Log.e(TAG, "NotificationIntentService stop");
//        if (mDataUpdateReceiver == null) {
//            mDataUpdateReceiver = new DataUpdateReciever();
//        }
//        IntentFilter iFilter = new IntentFilter(NotificationIntentService.RECEIVED_UPDATE);
//        registerReceiver(mDataUpdateReceiver, iFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e(TAG, "NotificationIntentService start");
//        if (mySharedPreference.getBoolean(getString(R.string.keys_sp_on), false)) {
//            //stop the service from the foreground
//            NotificationIntentService.stopServiceAlarm(this);
//            //restart but in the background
//            NotificationIntentService.startServiceAlarm(this, false);
//        }
//
//
//        if (mDataUpdateReceiver != null){
//            unregisterReceiver(mDataUpdateReceiver);
//        }
//        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        if (mCurrentLocation == null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {
                    Log.i(TAG, mCurrentLocation.toString());
                }
                startLocationUpdates();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, mCurrentLocation.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down…maybe ask for permission again?
                    finishAndRemoveTask();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public Location getLocation() {
        return mCurrentLocation;
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_mediterranean_blues_theme) {
            changeTheme(ThemeUtil.THEME_MEDITERRANEAN_BLUES);
        }

        if (id == R.id.action_shimmering_blues_theme) {
            changeTheme(ThemeUtil.THEME_SHIMMERING_BLUES);
        }

        if (id == R.id.action_turquoise_red_theme) {
            changeTheme(ThemeUtil.THEME_TURQUOISE_WATERMELON);
        }

        if (id == R.id.action_orange_sunset_theme) {
            changeTheme(ThemeUtil.THEME_ORANGE_SUNSET);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeTheme(int theme) {
        // Handles theme changes to activity
        mTheme = theme;
        setTheme(mTheme);

        NavigationActivity.this.recreate();

        int duration = Toast.LENGTH_SHORT;
        Context context = this.getBaseContext();
        Toast toast = Toast.makeText(context, "Changed Theme", duration);
        toast.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_landing) {
            loadFragment(new LandingFragment(), getString(R.string.keys_landing_fragment_tag));
        } else if (id == R.id.nav_contact) {
            loadFragment(new ContactsFragment(), getString(R.string.keys_contact_fragment_tag));
        } else if (id == R.id.nav_weather) {
            loadFragment(new WeatherFragment(), getString(R.string.keys_weather_fragment_tag));
        } else if (id == R.id.nav_friends) {
            loadFriendsFragment();
        } else if (id == R.id.nav_logout) {
            onLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void getAllContacts(String baseURL, String endPoint, String username) {
        Log.d(TAG, "Top of getAllContacts");
        JSONArray contacts = new JSONArray(); //This is never populated and is returned empty. Perhaps change this function to void?
        JSONObject unObject = new JSONObject();
        try {
            unObject.put("username", username);
        } catch (JSONException e) {
            Log.e(TAG, "Error building username JSONObject: " + e.getMessage());
        }

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(baseURL)
                .appendPath(endPoint)
                .appendQueryParameter("username", username)
                .build();
        Log.d("Load Contact Fragment", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), unObject)
                .onPostExecute(this::handleGetAllContactsOnPost)
                .build().execute();
        Log.d("Load Contact Fragment", "Bottom of getAllContacts");
    }

    @Override
    public void getPendingRequests(String baseURL, String endpoint, String username) {
        JSONObject unObject = new JSONObject();
        try {
            unObject.put("username", username);
        } catch (JSONException e) {
            Log.e(TAG, "Error building username JSONObject: " + e.getMessage());
        }

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(baseURL)
                .appendPath(endpoint)
                .appendQueryParameter("username", username)
                .build();
        Log.d("Load Contact Fragment", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), unObject)
                .onPostExecute(this::handleGetPendingRequestsOnPost)
                .build().execute();
    }


    @Override
    public void onLogout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_shared_prefs_username));

        prefs.edit().putBoolean(
                getString(R.string.keys_prefs_stay_login),
                false)
                .apply();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    @Override
    public void onSearchByEmailAttempt(String theEmail) {
        Log.e("NavigationActivity", "Search by email");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject emailJSON = new JSONObject();

        try {
            emailJSON.put(getString(R.string.keys_json_email), theEmail);
            Log.e("NavigationActivity", "Put email to json");
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), emailJSON)
                .onPostExecute(this::handleEndOfSearchByName).build().execute();
    }


    @Override
    public void onSearchByUsernameAttempt(String theUsername) {
        Log.e("NavigationActivity", "Search by username");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject usernameJSON = new JSONObject();

        try {
            usernameJSON.put(getString(R.string.keys_json_username), theUsername);
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), usernameJSON)
                .onPostExecute(this::handleEndOfSearchByName).build().execute();
    }

    @Override
    public void onSearchByNameAttempt(String theFirstName, String theLastName) {
        Log.e("NavigationActivity", "Search by name");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject nameJSON = new JSONObject();

        try {
            nameJSON.put(getString(R.string.keys_json_firstname), theFirstName);
            nameJSON.put(getString(R.string.keys_json_lastname), theLastName);
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), nameJSON)
                .onPostExecute(this::handleEndOfSearchByName).build().execute();

    }

    private void loadFragment(Fragment frag, String theFragmentTag) {
        Log.e("NavigationActivity", "" + theFragmentTag);
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, theFragmentTag)
                .addToBackStack(null);
        ft.commit();

    }

    private void loadFriendsFragment() {
        FriendsFragment friendsFragment = new FriendsFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("CONTACTS_ID_USERNAME", myContactList);
        friendsFragment.setArguments(bundle);
        Log.e(TAG, "loadFriendsFragment bundle = " + bundle);
        loadFragment(friendsFragment, getString(R.string.keys_contact_fragment_tag) + "New");

    }

    /**
     * Handle the search by email response. If found a user, show the user's first name and last name
     * in the result text view; otherwise show user not found.
     *
     * @param theResponse the response return from the server
     */
    private void handleEndOfSearchByName(String theResponse) {

        try {
            JSONObject responseJSON = new JSONObject(theResponse);
            boolean success = responseJSON.getBoolean(getString(R.string.keys_json_success));
            RecyclerView recyclerView = findViewById(R.id.searchRecycleViewUserFound);
            RecyclerViewAdapterSearchResult mAdapter;

            if (success) {
                JSONArray users = responseJSON.getJSONArray(getString(R.string.keys_json_array_users_data));
                if (users.length() > 0) {
                    mAdapter = (RecyclerViewAdapterSearchResult) recyclerView.getAdapter();
                    mAdapter.setAdapterDataSet(searchDataJsonArrayToStringArray(users));
                }
                Log.e("NavigationActivity", "User found by name");

            } else {
                ((RecyclerViewAdapterSearchResult) recyclerView.getAdapter()).setAdapterDataSet(null);
                Log.e("NavigationActivity", "User not found");
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "JSON parse error" + e.getMessage());
        }
    }

    /**
     * Sets the recyclerView of the contacts page to the list of existing contacts. Intended to be
     * set used with the functional interface of AsyncTask in the onPostExecute() function.
     *
     * @param theResponse The string returned by the database query containing all the contacts
     *                    the user has.
     */
    private void handleGetAllContactsOnPost(String theResponse) {
        Log.d("Load Contact Fragment", "Top of handleGetAllContactsOnPost");
        try {
            JSONObject responseAsJSON = new JSONObject(theResponse);
            boolean success = responseAsJSON.getBoolean(getString(R.string.keys_json_success));
            RecyclerView recyclerView = findViewById(R.id.contactRecycleViewAllContacts);
            RecyclerViewAdapterContact mAdapter;

            if (success) {
                JSONArray contactArray = responseAsJSON
                        .getJSONArray(getString(R.string.keys_json_contacts));
                mAdapter = new RecyclerViewAdapterContact(jsonArrayContactDataToStringList(contactArray));
                recyclerView.setAdapter(mAdapter);
            } else {
                ((RecyclerViewAdapterContact) recyclerView.getAdapter()).setAdapterDataSet(null);
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "Unable to build JSON: " + e.getMessage());
        }
        Log.d("Load Contact Fragment", "Bottom of handleGetAllContactsOnPost");
    }


    private void handleGetPendingRequestsOnPost(String theResponse) {
        try {
            JSONObject responseAsJSON = new JSONObject(theResponse);
            boolean success = responseAsJSON.getBoolean(getString(R.string.keys_json_success));
            RecyclerView recyclerView = findViewById(R.id.contactsRecyclerViewRequests);
            RecyclerViewAdapterRequest mAdapter;

            if (success) {
                JSONArray requestArray = responseAsJSON
                        .getJSONArray(getString(R.string.keys_json_requests));

                mAdapter = new RecyclerViewAdapterRequest(
                        jsonArrayContactDataToStringList(requestArray));
                recyclerView.setAdapter(mAdapter);
            } else {
                ((RecyclerViewAdapterRequest) recyclerView.getAdapter()).setAdapterDataSet(null);
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "Unable to build JSON: " + e.getMessage());
        }
    }

    /**
     * @param users the users data in Json array format
     * @return
     */
    private List<String> searchDataJsonArrayToStringArray(JSONArray users) {
        List<String> msgs = new ArrayList<>();
        try {
            for (int i = 0; i < users.length(); i++) {
                JSONObject msg = users.getJSONObject(i);
                String returnedUsername = msg.get(getString(R.string.keys_json_username)).toString();
                String returnedFirstname = msg.get(getString(R.string.keys_json_firstname)).toString();
                String returnedLastname = msg.get(getString(R.string.keys_json_lastname)).toString();
                String returnedMemberId = msg.get(getString(R.string.keys_json_memberid)).toString();
                if (!myMemberId.equals(returnedMemberId)) {
                    String string = returnedUsername + ":" + returnedFirstname + ":" + returnedLastname + ":" + returnedMemberId + ":" + myMemberId;
                    msgs.add(string);
                }
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "JSON parse error" + e.getMessage());
        }
        return msgs;

    }

    /**
     * @param users the users data in Json array format
     * @return 2D array where the first column is the username and the second column is both the
     * first and last name of the user.
     */
    private ArrayList<String> jsonArrayContactDataToStringList(JSONArray users) {
        ArrayList<String> contactsList = new ArrayList<>();
        ArrayList<String> friendsList = new ArrayList<>();
        try {
            for (int i = 0; i < users.length(); i++) {
                JSONObject msg = users.getJSONObject(i);
                //for Contacts Fragment
                String usernameA = msg.get(getString(R.string.keys_json_username)).toString();
                String firstNameA = msg.get(getString(R.string.keys_json_firstname)).toString();
                String lastNameA = msg.get(getString(R.string.keys_json_lastname)).toString();
                String msgString = usernameA + ":" + firstNameA + " " + lastNameA;
                contactsList.add(msgString);
                //for Friend Fragment
                String friendMemberId = msg.get(getString(R.string.keys_json_memberid)).toString();
                String friendUsername = msg.get(getString(R.string.keys_json_username)).toString();
                String friendsIdUsername = friendMemberId + ":" + friendUsername;
                friendsList.add(friendsIdUsername);
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "JSON parse error" + e.getMessage());
        }
        saveFriendIdUsername(friendsList);
        return contactsList;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void getFriendList() {
        String username = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE)
                .getString(getString(R.string.keys_shared_prefs_username), null);
        getFriendList(getString(R.string.ep_base_url),
                getString(R.string.ep_view_connections), username);
    }

    /**
     * send a asynv task to the server to get all the contacts that associate with the username
     *
     * @param baseURL
     * @param endPoint
     * @param username the username
     */
    @Override
    public void getFriendList(String baseURL, String endPoint, String username) {
        JSONObject unObject = new JSONObject();
        try {
            unObject.put("username", username);
        } catch (JSONException e) {
            Log.e(TAG, "Error building username JSONObject: " + e.getMessage());
        }

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(baseURL)
                .appendPath(endPoint)
                .appendQueryParameter("username", username)
                .build();
        new SendPostAsyncTask.Builder(uri.toString(), unObject)
                .onPostExecute(this::handleGetFriendListOnPost)
                .build().execute();
    }

    /**
     * extra the friend list from the response.
     *
     * @param theResponse
     */
    private void handleGetFriendListOnPost(String theResponse) {
        try {
            JSONObject responseAsJSON = new JSONObject(theResponse);
            boolean success = responseAsJSON.getBoolean(getString(R.string.keys_json_success));

            if (success) {
                JSONArray contactArray = responseAsJSON
                        .getJSONArray(getString(R.string.keys_json_contacts));
                myContactList = contactsJsonArrayToList(contactArray);
                Log.e(TAG, "handleGetFriendListOnPost success " + myContactList.toString());
//                loadFriendsFragment();
            } else {
                Log.e(TAG, "Unable to get friend list: ");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to build JSON: " + e.getMessage());
        }
    }

    /**
     * converts a jason array returned from handleGetFriendListOnPost to an string array list.
     *
     * @param allContacts the contacts data in Json array format
     * @return
     */
    private ArrayList<String> contactsJsonArrayToList(JSONArray allContacts) {
        ArrayList<String> msgs = new ArrayList<>();
        try {
            for (int i = 0; i < allContacts.length(); i++) {
                JSONObject msg = allContacts.getJSONObject(i);
                String memberIdB = msg.get(getString(R.string.keys_json_memberid)).toString();
                String username = msg.get(getString(R.string.keys_json_username)).toString();
                String string = memberIdB + ":" + username;
                msgs.add(string);
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "JSON parse error" + e.getMessage());
        }
        Log.e(TAG, "save friend id username ");
        saveFriendIdUsername(msgs);
        return msgs;
    }


    /**
     * saves the list in shared preference.
     *
     * @param theFriendList
     */
    private void saveFriendIdUsername(ArrayList<String> theFriendList) {
        Gson gson = new Gson();
        String jsonFriends = gson.toJson(theFriendList);
        mySharedPreference.edit().putString(getString(R.string.keys_saved_friend_list), jsonFriends).apply();
        Log.e(TAG, "saveFriendIdUsername " + jsonFriends);
    }

    public void displayClockThread(TextView theClock) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm:ss a");
                                String dateString = sdf.format(date);
                                theClock.setText(dateString);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /**
     * Stores the geographic coordinates of a point chosen on the map fragment
     * @param selection The location chosen on the map fragment to display weather for that area.
     */
    public void setMapSelection(LatLng selection) {
        mySelectedLocation = selection;
    }

    /**
     * Returns the last location selected on the map fragment as a set of geographic coordinates.
     * @return mySelectedLocation the set of geographic coordinates indicating a location selected
     *  on the map fragment
     */
    public LatLng getMapSelection() {
        return mySelectedLocation;
    }

    /**
     * Indicates whether the map was the selected input to perform the weather query.
     * @return true if the map was used to choose a location for the weather query.
     */
    public boolean getSearchWeatherByMap() {
        return searchWeatherByMap;
    }

    /**
     * Sets the boolean determining if the map was used to select an input for the weather query.
     */
    public void setSearchWeatherByMap(boolean wasSelected) {
        searchWeatherByMap = wasSelected;
    }

    /**
     * Indicates whether the zip code was the selected input to perform the weather query.
     * @return true if the zip code was used to choose a location for the weather query.
     */
    public boolean getSearchWeatherByZip() {
        return searchWeatherByZip;
    }

    /**
     * Sets the boolean determining if the zip code was used to select an input for the weather query.
     */
    public void setSearchWeatherByZip(boolean wasSelected) {
        searchWeatherByZip = wasSelected;
    }

    /**
     * Indicates whether the device's location was the selected input to perform the weather query.
     * @return true if the device's location was used to choose a location for the weather query.
     */
    public boolean getSearchWeatherByCurrentLocation() {
        return searchWeatherByCurrentLocation;
    }

    /**
     * Sets the boolean determining if the device's location was used to select an input for the
     * weather query.
     */
    public void setSearchWeatherByCurrentLocation(boolean wasSelected) {
        searchWeatherByCurrentLocation = wasSelected;
    }

//    /**-----------------------------------------------------------------------------------------**/
//    /**
//     * an inner class that will be a Broadcast Receiver for messages from the service
//     */
//    private class DataUpdateReciever extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
////            if (intent.getAction().equals(NotificationIntentService.RECEIVED_UPDATE)) {
////                Log.d(TAG, "hey I just got your broadcast!");
////            }
//        }
//    }
}