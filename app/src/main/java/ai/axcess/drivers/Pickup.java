package ai.axcess.drivers;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.ahmadrosid.lib.drawroutemap.DrawMarker;
//import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
//import com.directions.route.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ai.axcess.drivers.databinding.ActivityPickupBinding;
import ai.axcess.drivers.util.DirectionPointListener;
import ai.axcess.drivers.util.GetPathFromLocation;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import android.app.AlertDialog;
import android.content.DialogInterface;

import static android.graphics.Color.RED;

public class Pickup extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityPickupBinding binding;
    String responseBody;
    String fname;
    String cunq;
    String thisorderid;
    String whataction;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private Marker mUserMarker;
    private Polyline mRoute;
    private Marker mStartMarker;
    private Marker drivermaker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityPickupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();


        fname = shared.getString("sendfname", "");
        cunq = shared.getString("driver", "");

        thisorderid = getIntent().getExtras().getString("orderid");
        whataction = getIntent().getExtras().getString("doaction");
       // String theroute = getroute(cunq, thisorderid);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mRoute = mMap.addPolyline(new PolylineOptions());


        String theroute = getroute(cunq, thisorderid, whataction);

            theroute = theroute.trim();
        String[] havles = theroute.split(Pattern.quote("~"));
        String mylocation;
        String mydestination;
        mylocation = havles[0];
        mydestination = havles[1];


        String[] latng = mylocation.split("/");
        String mylat = latng[0];
        String mylon = latng[1];


        String[] dlatng = mydestination.split("/");
        String dmylat = dlatng[0];
        String dmylon = dlatng[1];



        Double mydoublelat = 0.0;
        try {
            mydoublelat = Double.parseDouble(mylat);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        Double mydoublelon = 0.0;
        try {
            mydoublelon = Double.parseDouble(mylon);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }


        Double myddoublelat = 0.0;
        try {
            myddoublelat = Double.parseDouble(dmylat);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        Double myddoublelon = 0.0;
        try {
            myddoublelon = Double.parseDouble(dmylon);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }



        LatLng source  = new LatLng(mydoublelat, mydoublelon);
        LatLng destination  = new LatLng(myddoublelat, myddoublelon);

        String API_KEY = getResources().getString(R.string.google_maps_key);
        new GetPathFromLocation(source, destination, API_KEY, new DirectionPointListener() {
            @Override
            public void onPath(PolylineOptions polyLine) {
                //when the path is retrieved, plot it on the map
                mMap.addPolyline(polyLine);
            }
        }).execute();


        // Add a marker in Sydney and move the camera
        LatLng anguilla = new LatLng(mydoublelat, mydoublelon);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setTrafficEnabled(true);
        drivermaker = mMap.addMarker(new MarkerOptions().position(anguilla).title("My Location"));
        //mMap.addMarker(new MarkerOptions().position(source).title("SOURCE"));
        //mMap.addMarker(new MarkerOptions().position(destination).title("DEST"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(anguilla));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );

    }



    @Override
    public void onResume() {
        super.onResume();
        // This registers messageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("my-location"));
    }


    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String mylat = intent.getStringExtra("mylat"); // -1 is going to be used as the default value
            String mylon = intent.getStringExtra("mylon");

            System.out.println("degres  lat :" + mylat + " long : " +  mylon);
            Double mydoublelat = 0.0;
            try {
                mydoublelat = Double.parseDouble(mylat);
            } catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            Double mydoublelon = 0.0;
            try {
                mydoublelon = Double.parseDouble(mylon);
            } catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            //mMap.clear();
            drivermaker.remove();
            MarkerOptions mp = new MarkerOptions();
            mp.position(new LatLng(mydoublelat, mydoublelon));
            mp.title("my position");
            drivermaker = mMap.addMarker(mp);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mydoublelat, mydoublelon), 16));


        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }



    public String getroute(String drvierid, String theorder, String action){

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_route.php?&action=" + action + "&driverid="+drvierid + "&orderid=" + theorder;
        Log.i("action url",url);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("what","this" )

                .build();
        Request request = new Request.Builder()
                .url(url)//your webservice url
                .post(requestBody)
                .build();
        try {
            //String responseBody;
            okhttp3.Response response = client.newCall(request).execute();
            // Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                Log.i("SUCC",""+response.message());
            }
            String resp = response.message();
            responseBody =  response.body().string();
            Log.i("respBody:main",responseBody);
            Log.i("MSG",resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;


    }





}