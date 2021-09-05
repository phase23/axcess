package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Registerphoto_result extends AppCompatActivity {
    Button finishbtn;
    Intent intent = getIntent();
    public String responsethis;
    private ImageView imageView;
    Button btnrelaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerphoto_result);
        imageView = (ImageView)findViewById(R.id.imageViewres);
        responsethis = getIntent().getExtras().getString("passthis");
        String[] separated = responsethis.split("~");
        String rekstat = separated[0].trim();

        Log.i("url in-", responsethis);
        Log.i("url rek-", rekstat);


        btnrelaunch = (Button)findViewById(R.id.btnfinish);

        btnrelaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nointernet = new Intent(Registerphoto_result.this, MainActivity.class);
                startActivity(nointernet);


            }

        });








        rekstat = rekstat.trim();

        if(rekstat.equals("1")) {
            String url = separated[1];
                    url = url.trim();
            System.out.println("url: " + url);
            new DownloadImageTask(imageView)
                    .execute("https://axcess.ai/barapp/" + url);



        }else {



        }


    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            Bitmap mIcon11 = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()) {
                try {
                    mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }




}