package yu.com.Wifi.wifiway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import yu.com.Wifi.R;

public class User extends AppCompatActivity {
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mReturnButton = findViewById(R.id.returnback);
        mReturnButton.setOnClickListener(v -> {
            Intent intent = new Intent(User.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}