package group9.tcss450.uw.edu.chatappgroup9;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";

    private NavigationActivity myActivity;
    private GoogleMap mGoogleMap;
    private MapView myMapView;
    private double mLat, mLng;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        myActivity = (NavigationActivity) getActivity();
        myMapView = v.findViewById(R.id.mapView);
        mLat = ((NavigationActivity)myActivity).getLocation().getLatitude();
        mLng = ((NavigationActivity)myActivity).getLocation().getLongitude();

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        if(myMapView != null) {
            myMapView.onCreate(null);
            myMapView.onResume();
            myMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
         myActivity.setMapSelection(latLng);
        FragmentTransaction ft = myActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new WeatherFragment(), "cheating")
                .addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this::onMapClick);
        // Hardcode - Add a marker in Tacoma, WA, and move the camera.
        //LatLng latLng = new LatLng(47.2529, -122.4443);
        LatLng latLng = new LatLng(mLat, mLng);
        mGoogleMap.addMarker(new MarkerOptions().
                position(latLng).
                title("Marker in Tacoma"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }
}
