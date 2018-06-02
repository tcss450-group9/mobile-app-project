package group9.tcss450.uw.edu.chatappgroup9;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
 * This is a simple {@link Fragment} subclass. This class is the first page to see after the login
 * to app. It shows the user a map, using the Google Maps API.
 *
 * @author @author Garrett Engle, Jenzel Villanueva, Cory Davis, Minqing Chen
 * @version 6/1/18
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

    /**
     * Creates and sets the layout of the this fragment and returns it to the activity.
     *
     * @return the View of this Fragment.
     */
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

    /**
     * After the view is created, this method gets this map ready.
     * @param v The View of the fragment
     * @param b The bundle; unused
     */
    @Override
    public void onViewCreated(View v, Bundle b) {
        if(myMapView != null) {
            myMapView.onCreate(null);
            myMapView.onResume();
            myMapView.getMapAsync(this);
        }
    }

    /**
     * This method is used for the kind of interaction from clicking on the Map
     * @param latLng The latitude and longitude of the map
     */
    @Override
    public void onMapClick(LatLng latLng) {
         myActivity.setMapSelection(latLng);
        FragmentTransaction ft = myActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new WeatherFragment(), "cheating")
                .addToBackStack(null);
        ft.commit();
    }

    /**
     * This method lets us know when the map is ready to be used, and allows camera control.
     * @param googleMap the map that we are using.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this::onMapClick);
        // Hardcode - Add a marker in Tacoma, WA, and move the camera.
        // LatLng latLng = new LatLng(47.2529, -122.4443);

        // Proper version
        LatLng latLng = new LatLng(mLat, mLng);
        mGoogleMap.addMarker(new MarkerOptions().
                position(latLng).
                title("Marker in Tacoma"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    /*FragmentManager getSupportFragmentManager().addOnBackStackChangedListener(
        new FragmentManager.OnBackStackChangedListener() {
        public void onBackStackChanged() {
            myActivity.setSearchWeatherByCurrentLocation(true);
            myActivity.setSearchWeatherByMap(false);
            myActivity.setSearchWeatherByZip(false);
        }
    });*/
}
