package yu.com.Wifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import yu.com.Wifi.R;
import yu.com.Wifi.TopActivityViewer;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Button opener = findViewById(R.id.opener);
        Button closer = findViewById(R.id.closer);
        opener.setOnClickListener(v -> {
            TopActivityViewer.getTopActivityTask(this);
        });
        closer.setOnClickListener(v -> {
            TopActivityViewer.stop();
        });
    }
}