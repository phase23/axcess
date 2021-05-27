package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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

public class Vieworders extends AppCompatActivity {
    Button back;
    String fname;
    String cunq;
    String responseBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vieworders);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        fname = shared.getString("sendfname", "");
        cunq = shared.getString("driver", "");

        back = (Button)findViewById(R.id.backbtn);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackgroundColor(getResources().getColor(R.color.gray));
                Intent intent = new Intent(Vieworders.this, Dashboard.class);

                startActivity(intent);

            }

        });




        String returnorders = getorders( cunq );
        createLayoutDynamically(returnorders);

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



    private void createLayoutDynamically( String scantext) {

        LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
        layout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //params.gravity = Gravity.TOP;
        layout.setGravity(Gravity.CENTER|Gravity.TOP);

        params.setMargins(10, 5, 0, 30);

        System.out.println("number scantxt : "+ scantext );
        // String[] separated = scantext.split(Pattern.quote("|"));

        String[] dishout = scantext.split(Pattern.quote("*"));

        int makebtn = dishout.length ;
        String tline;
        String company;
        String orderid;
        String locationid;
        String driver_accept;
        String is_pickedup;


        String printwforce = "<br>"
                + makebtn + " Orders listed";

        /*
        textView.setText(Html.fromHtml(printwforce));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        */

        TextView newtxt = new TextView(this);
        newtxt.setText(Html.fromHtml(printwforce));
        newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        newtxt.setTypeface(null, Typeface.BOLD);
        newtxt.setGravity(Gravity.CENTER);
        layout.addView(newtxt);


        System.out.println(makebtn + "number buttons: " + Arrays.toString(dishout));
        for (int i = 0; i < makebtn; i++) {


            tline = dishout[i] ;
            String[] sbtns = tline.split("~");
            orderid = sbtns[0];
            company = sbtns[1];
            locationid = sbtns[2];
            driver_accept = sbtns[3];
            is_pickedup = sbtns[3];

           // System.out.println(makebtn + "action listed: " +  printwforce + "col:  " +  imgx );

            Button btn = new Button(this);





            btn.setId(i);
            btn.setTag(orderid);
            final int id_ = btn.getId();
            btn.setText(" " + company + " " );
            params.width = 300;
            btn.setLayoutParams(params);
            btn.setPadding(5, 5, 5, 5 );
            btn.setBackgroundColor(Color.rgb(249, 249, 249));
            layout.addView(btn);

            if(driver_accept.equals("1")) {
                Log.i("action we got", "green");
                btn.setBackgroundColor(Color.GREEN);
            }

            btn = ((Button) findViewById(id_));

            btn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {


                }
            });

        }//end make buttons






    }

}