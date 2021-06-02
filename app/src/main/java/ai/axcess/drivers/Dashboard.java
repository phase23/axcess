package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);






        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();





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

        viewactionorders = (Button)findViewById(R.id.actionorders);

        //viewactionorders.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnani));


        driver = (TextView)findViewById(R.id.drivername);
        shiftstate = (TextView)findViewById(R.id.whatshift);
        offview = (TextView)findViewById(R.id.offlinemsg);
        driver.setText(fname);

       // getShift(cunq);
        gettershift(cunq);
        checklocationstatus();




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
    }

    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String myout = intent.getStringExtra("send"); // -1 is going to be used as the default value
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
            dialog.setTitle("GPS STATUS");
            dialog.setMessage("Your GPS is not enabled");
            dialog.setPositiveButton("Start GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //getApplication().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    Intent nointernet = new Intent(Dashboard.this, Startgps.class);
                    startActivity(nointernet);

                }
            })
                    .setNegativeButton("No ", new DialogInterface.OnClickListener() {
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
    public void onBackPressed() {
        dialog.dismiss();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }




}