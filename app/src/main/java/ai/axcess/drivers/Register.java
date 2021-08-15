package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class Register extends AppCompatActivity {

    Button setup;
    EditText thename;
    EditText thelname;
    EditText thephone;
    EditText theaddress;

    EditText passwrd;
    EditText cpasswrd;
    String responseLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setup = (Button)findViewById(R.id.gonext);
        thename = (EditText)findViewById(R.id.yourname);
        thelname = (EditText)findViewById(R.id.yourlname);

        thephone = (EditText)findViewById(R.id.phonenumber);
        theaddress = (EditText)findViewById(R.id.address);

        passwrd = (EditText)findViewById(R.id.pass);
        cpasswrd = (EditText)findViewById(R.id.cpass);



        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent bphotosetup = new Intent(Register.this, Registerphoto.class);
                startActivity(bphotosetup);



                String thisname = thename.getText().toString();
                String thislname = thelname.getText().toString();

                String thisphone = thephone.getText().toString();
                String thisaddress = theaddress.getText().toString();



                String thispass = passwrd.getText().toString();
                String thiscpass = cpasswrd.getText().toString();


                if (thisname.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }



                if (thislname.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter your last name", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (thisphone.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (thisaddress.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (thispass.matches("")) {
                    Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!thispass.equals(thiscpass)) {
                    Toast.makeText(getApplicationContext(), "Your password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (thispass.length() < 3) {
                    Toast.makeText(getApplicationContext(), "Your password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }



                String addDevice = postStartup(thisname,thislname , thisphone, thisaddress, thispass);
                    addDevice = addDevice.trim();

                    if(addDevice.equals("registered")){
                        Intent photosetup = new Intent(Register.this, Registerphoto.class);
                        startActivity(photosetup);


                    }else {

                        Toast.makeText(getApplicationContext(), "There was an error, with your registration", Toast.LENGTH_SHORT).show();
                        return;
                    }





            }
        });




    }


    public String postStartup( String thisname, String thislname,  String thisphone, String thisaddress, String thispass ) {

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String url = "https://axcess.ai/barapp/driver_devicesetup.php?action=register&token="+thisdevice;
        Log.i("action url",url);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("name",thisname )
                .addFormDataPart("lname",thislname )

                .addFormDataPart("phone",thisphone )
                .addFormDataPart("address",thisaddress )


                .addFormDataPart("pin",thispass )

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
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }



}