package pl.schibsted.smartemailinput.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import pl.schibsted.smartemailinput.R;
import pl.schibsted.smartemailinput.SmartEmailInput;

public class MainActivity extends AppCompatActivity {

    private SmartEmailInput emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = (SmartEmailInput) findViewById(R.id.email_input);
        emailInput.setActivityProvider(new SmartEmailInput.ActivityProvider() {
            @Override
            public Activity provideActivity() {
                return MainActivity.this;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        emailInput.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
