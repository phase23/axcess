package ai.axcess.drivers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity {

    Button setup;
    EditText thename;
    EditText thelname;
    EditText thephone;
    EditText theaddress;

    EditText passwrd;
    EditText cpasswrd;
    String responseLocation;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_STORAGE_REQUEST_CODE = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://axcessdrivers-default-rtdb.firebaseio.com/");
        //DatabaseReference myRef = database.getReference("driverid"); // yourwebisteurl/rootNode if it exist otherwise don't pass any string to it.
        //DatabaseReference pols = database.getReference("plco");
        //pols.setValue("befire");

        //DatabaseReference collection = database.getReference("drivers");
        //collection.setValue("driver2");

        String thisdevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);



        DatabaseReference newdriver = database.getReference(thisdevice); // yourwebisteurl/rootNode if it exist otherwise don't pass any string to it.

                    newdriver.child("latitude").setValue("18.205397742382385");
                    newdriver.child("longitude").setValue("-63.062720587183264");
                    newdriver.child("status").setValue("waiting");








        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

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

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }


        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STORAGE_REQUEST_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                Intent nopermission = new Intent(Register.this, Nopermission.class);
                startActivity(nopermission);

            }
        }


        if (requestCode == MY_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "storage permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "storage permission denied", Toast.LENGTH_LONG).show();
                Intent nopermission = new Intent(Register.this, Nopermission.class);
                startActivity(nopermission);

            }
        }







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

/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }




}