package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Vieworders extends AppCompatActivity {
    Button back;
    String fname;
    String cunq;
    String responseBody;
    String company;
    String zone;
    String orderid;
    String sendorderid;
    MediaPlayer player;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vieworders);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        fname = shared.getString("sendfname", "");
        cunq = shared.getString("driver", "");

        back = (Button)findViewById(R.id.backbtn);

        progressBar = (ProgressBar)findViewById(R.id.pbProgress);
        progressBar.setVisibility(View.VISIBLE);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackgroundColor(getResources().getColor(R.color.gray));
                Intent intent = new Intent(Vieworders.this, Dashboard.class);

                startActivity(intent);

            }

        });




        //String returnorders = getorders( cunq );
        //createLayoutDynamically(returnorders);


        try {
            returnorders("https://axcess.ai/barapp/driver_getorders.php?&action=liveorders&driverid="+cunq);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    void returnorders(String url) throws IOException{
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


                        try {

                            String[] dishout = resulting.split(Pattern.quote("*"));
                            System.out.println("number tickets: " + Arrays.toString(dishout));
                            //dialog.dismiss();

                            createLayoutDynamically(resulting);


                        } catch(ArrayIndexOutOfBoundsException e) {

                            LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            TextView newtxt = new TextView(getApplicationContext());
                            newtxt.setText(Html.fromHtml("No orders"));
                            newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                            newtxt.setPadding(0, 0, 0, 20 );
                            newtxt.setTypeface(null, Typeface.BOLD);
                            newtxt.setGravity(Gravity.CENTER);
                            layout.addView(newtxt);

                        }









                    }//end void

                });
    }








    public String getorders( String cunq ) {

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_getorders.php?&action=liveorders&driverid="+cunq;
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


    public String actionorder( String cunq , String theorder, String action ) {

/*
        Intent intent = new Intent(Vieworders.this, MyService.class);
        stopService(intent);
        startService(intent);

 */
        Bundle bundle = new Bundle();
        bundle.putString("stophandler", "yes");

        Intent i = new Intent(this, MyService.class);
        this.stopService(i);
        i.putExtras(bundle);
        this.startService(i);

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_dowhatwithorder.php?&action=" + action + "&driver=" + cunq + "&orderid=" + theorder;
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





    private void createLayoutDynamically( String scantext) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(View.INVISIBLE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
        layout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
        Params1.setMargins(0, 0, 0, 0);

        LinearLayout.LayoutParams acceptbtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
        acceptbtn.setMargins(0, 0, 0, 60);
        LinearLayout.LayoutParams declinebtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
        declinebtn.setMargins(0, 0, 0, 60);


        //params.gravity = Gravity.TOP;
        layout.setGravity(Gravity.CENTER|Gravity.TOP);

        params.setMargins(10, 5, 0, 30);

        System.out.println("number scantxt : "+ scantext );
        // String[] separated = scantext.split(Pattern.quote("|"));

        String[] dishout = scantext.split(Pattern.quote("*"));

        int makebtn = dishout.length ;
        String tline;


        String locationid;
        String driver_accept;
        String is_pickedup;


        String printwforce = "<br>"
                + makebtn + " listed";

        /*
        textView.setText(Html.fromHtml(printwforce));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        */

        TextView newtxt = new TextView(getApplicationContext());
        newtxt.setText(Html.fromHtml(printwforce));
        newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        newtxt.setPadding(0, 0, 0, 20 );
        newtxt.setTypeface(null, Typeface.BOLD);
        newtxt.setGravity(Gravity.CENTER);
        layout.addView(newtxt);

           int idup;
        System.out.println(makebtn + "number buttons: " + Arrays.toString(dishout));
        for (int i = 0; i < makebtn; i++) {
         idup = i + 20;



            tline = dishout[i] ;
            String[] sbtns = tline.split("~");
            orderid = sbtns[0];
            company = sbtns[1];
            locationid = sbtns[2];
            zone = sbtns[5];

           // System.out.println(makebtn + "action listed: " +  printwforce + "col:  " +  imgx );

            TextView panel = new TextView(getApplicationContext());
            panel.setText("From: "+ company + "\n\n To: Zone " + zone );
            panel.setLayoutParams(Params1);
            //panel.setWidth(200);
            panel.setPadding(20, 5, 20, 5 );
            panel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            panel.setTypeface(null, Typeface.BOLD);
            panel.setGravity(Gravity.LEFT);
            layout.addView(panel);
            panel.setBackgroundColor(getResources().getColor(R.color.gray));


             Button btn = new Button(getApplicationContext());
            btn.setId(i);
            btn.setTag(orderid);
            final int accept = btn.getId();
            btn.setText(" Accept  " );
            params.width = 300;
            btn.setTextSize(25);
            btn.setLayoutParams(acceptbtn);
            btn.setPadding(5, 5, 5, 5 );
            btn.setBackgroundColor(getResources().getColor(R.color.green));
            btn.setTextColor(getResources().getColor(R.color.black));
            layout.addView(btn);


            Button btn2 = new Button(getApplicationContext());
            btn2.setId(idup);
            btn2.setTag(orderid);
            final int decline = btn2.getId();
            btn2.setText(" Decline  " );
            btn2.setTextSize(25);
            btn2.setLayoutParams(declinebtn);
            btn2.setPadding(5, 15, 5, 5 );
            btn2.setBackgroundColor(Color.rgb(249, 249, 249));
            layout.addView(btn2);




            /*
            if(driver_accept.equals("1")) {
                Log.i("action we got", "green");
                btn.setBackgroundColor(Color.GREEN);
            }

             */

            btn = ((Button) findViewById(accept));
            btn2 = ((Button) findViewById(decline));


            btn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    final String tagname = (String)view.getTag();
                    Log.i("accept tag", tagname);


                    AlertDialog.Builder builder = new AlertDialog.Builder(Vieworders.this);
                    builder.setTitle("Confirm");

                    builder.setMessage(Html.fromHtml("Confirm  for delivery for <br><br>" + company + " to zone "+ zone));

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                             sendorderid = tagname.trim();
                            actionorder( cunq ,  sendorderid,  "accept" );
                            // Do nothing, but close the dialog
                            dialog.dismiss();
                            System.out.println("action numbers tag "+ tagname);

                            Intent intent = new Intent(Vieworders.this, Orderpanel.class);
                            startActivity(intent);

                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();









                }
            });



            btn2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    final String tagname = (String)view.getTag();
                    Log.i("decline tag", tagname);


                    AlertDialog.Builder builder = new AlertDialog.Builder(Vieworders.this);
                    builder.setTitle("Confirm");

                    builder.setMessage(Html.fromHtml("Confirm  decline for <br><br>" + company + " to zone "+ zone));

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            sendorderid = tagname.trim();
                            actionorder( cunq ,  sendorderid,  "decline" );
                            // Do nothing, but close the dialog
                            dialog.dismiss();
                            System.out.println("action numbers tag "+ tagname);

                            Intent intent = new Intent(Vieworders.this, Dashboard.class);
                            startActivity(intent);



                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();




                }
            });



        }//end make buttons


            }
        });


    }










}