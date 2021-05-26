package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    EditText pin;
    Button llogin;
    String responseLocation;
    String cunq;
    String fname;

    SharedPreferences sharedpreferences;
    int autoSave;
    //BroadcastReceiver  messageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Intent i = new Intent(this, MyService.class);
        this.startService(i);

        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        llogin = (Button)findViewById(R.id.llogin);
        pin = (EditText)findViewById(R.id.driverno);


        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }



        if(!connected) {
            Toast.makeText(getApplicationContext(),"Check Internet & Restart App",Toast.LENGTH_LONG).show();
            Intent nointernet = new Intent(MainActivity.this, Nointernet.class);
            startActivity(nointernet);

            }else {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            int j = sharedpreferences.getInt("key", 0);
            if(j > 0){
                Intent activity = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(activity);
            }



            llogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String thispin = pin.getText().toString();

                    if (thispin.matches("")) {
                        Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    String postaction = postLogin(thispin);
                    postaction = postaction.trim();
                    Log.i("[print]",postaction);

                    String[] separated = postaction.split("~");
                    String dologin = separated[0];
                    cunq = separated[1];

                    if(dologin.equals("noluck")){
                        Toast.makeText(getApplicationContext(), "Your password is incorrect", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(dologin.equals("sucess")){
                         fname = separated[2];
                        Log.i("pass:unq -- ",cunq + "name: "+ fname);

                        autoSave = 1;
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("key", autoSave);
                        editor.putString("driver", cunq);
                        editor.putString("sendfname", fname);
                        editor.apply();


                        // Toast.makeText(getApplicationContext(), "Success "+ cunq, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, Dashboard.class);
                        intent.putExtra("driver",cunq);
                        intent.putExtra("sendfname",fname);
                        startActivity(intent);

                    }




                }

            });

        }








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
            Log.i("SUCC", myout);

        }
    };


    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }



    public String postLogin( String thispin ) {

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_driverlogin.php?&token="+thisdevice;
        Log.i("action url",url);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("driverno",thispin )

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



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();

        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        return true;
    }



}