package group9.tcss450.uw.edu.chatappgroup9.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.Forecast;

import org.json.JSONException;
import org.json.JSONObject;

import group9.tcss450.uw.edu.chatappgroup9.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Contains the functions for querying the weather service and returning current and forecast results.
 */
public class WeatherUtilV2 {
    private String TAG = "WEATHER_UTIL";
    private long myLastAPICallTime;
    private Context myContext;
    private SharedPreferences myPrefs;
    private JSONObject myLastWeatherUpdate;
    private JSONObject myLastWeather24HForecast;
    ForecastConfiguration configuration ;


    public WeatherUtilV2(Context context) {
        myContext = context;
        myPrefs = myContext.getSharedPreferences(myContext.getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        configuration =
                new ForecastConfiguration.Builder(null)
                        .build();

        ForecastClient.create(configuration);
        double latitude = 40.2712;
        double longitude = -74.7829;
        ForecastClient.getInstance()
                .getForecast(latitude, longitude, new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                        if (response!=null) {
                            Forecast forecast = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<Forecast> forecastCall, Throwable t) {

                    }
                });
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
}
