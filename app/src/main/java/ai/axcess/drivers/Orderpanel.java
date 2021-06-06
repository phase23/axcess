package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    ProgressBar progressBar;
    String customerphonenumber;
    String locationto;
    String passthephone;
    String ordernumb;
    AlertDialog dialog;
    public Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderpanel);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
/*
        dialog = new SpotsDialog.Builder()
                .setMessage("Now loading...")
                .setContext(Orderpanel.this)
                .build();
        dialog.show();
*/
        progressBar = (ProgressBar)findViewById(R.id.pbProgress);
        progressBar.setVisibility(View.VISIBLE);

        fname = shared.getString("sendfname", "");
        cunq = shared.getString("driver", "");

        back = (Button)findViewById(R.id.backbtn);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackgroundColor(getResources().getColor(R.color.gray));
                //dialog.dismiss();

                Intent intent = new Intent(Orderpanel.this, Dashboard.class);
                startActivity(intent);

            }
        });



        try {
            returnorders("https://axcess.ai/barapp/driver_getorders.php?&action=acceptedorders&driverid="+cunq);
        } catch (IOException e) {
            e.printStackTrace();
        }


/*
        String returnorders = getacceptedorders( cunq );

        try {

            String[] dishout = returnorders.split(Pattern.quote("*"));
            System.out.println("number tickets: " + Arrays.toString(dishout));
            createLayoutDynamically(returnorders);


        } catch(ArrayIndexOutOfBoundsException e) {

            LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
            layout.setOrientation(LinearLayout.VERTICAL);

            TextView newtxt = new TextView(this);
            newtxt.setText(Html.fromHtml("No orders"));
            newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            newtxt.setPadding(0, 0, 0, 20 );
            newtxt.setTypeface(null, Typeface.BOLD);
            newtxt.setGravity(Gravity.CENTER);
            layout.addView(newtxt);

        }

 */


    }


    @Override
    public void onBackPressed() {

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



















    public String actionorder( String cunq , String theorder, String action ) {


        Bundle bundle = new Bundle();
        bundle.putString("stophandler", "yes");

       // Intent i = new Intent(this, MyService.class);
        //this.stopService(i);
        //i.putExtras(bundle);
        //this.startService(i);

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
            //okhttp3.Response response = client.newCall(request).execute();
            Response response = client.newCall(request).execute();
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

            TextView newtxt = new TextView(getApplicationContext());
            newtxt.setText(Html.fromHtml(" "));
            newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            newtxt.setPadding(0, 0, 0, 40 );
            newtxt.setTypeface(null, Typeface.BOLD);
            newtxt.setGravity(Gravity.CENTER);
            layout.addView(newtxt);



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


/*

        String printwforce = "<br>"
                + makebtn + " ";


        textView.setText(Html.fromHtml(printwforce));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        */


        int idup;
        int idup2;
        int idup3;
        System.out.println(makebtn + "number buttons: " + Arrays.toString(dishout));
        for (int i = 0; i < makebtn; i++) {
            idup = i + 20;
            idup2 = idup + 20;
            idup3 = idup2 + 20;

            tline = dishout[i] ;
            String[] sbtns = tline.split("~");
            orderid = sbtns[0];
            company = sbtns[1];
            locationid = sbtns[2];
            driver_accept = sbtns[3];
            is_pickedup = sbtns[4];
            zone = sbtns[5];
            customerphonenumber = sbtns[6];
            ordernumb = sbtns[7];
            locationto = sbtns[8];

            // System.out.println(makebtn + "action listed: " +  printwforce + "col:  " +  imgx );

            TextView panel = new TextView(getApplicationContext());
            panel.setText("From: "+ company + "\nTo: " + locationto + "\nOrder No:" +  ordernumb + "\n\n" );

            if(is_pickedup.equals("1")) {

                panel.setText("From: "+ company + "\nTo: " + locationto + "\nOrder No:" +  ordernumb + " (Order picked-up)\n\n" );
            }

            panel.setLayoutParams(Params1);
            //panel.setWidth(200);
            panel.setPadding(20, 5, 20, 15 );
            panel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            panel.setTypeface(null, Typeface.BOLD);
            panel.setGravity(Gravity.LEFT);
            panel.setTextColor(getResources().getColor(R.color.black));
            layout.addView(panel);
            panel.setBackgroundColor(getResources().getColor(R.color.gray));


            Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_contact_phone_24);

            if(is_pickedup.equals("1")) {
                Button customerphone = new Button(getApplicationContext());
                customerphone.setId(idup3);
                customerphone.setTag(customerphonenumber);
                final int cusphone = customerphone.getId();
                customerphone.setText(" Call customer");
                customerphone.setTextColor(getResources().getColor(R.color.black));
                customerphone.setTextSize(25);
                customerphone.setLayoutParams(dropoffbtn);

                customerphone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_contact_phone_24, 0, 0, 0);
                customerphone.setPadding(5, 15, 5, 5);
                customerphone.setBackgroundColor(Color.rgb(249, 249, 249));
                layout.addView(customerphone);
                customerphone = ((Button) findViewById(cusphone));



                customerphone.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {

                        final String tagname = (String)view.getTag();
                        Log.i("accept tag", tagname);


                        AlertDialog.Builder builder = new AlertDialog.Builder(Orderpanel.this);
                        builder.setTitle("CALL CUSTOMER");

                        builder.setMessage(Html.fromHtml("Do you want to Call ?"));

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String phonenumber = tagname.trim();

                                Uri number = Uri.parse("tel:" + phonenumber);
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                startActivity(callIntent);

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

            if(is_pickedup.equals("0")) {
                Button btn = new Button(getApplicationContext());
                btn.setId(i);
                btn.setTag(orderid + "~" + company + "~" + locationto + '~' + customerphonenumber);
                final int routetopickup = btn.getId();
                btn.setText(" Route to Pickup  ");
                params.width = 300;
                btn.setTextSize(25);
                btn.setLayoutParams(pickupbtn);
                btn.setPadding(5, 5, 5, 5);
                btn.setBackgroundColor(getResources().getColor(R.color.green));
                btn.setTextColor(getResources().getColor(R.color.black));
                layout.addView(btn);

                btn = ((Button) findViewById(routetopickup));

                btn.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {


                        dialog = new SpotsDialog.Builder()
                                .setMessage("Calculating")
                                .setContext(Orderpanel.this)
                                .build();
                        dialog.show();



                        final String tagname = (String)view.getTag();
                        Log.i("pickup tag", tagname);


                        String disrupttag = tagname.trim();
                        String[] desrupted = disrupttag.split("~");
                        sendorderid = desrupted[0];
                        passthephone = desrupted[3];



                                gettheroutes( sendorderid,  passthephone, "pickup" );







                    }
                });
            }





            Button btn2 = new Button(getApplicationContext());
            btn2.setId(idup);
            btn2.setTag(orderid + "~" + company + "~" + locationto + '~' + customerphonenumber);
            final int routetodrop = btn2.getId();
            btn2.setText(" Route to Drop off " );
            btn2.setTextColor(getResources().getColor(R.color.black));
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

                    dialog = new SpotsDialog.Builder()
                            .setMessage("Calculating")
                            .setContext(Orderpanel.this)
                            .build();
                    dialog.show();




                    final String tagname = (String)view.getTag();
                    Log.i("drop off tag", tagname);



                    String disrupttag = tagname.trim();
                    String[] desrupted = disrupttag.split("~");
                    sendorderid = desrupted[0];
                    passthephone = desrupted[3];


                    gettheroutes(sendorderid, passthephone, "dropoff");



                }
            });



            if(is_pickedup.equals("1")) {
                Button btn3 = new Button(getApplicationContext());
                btn3.setId(idup2);
                btn3.setTag(orderid + "~" + company + "~" + locationto + '~' + customerphonenumber);
                final int dropoff = btn3.getId();
                btn3.setText(" Order Completed " );
                btn3.setTextSize(25);
                btn3.setLayoutParams(dropoffbtn);
                btn3.setPadding(5, 15, 5, 5);
                //btn3.setBackgroundColor(Color.rgb(249, 249, 249));
                btn3.setTextColor(getResources().getColor(R.color.black));
                layout.addView(btn3);

                btn3 = ((Button) findViewById(dropoff));

                btn3.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {

                        final String tagname = (String)view.getTag();
                        Log.i("dropp off tag", tagname);

                        String splittag = tagname.trim();
                        String[] minitags = splittag.split("~");
                        String outcompany = minitags[1];
                        String outzone = minitags[2];

                        AlertDialog.Builder builder = new AlertDialog.Builder(Orderpanel.this);
                        builder.setTitle("Confirm");

                        builder.setMessage(Html.fromHtml(    " Confirm  for completion for <br><br>" + outcompany + " to  "+ outzone));

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                String disrupttag = tagname.trim();
                                String[] desrupted = disrupttag.split("~");
                                 sendorderid = minitags[0];

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


            btn2 = ((Button) findViewById(routetodrop));


        }//end make buttons



            }
        });

    }



    public void  gettheroutes(String orderid, String passthephone, String doaction ){





        try {
            returnroute("https://axcess.ai/barapp/driver_route.php?&action=" + doaction + "&driverid="+cunq + "&orderid=" + orderid);

        } catch (IOException e) {
            e.printStackTrace();
        }




    }


    void returnroute(String url) throws IOException{
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

                        Intent activity = new Intent(getApplicationContext(), Pickup.class);
                        activity.putExtra("orderid",sendorderid);
                        activity.putExtra("passthephone",passthephone);
                        activity.putExtra("theroute",resulting);
                        startActivity(activity);

                        dialog.dismiss();

                    }//end void

                });
    }








}