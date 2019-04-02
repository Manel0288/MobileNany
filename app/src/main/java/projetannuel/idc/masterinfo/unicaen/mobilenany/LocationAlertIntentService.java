package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//BroadcastReceiver JobIntentService
public class LocationAlertIntentService extends  IntentService{
    private static final String IDENTIFIER = "LocationAlertIntentS";

    public LocationAlertIntentService() {
        super(IDENTIFIER);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Toast.makeText(this,"Inside getGeofencePendingIntent",Toast.LENGTH_LONG).show();
        if (geofencingEvent.hasError()) {
            Log.e(IDENTIFIER, "" + getErrorString(geofencingEvent.getErrorCode()));
            return;
        }

        Log.w(IDENTIFIER, "----------------------------------Evenement geoFence Declenché---------------------------------------");

        Log.w(IDENTIFIER, geofencingEvent.toString());

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String transitionDetails = getGeofenceTransitionInfo(triggeringGeofences);

            String transitionType = getTransitionString(geofenceTransition);


            notifyLocationAlert(transitionType, transitionDetails);
        }
    }
//Context ctx,
    private String getGeofenceTransitionInfo(List<Geofence> triggeringGeofences) {
        ArrayList<String> locationNames = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            locationNames.add(getLocationName(geofence.getRequestId()));
        }
        String triggeringLocationsString = TextUtils.join(", ", locationNames);

        return triggeringLocationsString;
    }
//Context ctx,
    private String getLocationName(String key) {
        String[] strs = key.split("-");

        String locationName = null;
        if (strs != null && strs.length == 2) {
            double lat = Double.parseDouble(strs[0]);
            double lng = Double.parseDouble(strs[1]);

            locationName = getLocationNameGeocoder(lat, lng);
        }
        if (locationName != null) {
            return locationName;
        } else {
            return key;
        }
    }
//    Context ctx,
    private String getLocationNameGeocoder(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception ioException) {
            Log.e("", "Error in getting location name for the location");
        }

        if (addresses == null || addresses.size() == 0) {
            Log.d("", "no location name");
            return null;
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressInfo = new ArrayList<>();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressInfo.add(address.getAddressLine(i));
            }

            return TextUtils.join(System.getProperty("line.separator"), addressInfo);
        }
    }

    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "geofence too many_geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "geofence too many pending_intents";
            default:
                return "geofence error";
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "location entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "location exited";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "dwell at location";
            default:
                return "location transition";
        }
    }
//    Context ctx,
    private void notifyLocationAlert( String locTransitionType, String locationDetails) {

        String CHANNEL_ID = "Zoftino";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_add_child)
                        .setContentTitle(locTransitionType)
                        .setContentText(locationDetails);

        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // Get the type of transition (entry or exit)
//        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//        int geofenceTransition = geofencingEvent.getGeofenceTransition();
//        Log.w(IDENTIFIER, "----------------------------------Evenement geoFence Declenché---------------------------------------");
//
//        Log.w(IDENTIFIER, geofencingEvent.toString());
//
//        // Test that a valid transition was reported
//        if ((geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
//                || (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)) {
//            // Get the geofences that were triggered. A single event can trigger multiple geofences.
//            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//            String transitionDetails = getGeofenceTransitionInfo(context,triggeringGeofences);
//
//            String transitionType = getTransitionString(geofenceTransition);
//
//            notifyLocationAlert(context,transitionType,transitionDetails);
////            Intent broadcastIntent = new Intent();
////
//            // Give it the category for all intents sent by the Intent Service
//            //broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
////            broadcastIntent.setAction("projetannuel.idc.masterinfo.unicaen.mobilenany.")
////                        .putExtra("EXTRA_GEOFENCE_ID", geofence.getRequestId())
////                        .putExtra("EXTRA_GEOFENCE_TRANSITION_TYPE", geofenceTransitionString);
////            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
////            notifyLocationAlert(transitionType, transitionDetails);
////            for (Geofence geofence : triggeringGeofences) {
////                String geofenceTransitionString = getTransitionString(geofenceTransition);
////                String geofenceText=geofenceTransitionString+" : "+geofence.getRequestId();
////                Log.i(IDENTIFIER, "--------------Geofence Transition------------------:" + geofenceText);
////                notifyLocationAlert(context,geofenceText,);
////                // Create an Intent to broadcast to the app
////                broadcastIntent.setAction(GEOFENCE_ACTION)
////                        .putExtra("EXTRA_GEOFENCE_ID", geofence.getRequestId())
////                        .putExtra("EXTRA_GEOFENCE_TRANSITION_TYPE", geofenceTransitionString);
////                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
////            }
//        } else {
//            // Always log as an error
//            Log.e(IDENTIFIER, "---------------------Errrreurrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr------------------------");
//        }
//    }
}
