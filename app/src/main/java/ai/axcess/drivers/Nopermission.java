package ai.axcess.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Nopermission extends AppCompatActivity {
    Button btnrelaunch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nopermission);
        btnrelaunch = (Button)findViewById(R.id.relaunch);



        btnrelaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nointernet = new Intent(Nopermission.this, MainActivity.class);
                startActivity(nointernet);


            }

        });


    }
}