package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Earnings extends AppCompatActivity {
    ProgressBar progressBar;
    Button back;
    String fname;
    String cunq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);


        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        back = (Button)findViewById(R.id.earningsbackbtn);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fname = shared.getString("sendfname", "");
        cunq = shared.getString("driver", "");


        progressBar = (ProgressBar)findViewById(R.id.pbProgress_earnings);
        progressBar.setVisibility(View.VISIBLE);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackgroundColor(getResources().getColor(R.color.gray));
                //dialog.dismiss();

                Intent intent = new Intent(Earnings.this, Dashboard.class);
                startActivity(intent);

            }
        });





        try {
            getcompletedorders("https://axcess.ai/barapp/driver_getearnings.php?driverid="+cunq);
        } catch (IOException e) {
            e.printStackTrace();
        }




    }











    void getcompletedorders(String url) throws IOException {
        System.out.println("ernng url  " + url);
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

    private void createLayoutDynamically( String scantext) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);

                       /* LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params3.weight = 1f;
                */


                TableLayout table = new TableLayout(getApplicationContext());
                table.setStretchAllColumns(true);


                String[] dishout = scantext.split(Pattern.quote("*"));
                int makerows = dishout.length ;
                String tline;

                TableRow[] tableheadr = new TableRow[1];
                tableheadr[0] = new TableRow(getApplicationContext());
                tableheadr[0].setGravity(Gravity.CENTER);
                TextView date = new TextView(getApplicationContext());
                date.setGravity(Gravity.LEFT);
                date.setPadding(25,0,0,0);
                date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                date.setTypeface(null, Typeface.BOLD);
                date.setText("Date");
                date.setTextColor(getResources().getColor(R.color.black));

                TextView delr = new TextView(getApplicationContext());
                delr.setGravity(Gravity.CENTER);
                delr.setPadding(5,0,0,0);
                delr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                delr.setTypeface(null, Typeface.BOLD);
                delr.setText("Deliveries");
                delr.setTextColor(getResources().getColor(R.color.black));

                TextView fnds = new TextView(getApplicationContext());
                fnds.setGravity(Gravity.CENTER);
                fnds.setPadding(5,0,0,0);
                fnds.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                fnds.setTypeface(null, Typeface.BOLD);
                fnds.setText("Earnings");
                fnds.setTextColor(getResources().getColor(R.color.black));

                tableheadr[0].addView(date);
                tableheadr[0].addView(delr);
                tableheadr[0].addView(fnds);

                table.addView(tableheadr[0]);



                for (int i = 0; i < makerows; i++) {

                    tline = dishout[i] ;
                    String[] sbtns = tline.split("~");
                    String thedate = sbtns[0].trim();
                    String delivr = sbtns[3].trim();
                    String earn = sbtns[4].trim();
                    String ispaid = sbtns[2].trim();

                    TableRow[] tableRow = new TableRow[makerows];
                    tableRow[i] = new TableRow(getApplicationContext());
                    tableRow[i].setGravity(Gravity.LEFT);
                    TextView pos = new TextView(getApplicationContext());
                    pos.setGravity(Gravity.LEFT);
                    pos.setTextColor(getResources().getColor(R.color.black));
                    pos.setPadding(25,0,0,0);
                    pos.setText(thedate);

                    TextView a = new TextView(getApplicationContext());
                    a.setGravity(Gravity.CENTER);
                    a.setTextColor(getResources().getColor(R.color.black));
                    a.setPadding(5,0,0,0);
                    a.setText(delivr);

                    TextView points = new TextView(getApplicationContext());
                    points.setGravity(Gravity.CENTER);
                    points.setTextColor(getResources().getColor(R.color.black));
                    points.setPadding(5,0,0,0);
                    points.setText("$" + earn);
                    if(ispaid.equals("1")){
                        points.setBackgroundResource(R.color.green);
                    }
                    tableRow[i].addView(pos);
                    tableRow[i].addView(a);
                    tableRow[i].addView(points);



                    table.addView(tableRow[i]);

                    /*
                    TextView newtxt = new TextView(getApplicationContext());
                    newtxt.setText(Html.fromHtml(" test 1 "));
                    newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                    newtxt.setPadding(0, 0, 0, 0 );
                    newtxt.setLayoutParams(params1);
                    newtxt.setTypeface(null, Typeface.BOLD);
                    newtxt.setGravity(Gravity.CENTER);
                    layout.addView(newtxt);


                    TextView newtxt2 = new TextView(getApplicationContext());
                    newtxt2.setText(Html.fromHtml(" test 2"));
                    newtxt2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                    newtxt2.setPadding(0, 0, 0, 0 );
                    newtxt.setLayoutParams(params2);
                    newtxt2.setTypeface(null, Typeface.BOLD);
                    newtxt2.setGravity(Gravity.CENTER);
                    layout.addView(newtxt2);

                    TextView newtxt3 = new TextView(getApplicationContext());
                    newtxt3.setText(Html.fromHtml(" test 3"));
                    newtxt3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                    newtxt3.setPadding(0, 0, 0, 40 );
                    newtxt.setLayoutParams(params3);
                    newtxt3.setTypeface(null, Typeface.BOLD);
                    newtxt3.setGravity(Gravity.CENTER);
                    layout.addView(newtxt3);

 */


                }
                LinearLayout container = (LinearLayout) findViewById(R.id.scnf);
                container.addView(table);

            }
        });

    }//end function




















    @Override
    public void onBackPressed() {

    }



}