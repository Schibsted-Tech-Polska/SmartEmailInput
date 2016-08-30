package pl.schibsted.smartemailinput.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import pl.schibsted.smartemailinput.R;
import pl.schibsted.smartemailinput.SmartEmailInput;

public class MainActivity extends AppCompatActivity implements SmartEmailInput.ActivityProvider {

    private SmartEmailInput emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = (SmartEmailInput) findViewById(R.id.email_input);
        emailInput.setActivityProvider(this);
        emailInput.setRationaleMessage("Grant necessary permission for autocomplete");

        TextView versionLabel = (TextView) findViewById(R.id.version_label);
        versionLabel.setText(String.format("Version: %s", BuildConfig.VERSION_NAME));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        emailInput.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public Activity provideActivity() {
        return this;
    }
}
