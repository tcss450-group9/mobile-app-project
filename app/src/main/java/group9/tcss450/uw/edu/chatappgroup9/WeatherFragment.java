package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapter24HForecast;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendGetAsyncTask;
import group9.tcss450.uw.edu.chatappgroup9.utils.WeatherUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment{

    private final String TAG = "WeatherFragment";

    private OnFragmentInteractionListener mListener;
    private NavigationActivity myActivity;
    private WeatherUtil myWeatherUtil;
    private Location myLocation;
    private String myLongitude;
    private String myLatitude;
    private JSONObject myLastWeatherUpdate;
    private JSONObject myLastWeather24HForecast;
    private long myLastAPICallTime;
    private SharedPreferences myPrefs;
    private RecyclerView my24HForecast;

    //Elements from the fragment_weather layout
    private ImageView myWeatherIcon;
    private TextView myTimeDate;
    private TextView myCity;
    private TextView myCurrentTemp;
    private EditText myZipSearchBar;
    private Button myZipSearchButton;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myActivity = (NavigationActivity) getActivity();
        myPrefs = myActivity.getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        myLastWeatherUpdate = new JSONObject();
        myLastWeather24HForecast = new JSONObject();
        myWeatherUtil = new WeatherUtil(myActivity);
        myLocation = myActivity.getLocation();
        myLongitude = String.valueOf(myLocation.getLongitude());
        myLatitude = String.valueOf(myLocation.getLatitude());
        Log.d(TAG, myLatitude + " " + myLongitude);

        //Set the timer for allowing API calls (>10 minutes ago)
        if(myPrefs.contains(getString(R.string.keys_shared_prefs_last_weather_api_call_time))) {
            myLastAPICallTime = myPrefs.getLong(getString
                    (R.string.keys_shared_prefs_last_weather_api_call_time), 0);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        myWeatherIcon = view.findViewById(R.id.weatherImageViewCurrentWeatherIcon);
        myTimeDate = view.findViewById(R.id.landingTextViewDataTime);
        myCity = view.findViewById(R.id.landingTextViewCurrentLocation);
        myCurrentTemp = view.findViewById(R.id.landingTextViewFahrenheit);
        myZipSearchButton = view.findViewById(R.id.weatherButtonSearchZipCode);
        myZipSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myActivity.setSearchWeatherByCurrentLocation(false);
                myActivity.setSearchWeatherByZip(true);
                myActivity.setSearchWeatherByMap(false);
                getWeatherByZip(myZipSearchBar.getText().toString());
                get24HourForecastByZip(myZipSearchBar.getText().toString());
            }
        });
        myZipSearchBar = view.findViewById(R.id.weatherEditTextZipSearchBar);
        myZipSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isValidZip(s)) {
                    myZipSearchButton.setEnabled(true);
                }
                else
                    myZipSearchButton.setEnabled(false);
            }
        });
        FloatingActionButton fab = view.findViewById(R.id.weatherFloatingActionButtonMap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myActivity.setSearchWeatherByCurrentLocation(false);
                myActivity.setSearchWeatherByZip(false);
                myActivity.setSearchWeatherByMap(true);
                FragmentTransaction ft = myActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new MapFragment(), "cheating")
                        .addToBackStack(null);
                ft.commit();
            }});


        my24HForecast = view.findViewById(R.id.recyclerViewWeather24HForecast);

        myActivity.displayClockThread(myTimeDate);
        resetWeatherUI();
        return view;
    }

    private void init24HForecastRecyclerView(String[][] dataSet) {
        LinearLayoutManager lm = new LinearLayoutManager(myActivity,
                LinearLayoutManager.HORIZONTAL, false);
        RecyclerViewAdapter24HForecast adapter = new RecyclerViewAdapter24HForecast(dataSet);
        my24HForecast.setLayoutManager(lm);
        my24HForecast.setHasFixedSize(true);
        my24HForecast.setAdapter(adapter);
    }

    /**
     * Tests the input of the search bar to make sure it is a valid zip code.
     */
    private boolean isValidZip(Editable text) {
        String theText = new String(text.toString());
        return theText.length() == 5 && Pattern.matches("[0-9]+", theText);
    }

    private void onSearchByZipButtonClicked() {
        myActivity.setSearchWeatherByZip(true);
        myActivity.setSearchWeatherByMap(false);
        myActivity.setSearchWeatherByCurrentLocation(false);
        getWeatherByZip(myZipSearchBar.getText().toString());
        get24HourForecastByZip(myZipSearchBar.getText().toString());
    }

    /**
     * Queries the weather service for weather conditions based on current GPS coordinates.
     * Sends a GET request and then calls handleGetWeatherOnPost.
     * WARNING: The OpenWeatherAPI free service will penalize our access if we send more than one
     * request every ten minutes. DO NOT use this function somewhere where it can be called unlimited times.
     * The response containing the most recent weather update is stored in variable myLastWeatherUpdate
     */
    public void getWeatherByLocation(String lat, String lon) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_weather_base_url))
                .appendPath(getString(R.string.ep_weather_data))
                .appendPath(getString(R.string.ep_weather_version))
                .appendPath(getString(R.string.ep_weather_weather))
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lon", lon)
                .appendQueryParameter(getString(R.string.ep_weather_appkey_parameter),
                        getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG,uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGetWeatherOnPost)
                .build().execute();
        //myLastWeatherUpdate = myWeatherUtil.getCurrentWeather(myLatitude, myLongitude);
        /*try {
            JSONObject main = myLastWeatherUpdate.getJSONObject("main");
            JSONObject weather = (JSONObject) myLastWeatherUpdate.getJSONArray("weather").get(0);
            String city = myLastWeatherUpdate.getString("name");
            Log.d(TAG, city + "!!!!!!!!!!!!!!!!!!");
            String temp = convKelvinToFahrenheit(main.getString("temp"));
            String icon = weather.getString("icon");

            //myWeatherUtil.setWeatherIcon(icon, myWeatherIcon);
            setWeatherIcon(icon, myWeatherIcon);
            myCity.setText(city);
            myCurrentTemp.setText(temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        //Mark the time this API call was made. (No more than one call every ten minutes)
        myLastAPICallTime = System.currentTimeMillis();
        myPrefs.edit().putLong(getString(R.string.keys_shared_prefs_last_weather_api_call_time),
                myLastAPICallTime);
    }

    /**
     * Queries the weather service for weather conditions based on the given Zip Code.
     * Sends a GET request and then calls handleGetWeatherOnPost.
     */
    private void getWeatherByZip(String theZip) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_weather_base_url))
                .appendPath(getString(R.string.ep_weather_data))
                .appendPath(getString(R.string.ep_weather_version))
                .appendPath(getString(R.string.ep_weather_weather))
                .appendQueryParameter("zip", theZip)
                .appendQueryParameter(getString(R.string.ep_weather_appkey_parameter),
                        getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG,uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGetWeatherOnPost)
                .build().execute();
    }

    /**
     * Parses the JSON response returned by OpenWeatherMap and assigns the current temperature, date
     * and a descriptive weather icon to the corresponding UI elements.
     * @param response The JSON response supplied by the AsyncTask DoInBackground call.
     */
    public void handleGetWeatherOnPost(String response) {
        Log.d(TAG, response);
        try {
            myLastWeatherUpdate = new JSONObject(response);
            JSONObject main = myLastWeatherUpdate.getJSONObject("main");
            JSONObject weather = (JSONObject) myLastWeatherUpdate.getJSONArray("weather").get(0);
            String temp = convKelvinToFahrenheit(main.getString("temp"));
            String city = myLastWeatherUpdate.getString("name");
            Log.d(TAG, city + "!!!!!!!!!!!!!!!!!!");
            myCity.setText(city);
            String icon = weather.getString("icon");
            setWeatherIcon(icon, myWeatherIcon);

            //String dt = myLastWeatherUpdate.getString("dt"); //Inaccurate & unnecssary to get date/time from here
            //Date date = new Date(Long.parseLong(dt));
            //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            //Log.d(TAG, "Temp: " + temp + " Icon: " + icon + " Time: " + dt);

            //Set the UI elements to the values returned by the API call
            myCurrentTemp.setText(temp);
            //myTimeDate.setText(format.format(date));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resetWeatherUI() {
        long current = System.currentTimeMillis();
        Log.d(TAG, String.valueOf(current - myLastAPICallTime));
        boolean okToCall = (current - myLastAPICallTime) > 601000;
        if(okToCall) {
            Log.d(TAG, "Last API call >10 mins ago. Safe to call again.");
            if(myActivity.getSearchWeatherByCurrentLocation()) {
                getWeatherByLocation(myLatitude, myLongitude);
                get24HourForecastByLocation(myLatitude, myLongitude);
            } else if(myActivity.getSearchWeatherByMap()) {
                getWeatherByLocation(String.valueOf(myActivity.getMapSelection().latitude),
                        String.valueOf(myActivity.getMapSelection().longitude));
                get24HourForecastByLocation(String.valueOf(myActivity.getMapSelection().latitude),
                        String.valueOf(myActivity.getMapSelection().longitude));
            } else if(myActivity.getSearchWeatherByZip()) {
                //Search
            }
        }
        else if(myLastWeatherUpdate != null ) { //Consider adding additional parameter to check if 10 mins have passed
            try {
                Log.d(TAG, "Loading weather from previous API call");
                JSONObject main = myLastWeatherUpdate.getJSONObject("main");
                JSONObject weather = (JSONObject) myLastWeatherUpdate.getJSONArray("weather").get(0);
                String temp = convKelvinToFahrenheit(main.getString("temp"));
                String icon = weather.getString("icon");
                //myWeatherUtil.setWeatherIcon(icon, myWeatherIcon);
                setWeatherIcon(icon, myWeatherIcon);

                //Set the UI elements to the values returned by the API call
                myCurrentTemp.setText(temp);
                //myTimeDate.setText(format.format(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getContext(),"Weather unavailable. Try again later",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void setWeatherIcon(String iconCode, ImageView icon) {
        switch(iconCode) {
            case "01d": icon.setImageResource(R.drawable.ic_sunny);
                break;
            case "01n": icon.setImageResource(R.drawable.moon9);
                break;
            case "02d": icon.setImageResource(R.drawable.clouds_1);
                break;
            case "02n": icon.setImageResource(R.drawable.cloudy_night);
                break;
            case "03d": icon.setImageResource(R.drawable.clouds);
                break;
            case "03n": icon.setImageResource(R.drawable.clouds);
                break;
            case "04d": icon.setImageResource(R.drawable.clouds);
                break;
            case "04n": icon.setImageResource(R.drawable.clouds);
                break;
            case "09d": icon.setImageResource(R.drawable.raining);
                break;
            case "09n": icon.setImageResource(R.drawable.raining);
                break;
            case "10d": icon.setImageResource(R.drawable.summer_rain);
                break;
            case "10n": icon.setImageResource(R.drawable.weather);
                break;
            case "11d": icon.setImageResource(R.drawable.storm);
                break;
            case "11n": icon.setImageResource(R.drawable.storm);
                break;
            case "13d": icon.setImageResource(R.drawable.snowing);
                break;
            case "13n": icon.setImageResource(R.drawable.snowing);
                break;
            case "50d": icon.setImageResource(R.drawable.wind);
                break;
            case "50n": icon.setImageResource(R.drawable.wind);
                break;
            default: icon.setImageResource(R.drawable.tornado);
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String convKelvinToFahrenheit(String tempIn) {
        double tempKelv = Double.parseDouble(tempIn);
        tempKelv = (tempKelv * 9.0 / 5.0) - 459.67;
        int tempFahr = (int) tempKelv;
        return String.valueOf(tempFahr) + " F";
    }

    private void get24HourForecastByLocation(String lat, String lon) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_weather_base_url))
                .appendPath(getString(R.string.ep_weather_data))
                .appendPath(getString(R.string.ep_weather_version))
                .appendPath(getString(R.string.ep_weather_forecast))
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lon", lon)
                .appendQueryParameter(getString(R.string.ep_weather_appkey_parameter),
                        getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG,uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGet24HForecastOnPost)
                .build().execute();
        //myLastWeather24HForecast = myWeatherUtil.get5DayForecast(myLatitude, myLongitude); //This asyncTask doesn't finish before the rest of this method runs
        /*try {
            Log.d(TAG, myLastWeather24HForecast.toString());
            JSONArray list = myLastWeather24HForecast.getJSONArray("list");
            JSONObject curr;
            JSONObject currMember;
            Log.d("List Length", String.valueOf(list.length()));
            String[][] adapterData = new String[list.length()][3];
            for(int i = 0; i < list.length(); i++) {
                //format the forecast data for the recyclerView adapter
                curr = (JSONObject) list.get(i);
                currMember = curr.getJSONObject("main");
                //Get temperature
                adapterData[i][0] = convKelvinToFahrenheit(currMember.getString("temp"));
                //Get time
                Log.d(TAG, String.valueOf(curr.getLong("dt")));
                adapterData[i][1] = getDateTime(curr.getLong("dt"));
                //Get icon
                currMember = (JSONObject) curr.getJSONArray("weather").get(0);
                adapterData[i][2] = currMember.getString("icon");
            }
            init24HForecastRecyclerView(adapterData);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void get24HourForecastByZip(String zip) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_weather_base_url))
                .appendPath(getString(R.string.ep_weather_data))
                .appendPath(getString(R.string.ep_weather_version))
                .appendPath(getString(R.string.ep_weather_forecast))
                .appendQueryParameter("zip", zip)
                .appendQueryParameter(getString(R.string.ep_weather_appkey_parameter),
                        getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG,uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGet24HForecastOnPost)
                .build().execute();
        //myLastWeather24HForecast = myWeatherUtil.get5DayForecast(myLatitude, myLongitude);
        /*try {
            Log.d(TAG, myLastWeather24HForecast.toString());
            JSONArray list = myLastWeather24HForecast.getJSONArray("list");
            JSONObject curr;
            JSONObject currMember;
            Log.d("List Length", String.valueOf(list.length()));
            String[][] adapterData = new String[list.length()][3];
            for(int i = 0; i < list.length(); i++) {
                //format the forecast data for the recyclerView adapter
                curr = (JSONObject) list.get(i);
                currMember = curr.getJSONObject("main");
                //Get temperature
                adapterData[i][0] = convKelvinToFahrenheit(currMember.getString("temp"));
                //Get time
                Log.d(TAG, String.valueOf(curr.getLong("dt")));
                adapterData[i][1] = getDateTime(curr.getLong("dt"));
                //Get icon
                currMember = (JSONObject) curr.getJSONArray("weather").get(0);
                adapterData[i][2] = currMember.getString("icon");
            }
            init24HForecastRecyclerView(adapterData);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    public void handleGet24HForecastOnPost(String response) {
        Log.d(TAG, response);
        try {
            myLastWeather24HForecast = new JSONObject(response);
            Log.d(TAG, myLastWeather24HForecast.toString());
            JSONArray list = myLastWeather24HForecast.getJSONArray("list");
            JSONObject curr;
            JSONObject currMember;
            Log.d("List Length", String.valueOf(list.length()));
            String[][] adapterData = new String[list.length()][3];
            for(int i = 0; i < list.length(); i++) {
                //format the forecast data for the recyclerView adapter
                curr = (JSONObject) list.get(i);
                currMember = curr.getJSONObject("main");
                //Get temperature
                adapterData[i][0] = convKelvinToFahrenheit(currMember.getString("temp"));
                //Get time
                Log.d(TAG, String.valueOf(curr.getLong("dt")));
                adapterData[i][1] = getDateTime(curr.getLong("dt"));
                //Get icon
                currMember = (JSONObject) curr.getJSONArray("weather").get(0);
                adapterData[i][2] = currMember.getString("icon");
            }
            init24HForecastRecyclerView(adapterData);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String convertDateTime(long millis) {
        long timeDiff = (millis - System.currentTimeMillis()) / 3600000;

        return String.valueOf(timeDiff);
    }

    public String getDateTime(long millis) {
        //SimpleDateFormat sdf = new SimpleDateFormat("hha");
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm:ss a");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        //String out = sdf.format(millis);
        return sdf.format(millis);
    }
}
