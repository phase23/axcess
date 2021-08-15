package ai.axcess.drivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyService extends Service {
    Context mContext;
    LocationManager locationManager;
    public Handler orderhandler;
    public Handler handler;
    public Handler handler2;
    String thedevice;
    String responseBody;
    String cunq;
    MediaPlayer player;
    boolean isRunning = false;
    boolean checkOrder = false;
    String responseLocation;
    String outputthis;
    private final int TWENTY_SECONDS = 20000;
    private final int TW0_SECONDS = 2000;
    String stoprider;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
       // Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("stopchecks"));

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();
        cunq = shared.getString("driver", "");

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground();
        }else {
            startForeground(1337, notification);
        }

        mContext=this;


        orderhandler = new Handler();
        orderhandler.postDelayed(new Runnable() {
            public void run() {


                checkOrder = true;
                isneworder(cunq);

                // this method will contain your almost-finished HTTP calls
                orderhandler.postDelayed(this, TWENTY_SECONDS);
            }
        }, TWENTY_SECONDS);


        locationManager=(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                2000,
                1, locationListenerGPS);



    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
     /*
        // Bundle bundle = intent.getExtras();

        //if (bundle!=null) {
        //   stoprider = bundle.getString("stophandler");
           // handler2.removeCallbacksAndMessages(null);
           // Toast.makeText(this, "Handler must." + stoprider, Toast.LENGTH_LONG).show();
        //}

      */







/*

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {


        try {
            checkneworder("https://axcess.ai/barapp/driver_isneworder.php?test=1&action=checkorder&driverid="+cunq);
        } catch (IOException e) {
            e.printStackTrace();
        }

                handler.postDelayed(this, TWENTY_SECONDS);
            }
        }, TWENTY_SECONDS);


       */

    }


    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            Float bearing = location.getBearing();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            //Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
            Log.d("Changed", " Cordin :" + msg);
            sendlocation(latitude , longitude);
            sendnewlocationtomaps( latitude, longitude, bearing);
            //Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_LONG).show();
        }



            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }





    };



    public void isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

           // sendlocationerrorToActivity();
        } else {


        }
    }


    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }






    public void sendlocation(Double lati , Double longi ) {
/*
        String getdeviceid = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

  */
        String getdeviceid = "ho";

        String url = "https://axcess.ai/barapp/driver_driverlocation.php?latitude="+lati + "&longitude="+ longi + "&cunq=" + cunq;

        Log.i("action url",url);

        OkHttpClient client = new OkHttpClient();


        // String contentType = fileSource.toURL().openConnection().getContentType();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("device",getdeviceid )
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
            Log.i("respBody",responseBody);



            Log.i("MSG",resp);
        } catch (IOException e) {
            e.printStackTrace();
        }






    }


    private void sendnewlocationtomaps(Double lat, Double lon, Float Bearing){
        Intent intent = new Intent("my-location");
        // Adding some data
        String mylatconvert = String.valueOf(lat);
        String mylongconvert = String.valueOf(lon);
        String mybearingconvert = String.valueOf(Bearing);

        intent.putExtra("mylat", mylatconvert);
        intent.putExtra("mylon", mylongconvert);
        intent.putExtra("mybearing", mybearingconvert);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    private void sendalerttoActivity(String msg)
    {
        Intent intent = new Intent("my-message");
        // Adding some data
        intent.putExtra("send", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }








        void checkneworder(String url) throws IOException{
            System.out.println("url " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(final Call call, IOException e) {
                            // Error

                            handler = new Handler();
                            Thread thread = new Thread() {
                            //runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // For the example, you can show an error dialog or a toast
                                    // on the main UI thread
                                }
                            };
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            String outputthis = response.body().string();

                            outputthis = outputthis.trim();

                            int myNum = 0;
                            try {
                                myNum = Integer.parseInt(outputthis);
                            } catch(NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }

                            if(myNum == 0){
                                if(player != null){
                                    player.stop();
                                }

                                if(isRunning) {
                                    handler2.removeCallbacksAndMessages(null);
                                    isRunning = false;
                                }

                                sendalerttoActivity("whitebtn");
                            }else {

                                sendalerttoActivity("redbtn");
                                handler2 = new Handler();

                                handler2.postDelayed(new Runnable() {
                                    public void run() {
                                        isRunning = true;
                                        startplayer();

                                        handler2.postDelayed(this, TW0_SECONDS);
                                    }
                                }, TW0_SECONDS);

                            }





                        }//end void

                    });
        }











    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String send = intent.getStringExtra("send");
            //Log.d(TAG, "Data received is : " +  intent.getStringExtra("message"));
            //Toast.makeText(getApplicationContext(), "We got off." + send, Toast.LENGTH_LONG).show();

            //check if its running
                Log.i("check we in..","in");
                if(checkOrder) {
                   // Log.i("check order pass..","in");
                    orderhandler.removeCallbacksAndMessages(null);
                    checkOrder = false;
                }

            if(player != null){
                player.stop();
            }


            if(isRunning) {
                handler2.removeCallbacksAndMessages(null);
                isRunning = false;
            }




        }
    };


    public void onResume() {
        getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("stopchecks"));
    }

    protected void onPause() {
        getApplicationContext().unregisterReceiver(broadcastReceiver);
    }



    public void isneworder(String driverid) {


        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_isneworder.php?action=checkorder&driverid="+driverid;
        Log.i("action url",url);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("deviceid",thisdevice )
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

            String somebits = responseLocation.trim();

            String[] pieces = somebits.split(Pattern.quote("~"));
            outputthis = pieces[0];
            Log.i("check new order",outputthis);

            String shiftstat = pieces[1].trim();
            Log.i("check shiftstat",shiftstat);



            int myNum = 0;
            try {
                myNum = Integer.parseInt(outputthis);
            } catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            if(myNum == 0){
                if(player != null){
                    player.stop();
                }
                if(isRunning) {
                    handler2.removeCallbacksAndMessages(null);
                    isRunning = false;
                }

                sendalerttoActivity("whitebtn");
            }else {


                if (isRunning) {
                    sendalerttoActivity("redbtn");
                    } else {


                        handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            public void run() {
                                isRunning = true;
                                startplayer();
                                sendalerttoActivity("redbtn");
                                handler2.postDelayed(this, TW0_SECONDS);
                                }
                            }, TW0_SECONDS);

                    }
                }

            Log.i("respBody:outthis",outputthis);

        } catch (IOException e) {
            e.printStackTrace();
        }






    }//emd


    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());


        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void startplayer(){

        player = MediaPlayer.create(this, R.raw.beep08b);
        player.setVolume(20, 20);
        player.start();
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)

                .setContentTitle("Drivers")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }



}
