package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class RecyclerViewAdapter24HForecast extends RecyclerView.Adapter<RecyclerViewAdapter24HForecast.ViewHolder>  {

    private String[][] mDataset;

    public RecyclerViewAdapter24HForecast(String[][] data) {
        mDataset = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView myTime;
        TextView myTemp;
        ImageView myIcon;

        public ViewHolder(View v) {
            super(v);
            myTime = v.findViewById(R.id.recyclerView24WeatherTextViewTime);
            myTemp = v.findViewById(R.id.recyclerViewWeather24Temp);
            myIcon = v.findViewById(R.id.recyclerViewImageViewWeatherIcon);
        }
    }

    @Override
    public RecyclerViewAdapter24HForecast.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scroll_view_item_hours_weather, parent, false);
        RecyclerViewAdapter24HForecast.ViewHolder vh = new RecyclerViewAdapter24HForecast.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter24HForecast.ViewHolder holder, int position) {
        holder.myTemp.setText(mDataset[position][0]);
        holder.myTime.setText(mDataset[position][1]);
        setWeatherIcon(mDataset[position][2], holder.myIcon);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
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
}
