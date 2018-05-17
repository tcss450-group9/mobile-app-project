package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.model.RecycleViewAdapterContact;
import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterSearchResult;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;
import group9.tcss450.uw.edu.chatappgroup9.utils.ThemeUtil;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
//        ChatFragment.OnFragmentInteractionListener,
        LandingFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        ContactsFragment.OnFragmentInteractionListener {

    public static int mTheme = ThemeUtil.THEME_MEDITERRANEAN_BLUES;
    private String[] myDummyValue = {"Little_dog", "little_cat", "big_turtle", "myDummyValue", "African buffalo", "Meles meles"};
    private SharedPreferences mySharedPreference;
    private String myMemberId;

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
//            public void onClick(View view) {
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
            TextView textView = navigationView.getHeaderView(0).findViewById(R.id.navigationHeaderTextViewUsername);

//            Log.e("NavigationActivity", "header : " + textView);
            textView.setText(username);
        }

        if(savedInstanceState == null) {
            if(findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,
                        new LandingFragment(), getString(R.string.keys_landing_fragment_tag))
                        .commit();
            }
        }
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
        } else if (id == R.id.nav_chat) {
            loadFragment(new ChatFragment(), getString(R.string.keys_chat_fragment_tag));
        } else if (id == R.id.nav_contact) {
            loadFragment(new ContactsFragment(), getString(R.string.keys_contact_fragment_tag));
        } else if (id == R.id.nav_search) {
            loadFragment(new SearchFragment(), getString(R.string.keys_search_fragment_tag));
        } else if (id == R.id.nav_weather) {
            loadFragment(new WeatherFragment(), getString(R.string.keys_weather_fragment_tag));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void getAllContacts(String baseURL, String endPoint, String username, String verifiedStatus) {
        Log.d("Load Contact Fragment","Top of getAllContacts");
        JSONArray contacts = new JSONArray(); //This is never populated and is returned empty. Perhaps change this function to void?
        JSONObject unObject = new JSONObject();
        try {
            unObject.put("username", username);
        }
        catch(JSONException e) {
            Log.e("GETALLCONTACTS", "Error building username JSONObject: " + e.getMessage());
        }

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(baseURL)
                .appendPath(endPoint)
                .appendQueryParameter("username",username)
                .appendQueryParameter("verified",verifiedStatus)
                .build();
        Log.d("Load Contact Fragment", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), unObject)
                .onPostExecute(this::handleGetAllContactsOnPost)
                .build().execute();
        Log.d("Load Contact Fragment","Bottom of getAllContacts");
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
        //the way to close an app programmaticaly
//        finishAndRemoveTask();
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
            Log.e("NavigationActivity", "Put email to json" );
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), emailJSON)
                .onPostExecute(this::handleEndOfSearch).build().execute();
    }


    @Override
    public void onSearchByUsernameAttempt(String theUsername) {
//        Log.e("NavigationActivity", "Search by username");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject usernameJSON = new JSONObject();

        try {
            usernameJSON.put(getString(R.string.keys_json_username), theUsername);
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), usernameJSON)
                .onPostExecute(this::handleEndOfSearch).build().execute();
    }

    @Override
    public void onSearchByNameAttempt(String theFirstName, String theLastName) {
//        Log.e("NavigationActivity", "Search by name");
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
                .onPostExecute(this::handleEndOfSearch).build().execute();

    }


    private void loadFragment(Fragment frag, String theFragmentTag) {
        Log.e("NavigationActivity", "" + theFragmentTag);
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, theFragmentTag)
                .addToBackStack(null);
        ft.commit();

    }


    /**
     * Handle the search response. The Response data will be send to a recycler view adapter if the response success;
     * otherwise send null to a recycler view adapter.
     * @param theResponse the response return from the server
     */
    private void handleEndOfSearch(String theResponse) {

        try {
            JSONObject responseJSON = new JSONObject(theResponse);
            boolean success = responseJSON.getBoolean(getString(R.string.keys_json_success));
            RecyclerView recyclerView = findViewById(R.id.searchRecycleViewUserFound);
            RecyclerViewAdapterSearchResult mAdapter;

            if (success) {
                JSONArray users = responseJSON.getJSONArray(getString(R.string.keys_json_array_users_data));
                if (users.length() > 0) {
                    mAdapter = (RecyclerViewAdapterSearchResult) recyclerView.getAdapter();
                    mAdapter.setAdapterDataSet(searchDataJsonArrayToList(users));
                }
//                Log.e("NavigationActivity", "User found by name");

            } else {
                ((RecyclerViewAdapterSearchResult) recyclerView.getAdapter()).setAdapterDataSet(null);
//                Log.e("NavigationActivity", "User not found");
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
        Log.d("Load Contact Fragment","Top of handleGetAllContactsOnPost");
        try {
            JSONObject responseAsJSON = new JSONObject(theResponse);
            boolean success = responseAsJSON.getBoolean(getString(R.string.keys_json_success));
            RecyclerView recyclerView = findViewById(R.id.contactRecycleViewAllContacts);
            RecycleViewAdapterContact mAdapter;

            if(success) {
                JSONArray contactArray = responseAsJSON
                        .getJSONArray(getString(R.string.keys_json_contacts));

                mAdapter = new RecycleViewAdapterContact(
                        jsonArrayUsersDataToStringMultiArray(contactArray));
                recyclerView.setAdapter(mAdapter);
            }
            else {
                //TODO This is causing a fatal exception on response success=false. Cannot set adapter to null
                ((RecycleViewAdapterContact) recyclerView.getAdapter()).setAdapterDataSet(null);
            }
        }
        catch (JSONException e) {
            Log.e("NavigationActivity", "Unable to build JSON: " + e.getMessage());
        }
        Log.d("Load Contact Fragment","Bottom of handleGetAllContactsOnPost");
    }


    /**
     * converts a jason array returned from search to an string array list.
     * @param users the users data in Json array format
     * @return
     */
    private List<String> searchDataJsonArrayToList(JSONArray users) {
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
     *
     * @param users the users data in Json array format
     * @return 2D array where the first column is the username and the second column is both the
     * first and last name of the user.
     */
    private String[][] jsonArrayUsersDataToStringMultiArray(JSONArray users) {
        String[][] msgs = new String[users.length()][2];
        try {
            for (int i = 0; i < users.length(); i++) {
                JSONObject msg = users.getJSONObject(i);
                String username = msg.get(getString(R.string.keys_json_username)).toString();
                String firstname = msg.get(getString(R.string.keys_json_firstname)).toString();
                String lastname = msg.get(getString(R.string.keys_json_lastname)).toString();
                msgs[i][0] = username;
                msgs[i][1] = firstname + " " + lastname;
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "JSON parse error" + e.getMessage());
        }
        return msgs;

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
