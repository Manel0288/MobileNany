package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ChildAndHisAreas;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ChildLocation;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildMapFragment extends Fragment implements OnMapReadyCallback, LocationListener,
                                                        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "ChildMapFragment";
    private static final String AREA_STATUS = "Autorisé";
    private static final String[] PERMISSIONS = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_CODE = 1;
    private static final int GEOFENCE_RADIUS = 500;
    //in milli seconds
    //private static final String GEOFENCE_ACTION_RECEIVER = "projetannuel.idc.masterinfo.unicaen.mobilenany.ACTION_RECEIVE_GEOFENCE";
    PendingIntent pendingIntent;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    private SupportMapFragment mapFragment;
    private ChildAndHisAreas childAndHisAreas;
    LocationManager locationManager;
    GoogleMap map;
    Marker marker;
    MarkerOptions options;
    ApiService service;
    TokenManager tokenManager;
    Call<ChildLocation> call;
    ChildLocation childLocation;
    Timer timer;

    // Deuxieme essai
    //private FusedLocationProviderApi fusedLocationProviderApi;
    private GoogleApiClient googleApiClient;
    private GeofencingClient geofencingClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_map, container, false);
        ButterKnife.bind(this, view);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            childAndHisAreas = bundle.getParcelable("ChildAndAreas");
        }

        buildLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //buildLocationRequest();
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.child_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        geofencingClient = LocationServices.getGeofencingClient(getActivity());

        if (!isLocationEnabled()) {
            Log.w(TAG, "-----------------------------GPS not enabled----------------------------  " );
            showAlert(1);
        }else {
            Log.w(TAG,"----------------------------GPS enabled---------------");
        }

        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            Log.w(TAG,"Dans le if de isPermiGrant---------------");
            requestPermissions(PERMISSIONS, LOCATION_REQUEST_CODE);
        } else {
            googleApiClient.connect();
            Log.w(TAG,"Dans le else de isPermiGrant---------------");
            requestLocation();
            fetchLastLocation();
            buildLocationCallback();

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            //mapFragment.getMapAsync(this);
        }


    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLastLocation();
                // stocker la location dans la bd
                if (!tokenManager.getToken().getRole().equals("Parent")){
                    addChildLocation();
                }
               Log.w(TAG, "---------Current Location--------- "+ currentLocation.getLatitude() +" "+currentLocation.getLongitude());

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.w(TAG, "---------onMapReady--------- ");
        map = googleMap;
        options = new MarkerOptions();
        map.getUiSettings().setZoomControlsEnabled(true);
        this.addMarkers();
        LatLng latLng;
        if (!tokenManager.getToken().getRole().equals("Parent")) {
            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            options.position(latLng)
                    .title("Child Current Location")
                    .snippet("Hello")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }else{
            getChildLocation();
            timer = new Timer ();
            TimerTask hourlyTask = new TimerTask () {
                @Override
                public void run () {
                    Log.w(TAG, "--------------Dans le timer TimerTask ----------------- ");
                    getChildLocation();
                }
            };
            timer.schedule (hourlyTask, 0l, 1000*60);
            //latLng = new LatLng(Double.valueOf(childLocation.getLatitude()), Double.valueOf(childLocation.getLongitude()));
        }

        //Adding the created the marker on the map
        marker = map.addMarker(options);

    }

    @Override
    public void onLocationChanged(Location location) {
        if (!tokenManager.getToken().getRole().equals("Parent")) {
            if (location != null) {
                currentLocation = location;

                Log.i(TAG, "Coord Changed : latitude : " + location.getLatitude() + " logitude : " + location.getLongitude());

                if (map == null) {
                    mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.child_map);
                    mapFragment.getMapAsync(ChildMapFragment.this);
                } else {
                    LatLng myCoords = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    LatLngBounds bounds = this.map.getProjection().getVisibleRegion().latLngBounds;
                    marker.setPosition(myCoords);
                    if (!bounds.contains(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))) {
                        //Move the camera to the user's location if they are off-screen!
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoords, 15));
                    }
                }
            } else {
                Log.i(TAG, "--------The LOCATION IS NULL ");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG,"--------------------Location permission not missing--------------------");
                    googleApiClient.connect();
                    requestLocation();


                    fetchLastLocation();
                    buildLocationCallback();

                    if (ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getContext(),
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                } else {
                    Log.w(TAG,"--------------------Location permission is missing--------------------");
                }
                break;
            default:
                Log.w(TAG,"--------------------Location permission missing in default--------------------");
                break;

        }
    }

    private void addChildLocation(){
        String longitude = String.valueOf(currentLocation.getLongitude());
        String latitude = String.valueOf(currentLocation.getLatitude());

        call = service.addChildLocation(longitude, latitude);
        call.enqueue(new Callback<ChildLocation>() {
            @Override
            public void onResponse(Call<ChildLocation> call, Response<ChildLocation> response) {
                Log.w(TAG, "onResponse " + response);

                if (response.code() == 401) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

                    tokenManager.deleteToken();
                }

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());
                    Toast.makeText(getActivity(), "location added", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getActivity(), "Erreur de recuperation des données", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onFailure(Call<ChildLocation> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
    }

    private void getChildLocation(){
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Chargement des données...");
        dialog.show();

        call = service.getChildLastLocation(childAndHisAreas.getChild().getId());
        call.enqueue(new Callback<ChildLocation>() {
            @Override
            public void onResponse(Call<ChildLocation> call, Response<ChildLocation> response) {
                dialog.dismiss();
                Log.w(TAG, "onResponse " + response);

                if (response.code() == 401) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

                    tokenManager.deleteToken();
                }

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());
                    LatLng latLng;
                    if (childLocation == null || childLocation != response.body()) {
                        childLocation = response.body();
                        latLng = new LatLng(Double.valueOf(childLocation.getLatitude()), Double.valueOf(childLocation.getLongitude()));
                        options.position(latLng)
                                .title("Child Current Location")
                                .snippet("Hello")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        map.addMarker(options);
                    }
                }else{
                    Toast.makeText(getActivity(), "Erreur de recuperation des données", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onFailure(Call<ChildLocation> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
    }

    private void buildLocationRequest(){
        Log.w(TAG,"--------------------buildLocationRequest buildLocationRequest buildLocationRequest --------------------");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10);
    }

    private void addMarkers() {
        Area[] areas = this.childAndHisAreas.getAreas();

        Log.w(TAG, "areas size: : " + areas.length);
        int index = 0;
        for (Area area : areas) {

            Double lat = Double.valueOf(area.getLatitude());
            Double lon = Double.valueOf(area.getLongitude());

            LatLng coord = new LatLng(lat, lon);
            drawMarker(coord, area);
            index++;
        }
    }

    private void drawMarker(LatLng point, Area area) {
        // Setting latitude and longitude for the marker
        if (area.getCategory().equals(AREA_STATUS)) {
            Log.w(TAG, "Area category dans IF : " + area.getCategory());
            options.position(point)
                    .title(area.getLabel())
                    .snippet(area.getCategory())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            ;
        } else {
            Log.w(TAG, "--------- Adresse de Location non autorisé --------- "+ area.getAdresse());
            //addLocationAlert();
            Log.w(TAG, "Area category dans ELSE : " + area.getCategory());
            options.position(point)
                    .title(area.getLabel())
                    .snippet(area.getCategory())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            ;
        }

        // Adding marker on the Google Map
        map.addMarker(options);
    }

    @SuppressLint("MissingPermission")
    private void addLocationAlert(){
        ArrayList<Geofence> geofences = new ArrayList<>();
        ArrayList<LatLng> positions = new ArrayList<>();
        Area[] areas = this.childAndHisAreas.getAreas();

        for (Area area : areas) {
            if (!area.getCategory().equals(AREA_STATUS)) {
                Double lat = Double.valueOf(area.getLatitude());
                Double lon = Double.valueOf(area.getLongitude());
                LatLng p = new LatLng(lat, lon);
                positions.add(p);
                String key = ""+lat+"-"+lon;
                Geofence geofence = getGeofence(lat, lon, key);

                geofences.add(geofence);
            }

        }

            pendingIntent = getGeofencePendingIntent();
            Log.w(TAG, "------------------------------Dans le else de addLocationAlert : valeur de pedingIntent----- "+pendingIntent);
            if(geofences.size() > 0) {
                geofencingClient.addGeofences(getGeofencingRequest(geofences), pendingIntent)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.w(TAG, "------------------------------Location alter has been added--------------------------------------");
                                    CircleOptions circleOptions = new CircleOptions()
                                            .center(positions.get(0))
                                            .strokeColor(Color.argb(50, 70, 70, 70))
                                            .fillColor(Color.argb(100, 150, 150, 150))
                                            .radius(GEOFENCE_RADIUS);
                                    map.addCircle(circleOptions);
                                } else {
                                    Log.w(TAG, "------------------------------Location alter could not be added--------------------------------------");
                                }
                            }
                        });
            }
    }

    private void removeLocationAlert(){
        if (isPermissionGranted()) {
            requestPermissions(PERMISSIONS, LOCATION_REQUEST_CODE);
        } else {
            geofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        "Location alters have been removed",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(),
                                        "Location alters could not be removed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private PendingIntent getGeofencePendingIntent() {
        Toast.makeText(getActivity(),"Inside getGeofencePendingIntent",Toast.LENGTH_LONG).show();
        Log.w(TAG, "------------------------------Inside getGeofencePendingIntent--------------------------------------");
        Intent intent = new Intent(getActivity(), LocationAlertIntentService.class);
        if (pendingIntent != null) {
            // Return the existing intent
            return pendingIntent;
            // If no PendingIntent exists
        } else {
            // Create an Intent pointing to the IntentService
            //Intent intent = new Intent(GEOFENCE_ACTION_RECEIVER);
            return PendingIntent.getService(
                    getActivity(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
    private GeofencingRequest getGeofencingRequest(ArrayList<Geofence> geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofence);
        return builder.build();
    }

    private Geofence getGeofence(double lat, double lang, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(5000)
                .build();
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        //String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        //buildLocationRequest();
        //locationManager.requestLocationUpdates(provider, 1000, 10, this);
    }

    @SuppressLint("MissingPermission")
    private void fetchLastLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Log.w(TAG, "---------Current Location dans fetchLocation--------- "+ currentLocation.getLatitude() +" "+currentLocation.getLongitude());
                    mapFragment.getMapAsync(ChildMapFragment.this);
                }else{
                    Log.w(TAG, "---------Current Location Error---------");
                }
            }
        });
    }

    private boolean isPermissionGranted(){
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location Permission granted  ");
            return true;
        }else {
            Log.w(TAG, "Location Permission not granted  ");
            return false;
        }
    }

    private boolean isLocationEnabled(){
        boolean gps_enabled = false;
        boolean network_enabled = false;


        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled && network_enabled;
    }

    private void showAlert(int status){
        String title, message, btnText;
        Log.w(TAG, "--------------------------------------- showAlert ------------------------------------  ");
        if (status == 1){
            title ="Activer le gps";
            btnText = "Paramètres";
            message = "Votre gps est désactivé. S'il vous plaît activé le pour utiliser l'application.";
        }else{
            title ="Autorisation reussie";
            btnText = "Autoriser";
            message = "S'il vous plaît autorisez l'application à utiliser vos données gps.";
        }
        final AlertDialog.Builder dialogue = new AlertDialog.Builder(getActivity());
        dialogue.setCancelable(false)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (status == 1){
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            getActivity().startActivity(intent);
                        }else
                            requestPermissions(PERMISSIONS, REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                });
        dialogue.show();
    }

    @Override
    //@SuppressLint("MissingPermission")
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(PERMISSIONS, LOCATION_REQUEST_CODE);
        }
        if(googleApiClient.isConnected()){
            Log.w(TAG,"-----------Google_Api_Client: It was connected on (onConnected) function, working as it should.-----------------");
        }
        else{
            Log.w(TAG,"-----------------Google_Api_Client: It was NOT connected on (onConnected) function, It is definetly bugged.------------------");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        addLocationAlert();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "--------------onConnectionSuspended ---------------------- ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "-------------------onConnectionFailed ------------------------- ");
    }
}
