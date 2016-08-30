# What is it?
It's AutoompleteTextView on steroids that handles device accounts querrying - very convinient for login screens. It also handles runtime permissions smoothly.

#How to use it?

####Just add the view in your layout:

```xml
<pl.schibsted.smartemailinput.SmartEmailInput
  android:id="@+id/email_input"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:hint="e-mail" />
```
  
####Setup the input: 

```java
public class MainActivity extends AppCompatActivity {

    private SmartEmailInput emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = (SmartEmailInput) findViewById(R.id.email_input);
        
        //input will need to use Activity at some point
        emailInput.setActivityProvider(new SmartEmailInput.ActivityProvider() {
            @Override
            public Activity provideActivity() {
                return MainActivity.this;
            }
        });
        
        //This is optional. By default this is default message.
        emailInput.setRationaleMessage("Grant necessary permission for autocomplete");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        emailInput.onRequestPermissionsResult(requestCode, permissions, grantResults); //Don't forget that - it's responsible for runtime permission handling
    }
}
```

#### Give the app GET_ACCOUNTS permission for pre-Marshmallow devices.
```<uses-permission android:name="android.permission.GET_ACCOUNTS" />```


##Gradle dependency
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
    
dependencies {
    compile 'com.github.Schibsted-Tech-Polska:SmartEmailInput:1.0.3'
}
```
