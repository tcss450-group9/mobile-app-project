package group9.tcss450.uw.edu.chatappgroup9.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group9.tcss450.uw.edu.chatappgroup9.R;

import static android.support.constraint.Constraints.TAG;

/**
 * Contains the functions for querying the weather service and returning current and forecast results.
 */
public class WeatherUtil {
    private String TAG = "WEATHER_UTIL";
    private long myLastAPICallTime;
    private Context myContext;
    private SharedPreferences myPrefs;
    private JSONObject myLastWeatherUpdate;
    private JSONObject myLastWeather24HForecast;

    public WeatherUtil(Context context) {
        myContext = context;
        myPrefs = myContext.getSharedPreferences(myContext.getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        myLastWeatherUpdate = new JSONObject();
        myLastWeatherUpdate = new JSONObject();
    }

    /**
     * Queries the weather service for weather conditions based on current GPS coordinates.
     * Sends a GET request and then calls handleGetWeatherOnPost.
     */
    private void getWeatherByLocation(String latitude, String longitude) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(myContext.getString(R.string.ep_weather_base_url))
                .appendPath(myContext.getString(R.string.ep_weather_data))
                .appendPath(myContext.getString(R.string.ep_weather_version))
                .appendPath(myContext.getString(R.string.ep_weather_weather))
                .appendQueryParameter("lat", latitude)
                .appendQueryParameter("lon", longitude)
                .appendQueryParameter(myContext.getString(R.string.ep_weather_appkey_parameter),
                        myContext.getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG,uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGetWeatherOnPost)
                .build().execute();

        //Mark the time this API call was made. (No more than one call every ten minutes)
        myLastAPICallTime = System.currentTimeMillis();
        myPrefs.edit().putLong(myContext.getString(R.string.keys_shared_prefs_last_weather_api_call_time),
                myLastAPICallTime);
    }

    /**
     * Queries the weather service for weather conditions based on the given Zip Code.
     * Sends a GET request and then calls handleGetWeatherOnPost.
     */
    private void getWeatherByZip(String theZip) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(myContext.getString(R.string.ep_weather_base_url))
                .appendPath(myContext.getString(R.string.ep_weather_data))
                .appendPath(myContext.getString(R.string.ep_weather_version))
                .appendPath(myContext.getString(R.string.ep_weather_weather))
                .appendQueryParameter("zip", theZip)
                .appendQueryParameter(myContext.getString(R.string.ep_weather_appkey_parameter),
                        myContext.getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG,uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGetWeatherOnPost)
                .build().execute();

        //Mark the time this API call was made. (No more than one call every ten minutes)
        myLastAPICallTime = System.currentTimeMillis();
        myPrefs.edit().putLong(myContext.getString(R.string.keys_shared_prefs_last_weather_api_call_time),
                myLastAPICallTime);
    }

    /**
     * Parses the JSON response returned by OpenWeatherMap and assigns the current temperature, date
     * and a descriptive weather icon to the corresponding UI elements.
     * @param response The JSON response supplied by the AsyncTask DoInBackground call.
     */
    public void handleGetWeatherOnPost(String response) {
        Log.d(TAG, "HandleWeatherOnPost!!!!!!!");
        Log.d(TAG, response);
        try {
            myLastWeatherUpdate = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Queries the weather service for 5 day forecast returned as an array of 3-hour intervals.
     * Uses GPS coordinates as input.
     * Sends a GET request and then calls handleGetWeatherOnPost.
     */
    private void get5DayForecastByLocation(String latitude, String longitude) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(myContext.getString(R.string.ep_weather_base_url))
                .appendPath(myContext.getString(R.string.ep_weather_data))
                .appendPath(myContext.getString(R.string.ep_weather_version))
                .appendPath(myContext.getString(R.string.ep_weather_forecast))
                .appendQueryParameter("lat", latitude)
                .appendQueryParameter("lon", longitude)
                .appendQueryParameter(myContext.getString(R.string.ep_weather_appkey_parameter),
                        myContext.getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG, uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGet24HForecastOnPost)
                .build().execute();
    }

    /**
     * Queries the weather service for 5 day forecast returned as an array of 3-hour intervals.
     * Uses zipcode as input.
     * Sends a GET request and then calls handleGetWeatherOnPost.
     * @param zip The zip code to query the weather service
     */
    private void get5DayForecastByZip(String zip) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(myContext.getString(R.string.ep_weather_base_url))
                .appendPath(myContext.getString(R.string.ep_weather_data))
                .appendPath(myContext.getString(R.string.ep_weather_version))
                .appendPath(myContext.getString(R.string.ep_weather_forecast))
                .appendQueryParameter("zip", zip)
                .appendQueryParameter(myContext.getString(R.string.ep_weather_appkey_parameter),
                        myContext.getString(R.string.ep_weather_api_key))
                .build();
        Log.d(TAG, uri.toString());
        new SendGetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleGet24HForecastOnPost)
                .build().execute();
    }

    public void handleGet24HForecastOnPost(String response) {
        Log.d(TAG, "HandleGet24HForecastOnPost");
        Log.d(TAG, response);
        try {
            myLastWeather24HForecast = new JSONObject(response);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the iconCode returned from the weather query to select an appropriate icon for the weather.
     * @param iconCode The string matching the corresponding weather of the query.
     * @param icon The image to be changed
     */
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

    /**
     * Receives a string representation of a temperature reading in degrees Kelvin, converts it to
     * Fahrenheit and converts it back to a string.
     * @param tempIn The given temperature in degrees Kelvin
     * @return The converted temperature in degrees Fahrenheit
     */
    public String convKelvinToFahrenheit(String tempIn) {
        double tempKelv = Double.parseDouble(tempIn);
        tempKelv = (tempKelv * 9.0 / 5.0) - 459.67;
        int tempFahr = (int) tempKelv;
        return String.valueOf(tempFahr) + " F";
    }

    /**
     * The public handle for getting the current weather by zip code. Returns a fresh query of the
     * weather service.
     * @param zip The given zip code
     * @return myLastWeatherUpdate The JSON object returned by the weather service.
     */
    public JSONObject getCurrentWeather(String zip) {
        getWeatherByZip(zip);
        return myLastWeatherUpdate;
    }

    /**
     * The public handle for getting the current weather by geographic coordinates.\
     * Returns a fresh query of the weather service.
     * @param lat The given latitude as a string
     * @param lng The given longitude as a string
     * @return myLastWeatherUpdate The JSON object returned by the weather service.
     */
    public JSONObject getCurrentWeather(String lat, String lng) {
        getWeatherByLocation(lat, lng);
        Log.d(TAG, myLastWeatherUpdate.toString());
        return myLastWeatherUpdate;
    }

    /**
     * The public handle for getting the 5 day forecast by zip code. Returns a fresh query of the
     * weather service.
     * @param zip The given zip code
     * @return myLastWeatherUpdate The JSON object returned by the weather service.
     */
    public JSONObject get5DayForecast(String zip) {
        get5DayForecastByZip(zip);
        return myLastWeatherUpdate;
    }

    /**
     * The public handle for getting the 5 day forecast by geographic coordinates.
     * Returns a fresh query of the weather service.
     * @param lat The given latitude as a string
     * @param lng The given longitude as a string
     * @return myLastWeatherUpdate The JSON object returned by the weather service.
     */
    public JSONObject get5DayForecast(String lat, String lng) {
        get5DayForecastByLocation(lat, lng);
        return myLastWeatherUpdate;
    }


}
