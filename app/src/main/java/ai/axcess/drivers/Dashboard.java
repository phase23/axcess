package ai.axcess.drivers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.blue;

public class Dashboard extends AppCompatActivity {


    TextView driver;
    TextView shiftstate;
    TextView locationstate;
    TextView offview;
    Button shift;
    Button viewinorders;
    Button viewearnings;
    Button missedjobs;
    Button viewactionorders;
    AlertDialog dialog;
    Button llogout;
    String responseLocation;
    String fname;
    String cunq;
    String isgpson;
    private int name;
    int newstate;
    public Handler handler;
    Handler handler2;
    String returnshift;
    String somebits;
    String orderspending;
    String getmsg;
    MediaPlayer player;
    public Handler handler3;
    private static final int MY_COARSE_REQUEST_CODE = 102;
    private static final int MY_FINE_REQUEST_CODE = 103;
    private static final int PHONE_REQUEST_CODE = 104;
    private final int ONE_SECONDS = 1000;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }


        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();


        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://axcessdrivers-default-rtdb.firebaseio.com/");
        DatabaseReference newdriver = database.getReference(thisdevice); // yourwebisteurl/rootNode if it exist otherwise don't pass any string to it.

        newdriver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("status")) {
                    // Exist! Do whatever.
                } else {
                    // Don't exist! Do something.
                    newdriver.child("latitude").setValue("18.205397742382385");
                    newdriver.child("longitude").setValue("-63.062720587183264");
                    newdriver.child("status").setValue("waiting");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed, how to handle?

            }

        });


        newdriver.child("status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                   // Toast.makeText(getApplicationContext(), "status : " + String.valueOf(task.getResult().getValue()), Toast.LENGTH_LONG).show();

                    String thisstatus = String.valueOf(task.getResult().getValue());

                    if(thisstatus.equals("alert")) {
                        viewinorders.setBackgroundColor(RED);
                        viewinorders.setVisibility(View.VISIBLE);

                    }

                }
            }
        });


        // Read from the database
        newdriver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //Double value = dataSnapshot.child("latitude").getValue(Double.class);
                String alert = dataSnapshot.child("status").getValue(String.class);
                //boolean isSeen = ds.child("isSeen").getValue(Boolean.class);
                //Log.d(TAG, "Value is: " + value);
               // Toast.makeText(getApplicationContext(), "Value is:" + value + " Alert: " + alert, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "changed : " + value, Toast.LENGTH_LONG).show();

                if(alert.equals("alert")){
                if(isRunning) {

                   // Toast.makeText(getApplicationContext(), "Runing already Alert: " + alert, Toast.LENGTH_LONG).show();

                }else {

                    //Toast.makeText(getApplicationContext(), "Not Runing already Alert: " + alert, Toast.LENGTH_LONG).show();

                    if( !am.isMusicActive()) {

                        isRunning = true;
                        startplayer();
                       // Toast.makeText(getApplicationContext(), "Alert on : " + alert, Toast.LENGTH_LONG).show();

                        viewinorders.setBackgroundColor(RED);
                        viewinorders.setVisibility(View.VISIBLE);
                    }



                }


                }else{

                    viewinorders.setBackgroundColor(getResources().getColor(android.R.color.white));
                    viewinorders.setVisibility(View.INVISIBLE);

                    if(isRunning) {
                        isRunning = false;
                    }

                    if(player != null){
                        player.stop();
                        player.release();
                        player = null;
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "AxcessEats Welcome.", error.toException());
            }
        });




        cunq = shared.getString("driver", "");
        int j = shared.getInt("key", 0);


        handler2 = new Handler(Looper.getMainLooper());


        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        if(j > 0) {
            fname = shared.getString("sendfname", "");
            cunq = shared.getString("driver", "");
        }else {
             fname = getIntent().getExtras().getString("sendfname");
              cunq = getIntent().getExtras().getString("driver");
        }




        //String getunsettledorders = getordercount( cunq );


        shift = (Button)findViewById(R.id.Startshift);

            llogout = (Button)findViewById(R.id.logout);
        viewinorders = (Button)findViewById(R.id.vieworders);
        viewearnings = (Button)findViewById(R.id.viewearnings);
        missedjobs = (Button)findViewById(R.id.missedcalls);

        viewactionorders = (Button)findViewById(R.id.actionorders);

        //viewactionorders.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnani));


        driver = (TextView)findViewById(R.id.drivername);
        shiftstate = (TextView)findViewById(R.id.whatshift);
        offview = (TextView)findViewById(R.id.offlinemsg);
        driver.setText(fname);

       // getShift(cunq);
        gettershift(cunq);
        checklocationstatus();




        missedjobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dashboard.this)
                        .build();
                dialog.show();



                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){


                        Intent intent = new Intent(Dashboard.this, Missedjobs.class);
                        startActivity(intent);
                        dialog.dismiss();


                    }
                }, 1000);

            }
        });



        viewearnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dashboard.this)
                        .build();
                dialog.show();



                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){


                Intent intent = new Intent(Dashboard.this, Earnings.class);
                startActivity(intent);
                        dialog.dismiss();


                    }
                }, 1000);

            }
        });


        viewactionorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dashboard.this)
                        .build();
                dialog.show();


                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){

                        Intent intent = new Intent(Dashboard.this, Orderpanel.class);
                        startActivity(intent);
                        dialog.dismiss();

                    }
                    }, 1000);




            }
        });


        llogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared.edit().clear().commit();

                getmsg = "Turning off";
                Intent offintent = new Intent("stopchecks");
                offintent.putExtra("send", "off");
                offintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().sendBroadcast(offintent);

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dashboard.this)
                        .build();
                dialog.show();

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){

                Shiftactionset(cunq,1);
                stopService(new Intent(Dashboard.this, MyService.class));
                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                startActivity(intent);

                        dialog.dismiss();

                    }
                }, 1000);


            }

        });


        viewinorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dashboard.this)
                        .build();
                dialog.show();

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){




                Intent intent = new Intent(Dashboard.this, Vieworders.class);

                startActivity(intent);
                        dialog.dismiss();

                    }
                }, 1000);


            }
        });





        shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            shift.setEnabled(false);

                int getstateback = getState();

                if(getstateback == 1){
                    getmsg = "Turning off";
                    Intent intent = new Intent("stopchecks");
                    intent.putExtra("send", "off");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().sendBroadcast(intent);


                }else{
                     getmsg = "Turning on";
                }


                dialog = new SpotsDialog.Builder()
                        .setMessage(getmsg)
                        .setContext(Dashboard.this)
                        .build();
                dialog.show();

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){




                        shiftstate.setText("Please wait..");
                        int returnstate = getState();
                        Shiftactionset(cunq, returnstate);
                       checklocationstatus();

                        dialog.dismiss();
                        shift.setEnabled(true);
                    }
                }, 1000);


            }

        });



       // if (Build.VERSION.SDK_INT >= 23) {

        /*
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_COARSE_REQUEST_CODE);
        }
*/
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
           // Toast.makeText(getApplicationContext(), "fine access not granted ", Toast.LENGTH_LONG).show();
            //requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, MY_FINE_REQUEST_CODE);
        }








           // if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED  ) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {


                    android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(Dashboard.this);
            dialog.setCancelable(false);
            dialog.setTitle("Disclaimer");
            dialog.setMessage(" This app collects location data in the background to enable driver location even when the app is closed or not in use.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                   // requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION }, MY_COARSE_REQUEST_CODE);
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, MY_FINE_REQUEST_CODE);


                }
            })
                    .setNegativeButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                        }

                    });
            final AlertDialog alert = dialog.create();
            alert.show();




            }



    }




    public void gettershift(String cunq){

        try {
            doGetRequest("https://axcess.ai/barapp/driver_shiftaction.php?&action=getshift&cunq="+cunq);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//end



    public void getShift(String cunq){

        String bits = Shiftaction(cunq );
        String[] pieces = bits.split(Pattern.quote("~"));


        returnshift = pieces[0];
        returnshift = returnshift.trim();

        String getunsettledorders = pieces[1];
        getunsettledorders = getunsettledorders.trim();
        viewactionorders.setText("View Orders ("+ getunsettledorders + ")" );

        int myNum = 0;
        try {
            myNum = Integer.parseInt(returnshift);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }


        System.out.println("shift url  " + myNum);
        setState(myNum);

        if(myNum == 0) {
            viewactionorders.setEnabled(false);
            shiftstate.setText("Off Shift");
            offview.setVisibility(View.VISIBLE);
            shift.setBackgroundColor(Color.RED);

        }

        if(myNum == 1) {

            System.out.println("shift url  " + myNum);
            shift.setBackgroundColor(GREEN);
            shiftstate.setText("On Shift");
            offview.setVisibility(View.INVISIBLE);

        }


    }


    void getviewcount(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        String resulting = response.body().string();

                    }//end void

                });
    }



    void doGetRequest(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {


                        somebits = response.body().string();


                        handler2.post(new Runnable() {
                            @Override
                            public void run() {


                                String[] pieces = somebits.split(Pattern.quote("~"));


                                returnshift = pieces[0];
                                returnshift = returnshift.trim();

                                String getunsettledorders = pieces[1];
                                getunsettledorders = getunsettledorders.trim();
                                viewactionorders.setText("View Orders ("+ getunsettledorders + ")" );
                                setinOrders(getunsettledorders);

                                if(getunsettledorders.equals("0")){
                                    viewactionorders.setEnabled(false);
                                }else{
                                    viewactionorders.setEnabled(true);
                                }

                                int myNum = 0;
                                try {
                                    myNum = Integer.parseInt(returnshift);
                                } catch(NumberFormatException nfe) {
                                    System.out.println("Could not parse " + nfe);
                                }


                                System.out.println("shift url  " + myNum);
                                setState(myNum);

                                if(myNum == 0) {
                                    viewactionorders.setEnabled(false);
                                    shiftstate.setText("Off Shift");
                                    offview.setVisibility(View.VISIBLE);
                                    shift.setBackgroundColor(Color.RED);

                                }

                                if(myNum == 1) {


                                    Intent i = new Intent(getApplicationContext(), MyService.class);
                                    getApplicationContext().startService(i);

                                    System.out.println("shift url  " + myNum);
                                    shift.setBackgroundColor(GREEN);
                                    shiftstate.setText("On Shift");
                                    offview.setVisibility(View.INVISIBLE);

                                }



                            }
                        });


                    }//end if




                });

    }

    public void startplayer(){

        player = MediaPlayer.create(this, R.raw.beep6);
        player.setVolume(20, 20);
        player.start();
        //player.stop();
        //player.release();
    }

    public void Shiftactionset(String cunq, int State){
        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if(State == 0) { //enable
            viewactionorders.setEnabled(true);

            String orderspendin = getinorders();
            if(orderspendin.equals("0")) {
                viewactionorders.setEnabled(false);
            }
            //if its zero....


            Intent i = new Intent(this, MyService.class);
            this.startService(i);

            newstate = 1;
        }

        if(State == 1) {//disable
            viewactionorders.setEnabled(false);
            stopService(new Intent(Dashboard.this, MyService.class));
            newstate = 0;
        }


        String url = "https://axcess.ai/barapp/driver_shiftaction.php?&action=changeshift&cunq="+cunq + "&changestate=" + newstate;
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
            responseLocation =  response.body().string().trim();
            Log.i("respBody:main",responseLocation);
            Log.i("MSG",resp);

            if(responseLocation.equals("updated")){
                setState(newstate);


                if(newstate == 0) {
                    shift.setBackgroundColor(RED);
                    shiftstate.setText("Off Shift");
                    offview.setVisibility(View.VISIBLE);
                }

                if(newstate == 1) {
                    shift.setBackgroundColor(GREEN);
                    shiftstate.setText("On Shift");
                    offview.setVisibility(View.INVISIBLE);
                }



            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }//end function





    public String getordercount( String cunq ) {

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_countorders.php?driverid="+cunq;
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
            responseLocation =  response.body().string();
            Log.i("respBody:main",responseLocation);
            Log.i("MSG",resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseLocation;
    }




    public String Shiftaction( String cunq ) {

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_shiftaction.php?&action=getshift&cunq="+cunq;
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
            responseLocation =  response.body().string();
            Log.i("respBody:main",responseLocation);
            Log.i("MSG",resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseLocation;
    }



    public int getState() {
        return name;
    }

    public void setState(int newName) {
        this.name = newName;
    }

    public String getinorders() {
        return orderspending;
    }

    public void setinOrders(String orderamt) {
        this.orderspending = orderamt;
    }

    @Override
    public void onResume() {
        super.onResume();
        // This registers messageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("my-message"));


        gettershift(cunq);
        checklocationstatus();


    }

    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String myout = intent.getStringExtra("send"); // -1 is going to be used as the default value
            Log.i("url out",myout);


            if(myout.equals("redbtn")) {
                viewinorders.setBackgroundColor(RED);
                viewinorders.setVisibility(View.VISIBLE);
            }

            if(myout.equals("whitebtn")) {
                viewinorders.setBackgroundColor(getResources().getColor(android.R.color.white));
               viewinorders.setVisibility(View.INVISIBLE);
            }



        }
    };

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                //Do your stuff on GPS status change


                new Helpers(context).checklocationstatus();
                  Toast.makeText(getApplicationContext(), "GPS changed ", Toast.LENGTH_LONG).show();
            }
        }
    };



    public void checklocationstatus(){

        LocationManager lm = (LocationManager)getApplication().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            //new AlertDialog.Builder(context)
            Shiftactionset(cunq, 1);

            android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(Dashboard.this);
            dialog.setCancelable(false);
            dialog.setTitle("Location required");
            dialog.setMessage("Turn on your location on the phone to receive job alerts");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                   // getApplication().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    Intent nointernet = new Intent(Dashboard.this, Startgps.class);
                    startActivity(nointernet);




                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                        }

                    });
            final AlertDialog alert = dialog.create();
            alert.show();


        }

    }



    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
        dialog.dismiss();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       /*
        if (requestCode == MY_COARSE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                Intent nopermission = new Intent(Dashboard.this, Startgps.class);
                startActivity(nopermission);

            }
        }
        */

        if (requestCode == MY_FINE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                Intent nopermission = new Intent(Dashboard.this, Startgps.class);
                startActivity(nopermission);

            }


        }






    }



}