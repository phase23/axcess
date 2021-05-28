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
                Intent intent = new Intent(Orderpanel.this, Dashboard.class);

                startActivity(intent);

            }
        });




        String returnorders = getacceptedorders( cunq );
        createLayoutDynamically(returnorders);
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

        TextView newtxt = new TextView(this);
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


            Button btn = new Button(this);
            btn.setId(i);
            btn.setTag(orderid);
            final int accept = btn.getId();
            btn.setText(" Route to Pickup  " );
            params.width = 300;
            btn.setTextSize(25);
            btn.setLayoutParams(pickupbtn);
            btn.setPadding(5, 5, 5, 5 );
            btn.setBackgroundColor(getResources().getColor(R.color.green));
            btn.setTextColor(getResources().getColor(R.color.black));
            layout.addView(btn);


            Button btn2 = new Button(this);
            btn2.setId(idup);
            btn2.setTag(orderid);
            final int decline = btn2.getId();
            btn2.setText(" Route to Drop off " );
            btn2.setTextSize(25);
            btn2.setLayoutParams(dropoffbtn);
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
                    sendorderid = tagname.trim();


                    Intent activity = new Intent(getApplicationContext(), Pickup.class);
                    activity.putExtra("orderid",sendorderid);
                    startActivity(activity);


                }
            });



            btn2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    final String tagname = (String)view.getTag();
                    Log.i("decline tag", tagname);

                }
            });



        }//end make buttons


    }












}