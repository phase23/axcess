package ai.axcess.drivers;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Notes extends AppCompatActivity {
    String thisorderid;
    String thephone;
    Button back;
    String theroute;
    TextView isphone;
    TextView notes;
    Button rorders;
    Button calldirect;
    Button callwhatsapp;
    Button callskype;
    Button updatecustomer;
    String resulting;
    AlertDialog dialog;
    AlertDialog sdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        thisorderid = getIntent().getExtras().getString("orderid");
        thephone = getIntent().getExtras().getString("passthephone");
        theroute = getIntent().getExtras().getString("theroute");


        rorders = (Button)findViewById(R.id.rvieworders);
        back = (Button)findViewById(R.id.backbtn);

        calldirect = (Button)findViewById(R.id.calldirect);
        callwhatsapp = (Button)findViewById(R.id.callwhatapp);
        callskype = (Button)findViewById(R.id.callskype);
        updatecustomer = (Button)findViewById(R.id.updatecustomer);



        isphone = (TextView)findViewById(R.id.isphone);
        notes = (TextView)findViewById(R.id.notesplace);

        isphone.setText("Phone: " + thephone);

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent = new Intent(Notes.this, Pickup.class);
                intent.putExtra("passthephone",thephone);
                intent.putExtra("orderid",thisorderid);
                intent.putExtra("theroute",theroute);
                startActivity(intent);


            }
        });



        updatecustomer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Notes.this);
                builder.setTitle("Update Customer");

                builder.setMessage(Html.fromHtml("<b>Do you want to update this customer address?</b>"));

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        sdialog = new SpotsDialog.Builder()
                                .setMessage("Updated")
                                .setContext(Notes.this)
                                .build();
                        sdialog.show();


                        try {
                            updateaccount("https://axcess.ai/barapp/driver_updatecustomer.php?orderid=" + thisorderid);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


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




        calldirect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {




                AlertDialog.Builder builder = new AlertDialog.Builder(Notes.this);
                builder.setTitle("CALL CUSTOMER");

                builder.setMessage(Html.fromHtml("<b>Do you want to Call ?</b>"));

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Uri number = Uri.parse("tel:" + thephone);
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



        callwhatsapp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Notes.this);
                builder.setTitle("CALL CUSTOMER");

                builder.setMessage(Html.fromHtml("<b>Do you want to make a whatsapp call ?</b>"));

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        String url = "https://api.whatsapp.com/send?phone=" + thephone;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

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



        callskype.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(Notes.this);
                        builder.setTitle("CALL CUSTOMER");

                        builder.setMessage(Html.fromHtml("<b>Do you want to make a Skype call call ?</b>"));

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                                Intent sky = new Intent("android.intent.action.VIEW");
                                sky.setData(Uri.parse("tel:" + thephone));
                                startActivity(sky);

/*
                                String number = "+919897910168";

                                String uriString = "ms-sfb://call?id="+number;

                                Uri uri = Uri.parse(uriString);
                                Intent callIntent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(callIntent);



                                try {
                                    SkypeApi skypeApi = new SkypeApi(getApplicationContext());

                                    skypeApi.startConversation("con3ro11", Modality.AudioCall);
                                } catch (SkypeSdkException e) {
                                    // Exception handling logic here
                                }
*/

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


                rorders.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {

                        Intent intent = new Intent(Notes.this, Orderpanel.class);
                        startActivity(intent);


                    }
                });


                try {
                    getnotes("https://axcess.ai/barapp/driver_getnotes.php?orderid=" + thisorderid);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


    void updateaccount(String url) throws IOException {

        Log.i("action url", url);

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

                        resulting = response.body().string();
                        sdialog.dismiss();

                    }//end void

                });
    }


    void getnotes(String url) throws IOException {

                Log.i("action url", url);

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

                                resulting = response.body().string();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // For the example, you can show an error dialog or a toast
                                        // on the main UI thread
                                        resulting = resulting.trim();
                                        notes.setText(resulting);
                                    }
                                });

                            }//end void

                        });
            }


            @Override
            public void onBackPressed() {

            }



}