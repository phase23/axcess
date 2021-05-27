package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Startgps extends AppCompatActivity {
    Button btnrelaunch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgps);
        btnrelaunch = (Button)findViewById(R.id.relaunch);

        btnrelaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nointernet = new Intent(Startgps.this, MainActivity.class);
                startActivity(nointernet);


            }

        });




    }
}