package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Orderpanel extends AppCompatActivity {
    Button back;
    String fname;
    String cunq;
    String responseBody;
    String company;
    String zone;
    String orderid;
    String sendorderid;
    String is_pickedup;
    String driver_accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderpanel);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        fname = shared.getString("sendfname", "");
        cunq = shared.getString("driver", "");

        back = (Button)findViewById(R.id.backbtn);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackgroundColor(getResources().getColor(R.color.gray));
               /*
                Intent intent = new Intent(Orderpanel.this, Dashboard.class);
                startActivity(intent);


                */
                //this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                finish();
            }
        });




        String returnorders = getacceptedorders( cunq );

        try {

            String[] dishout = returnorders.split(Pattern.quote("*"));
            System.out.println("number tickets: " + Arrays.toString(dishout));

            createLayoutDynamically(returnorders);


        } catch(ArrayIndexOutOfBoundsException e) {


        }


    }

    public String getacceptedorders( String cunq ) {

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_getorders.php?&action=acceptedorders&driverid="+cunq;
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

        LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
        layout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
        Params1.setMargins(0, 0, 0, 0);

        LinearLayout.LayoutParams pickupbtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
        pickupbtn.setMargins(0, 0, 0, 10);
        LinearLayout.LayoutParams dropoffbtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
        dropoffbtn.setMargins(0, 0, 0, 60);


        //params.gravity = Gravity.TOP;
        layout.setGravity(Gravity.CENTER|Gravity.TOP);

        params.setMargins(10, 5, 0, 30);

        System.out.println("number scantxt : "+ scantext );
        // String[] separated = scantext.split(Pattern.quote("|"));

        String[] dishout = scantext.split(Pattern.quote("*"));

        int makebtn = dishout.length ;
        String tline;


        String locationid;




        String printwforce = "<br>"
                + makebtn + " ";

        /*
        textView.setText(Html.fromHtml(printwforce));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        */

        TextView newtxt = new TextView(this);
        newtxt.setText(Html.fromHtml(printwforce));
        newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        newtxt.setPadding(0, 0, 0, 20 );
        newtxt.setTypeface(null, Typeface.BOLD);
        newtxt.setGravity(Gravity.CENTER);
        layout.addView(newtxt);

        int idup;
        int idup2;

        System.out.println(makebtn + "number buttons: " + Arrays.toString(dishout));
        for (int i = 0; i < makebtn; i++) {
            idup = i + 20;
            idup2 = idup + 20;


            tline = dishout[i] ;
            String[] sbtns = tline.split("~");
            orderid = sbtns[0];
            company = sbtns[1];
            locationid = sbtns[2];
            driver_accept = sbtns[3];
            is_pickedup = sbtns[4];
            zone = sbtns[5];



            // System.out.println(makebtn + "action listed: " +  printwforce + "col:  " +  imgx );

            TextView panel = new TextView(this);
            panel.setText("From: "+ company + "\n\n To: Zone " + zone );
            panel.setLayoutParams(Params1);
            //panel.setWidth(200);
            panel.setPadding(20, 5, 20, 5 );
            panel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            panel.setTypeface(null, Typeface.BOLD);
            panel.setGravity(Gravity.LEFT);
            layout.addView(panel);
            panel.setBackgroundColor(getResources().getColor(R.color.gray));


            if(is_pickedup.equals("0")) {
                Button btn = new Button(this);
                btn.setId(i);
                btn.setTag(orderid);
                final int accept = btn.getId();
                btn.setText(" Route to Pickup  ");
                params.width = 300;
                btn.setTextSize(25);
                btn.setLayoutParams(pickupbtn);
                btn.setPadding(5, 5, 5, 5);
                btn.setBackgroundColor(getResources().getColor(R.color.green));
                btn.setTextColor(getResources().getColor(R.color.black));
                layout.addView(btn);

                btn = ((Button) findViewById(accept));

                btn.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {

                        final String tagname = (String)view.getTag();
                        Log.i("accept tag", tagname);
                        sendorderid = tagname.trim();


                        Intent activity = new Intent(getApplicationContext(), Pickup.class);
                        activity.putExtra("orderid",sendorderid);
                        activity.putExtra("doaction","pickup");
                        startActivity(activity);


                    }
                });
            }





            Button btn2 = new Button(this);
            btn2.setId(idup);
            btn2.setTag(orderid);
            final int decline = btn2.getId();
            btn2.setText(" Route to Drop off " );
            btn2.setTextSize(25);
            btn2.setLayoutParams(dropoffbtn);
            btn2.setPadding(5, 15, 5, 5 );
            btn2.setBackgroundColor(Color.rgb(249, 249, 249));

            if(driver_accept.equals("1") && is_pickedup.equals("1")){
                btn2.setBackgroundColor(getResources().getColor(R.color.green));
                btn2.setTextColor(getResources().getColor(R.color.black));

            }

            layout.addView(btn2);


            btn2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    final String tagname = (String)view.getTag();
                    Log.i("drop off tag", tagname);

                    sendorderid = tagname.trim();


                    Intent activity = new Intent(getApplicationContext(), Pickup.class);
                    activity.putExtra("orderid",sendorderid);
                    activity.putExtra("doaction","dropoff");
                    startActivity(activity);

                }
            });



            if(is_pickedup.equals("1")) {
                Button btn3 = new Button(this);
                btn3.setId(idup2);
                btn3.setTag(orderid);
                final int dropoff = btn3.getId();
                btn3.setText(" Order Completed ");
                btn3.setTextSize(25);
                btn3.setLayoutParams(dropoffbtn);
                btn3.setPadding(5, 15, 5, 5);
                btn3.setBackgroundColor(Color.rgb(249, 249, 249));
                layout.addView(btn3);

                btn3 = ((Button) findViewById(dropoff));

                btn3.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {

                        final String tagname = (String)view.getTag();
                        Log.i("dropp off tag", tagname);


                        AlertDialog.Builder builder = new AlertDialog.Builder(Orderpanel.this);
                        builder.setTitle("Confirm");

                        builder.setMessage(Html.fromHtml("Confirm  for completion for <br><br>" + company + " to zone "+ zone));

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                sendorderid = tagname.trim();
                                actionorder( cunq ,  sendorderid,  "delivered" );
                                // Do nothing, but close the dialog
                                dialog.dismiss();
                                System.out.println("action numbers tag "+ tagname);

                                Intent intent = new Intent(Orderpanel.this, Dashboard.class);
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

            }

            /*
            if(driver_accept.equals("1")) {
                Log.i("action we got", "green");
                btn.setBackgroundColor(Color.GREEN);
            }

             */


            btn2 = ((Button) findViewById(decline));














        }//end make buttons


    }












}