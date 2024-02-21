package com.gdsc.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.gdsc.project.AddContacts.phoneNumber1;
import static com.gdsc.project.AddContacts.phoneNumber2;
import static com.gdsc.project.AddContacts.phoneNumber3;
import static com.gdsc.project.AddContacts.myPreferences;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Home extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private String user_location = "";

    private float mAccel;
    private float mAccelCurrent;
    private  float mAccelLast;
    private SensorManager sensorManager;

    private Boolean toggleBoolean;
    private SharedPreferences toggleState;

    private TextView latitude;
    private TextView longitude;
    private ImageView toggle;

    private LinearLayout linearx;
    private LinearLayout linears;
    private LinearLayout linear7;
    private LinearLayout linear8;

    private ScrollView vscroll1;

    static String[] numbers = new String[3];
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    static double mLatitude,mLongitude;
    Button SOS;
    SharedPreferences sharedPreferences;
    private SharedPreferences userdata;
    static Address address;
    private FirebaseAnalytics mFirebaseAnalytics;

    private DatabaseReference user = _firebase.getReference("user");
    private ChildEventListener _user_child_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Need to check permission regularly
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.FOREGROUND_SERVICE
            }, 1);
        }

        SOS = findViewById(R.id.send);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        toggle = findViewById(R.id.toggle);

        linear7 = findViewById(R.id.linear7);
        linear8 = findViewById(R.id.linear8);
        linearx = findViewById(R.id.linearx);
        linears = findViewById(R.id.linears);

        vscroll1 = findViewById(R.id.vscroll1);

        toggleState = getSharedPreferences("toggleState", Activity.MODE_PRIVATE);
        userdata = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(myPreferences,Context.MODE_PRIVATE);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(0xFFE7EEFB);
        Window window = this.getWindow();
        window.setNavigationBarColor(Color.parseColor("#E23744"));

        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        vscroll1.setVerticalScrollBarEnabled(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(sensorManager).registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        linears.setOnClickListener(v -> {
            Intent n = new Intent(this,SafeRoutes.class);
            startActivity(n);
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });

        linear7.setOnClickListener(v -> {
            Intent n = new Intent(this,Info.class);
            startActivity(n);
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });

        linear8.setOnClickListener(v -> {
            Intent n = new Intent(this,Profile.class);
            startActivity(n);
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });


        // sharedPref
        getLocation();

        if(toggleState.getString("toggleState","").equals("true")) {
            toggleBoolean=true;
            toggle.setImageResource(R.drawable.toggle_on);
        }
        else {
            toggleBoolean=false;
            toggle.setImageResource(R.drawable.toggle_off);
        }

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleBoolean){
                    toggle.setImageResource(R.drawable.toggle_off);
                    toggleBoolean=false;
                    toggleState.edit().putString("toggleState", "false").commit();
                    stopService(view);
                }
                else {
                    toggle.setImageResource(R.drawable.toggle_on);
                    toggleBoolean=true;
                    toggleState.edit().putString("toggleState", "true").commit();
                    startService(view);
                }
            }
        });

        _user_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        user.addChildEventListener(_user_child_listener);

        user.removeEventListener(_user_child_listener);
        user_location = "user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user = _firebase.getReference(user_location);

        ValueEventListener valuelistener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot _param1) {
                GenericTypeIndicator <HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap< String, Object>>() {};
                try {
                    HashMap <String, Object> _map= _param1.getValue(_ind);

                    try{
                        userdata.edit().putString("name", _map.get("name").toString()).commit();
                        userdata.edit().putString("age", _map.get("age").toString()).commit();
                        userdata.edit().putString("bg", _map.get("bg").toString()).commit();
                        userdata.edit().putString("gender", _map.get("gender").toString()).commit();
                        userdata.edit().putString("cn", _map.get("cn").toString()).commit();
                        userdata.edit().putString("city", _map.get("city").toString()).commit();
                        userdata.edit().putString("district", _map.get("district").toString()).commit();
                        userdata.edit().putString("state", _map.get("state").toString()).commit();
                        userdata.edit().putString("insurance", _map.get("insurance").toString()).commit();
                        userdata.edit().putString("od", _map.get("od").toString()).commit();
                        userdata.edit().putString("email", _map.get("email").toString()).commit();
                        userdata.edit().putString("volunteer", _map.get("volunteer").toString()).commit();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(phoneNumber1, _map.get("c1").toString());
                        editor.putString(phoneNumber2, _map.get("c2").toString());
                        editor.putString(phoneNumber3, _map.get("c3").toString());
                        editor.apply();
                    }catch(Exception e){

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        user.addValueEventListener(valuelistener2);
    }
    //Foreground Service

    public void startService(View v){
        Intent serviceIntent = new Intent(Home.this, MyService.class);
        ContextCompat.startForegroundService(this,serviceIntent);
    }
    public void stopService(View v){
        Intent serviceIntent = new Intent(this,MyService.class);
        stopService(serviceIntent);
    }

    // callback for requestLocationUpdate

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull @org.jetbrains.annotations.NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
        }
    };

    // SensorListener for onShake trigger feature
    private final SensorEventListener mSensorListener =  new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            //type cast and update the current acceleration
            // event values are vector values to be taken magnitude

            mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z) ;
            float change = mAccelCurrent - mAccelLast;
            // determine acceleration
            mAccel = mAccel* 0.9f + change;
            if(mAccel > 12){
                Toast.makeText(getApplicationContext(),"Shake event detected",Toast.LENGTH_SHORT).show();
                onSend(null);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //
        }
    };

    public static class PowerButton extends BroadcastReceiver {
        public  boolean wasScreenOn = true;
        public  int count = 0;
        public  long firstTime = 0;
        public  long lastTime = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                count++;
                if (count >= 4) {
                    count = 4;
                }
                wasScreenOn = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                count++;
            }
            if (count == 1) {
                firstTime = System.currentTimeMillis();
            } else if (count <= 3) {
                long currentTime = System.currentTimeMillis();
                if   (currentTime - firstTime >= 3000) {
                    count = 0;
                }
            }
            if (count == 4) {
                lastTime = System.currentTimeMillis();
                if (lastTime - firstTime <= 4000) {
                    updateAddress(context);
                    String strAddress = "I'm in danger and here are my current coordinates: " + "\n" +
                            "City : " + address.getSubAdminArea() + "\n" +
                            "State : " + address.getAdminArea() + "\n" +
                            "Country : " + address.getCountryName() + "\n" +
                            "Country Code : " + address.getCountryCode() + "\n";
                    SmsManager smsManager = SmsManager.getDefault();
                    for(String num : numbers){
                        smsManager.sendTextMessage(num,null,strAddress,null,null);
                    }
                }
            }
        }
    }


    // Sending the textMsg
    public void onSend(View v) {
        // Update location everytime we need to send the text
        // Get numbers from the shared preference list

        this.sharedPreferences = getSharedPreferences(myPreferences,Context.MODE_PRIVATE);
        numbers[0] = sharedPreferences.getString(phoneNumber1,"0000000000");
        numbers[1] = sharedPreferences.getString(phoneNumber2,"0000000000");
        numbers[2] = sharedPreferences.getString(phoneNumber3,"0000000000");

        // Geocoder to change latitudes and longitudes into address

        Geocoder geocoder = new Geocoder(this);

        try{
            List<Address> list = geocoder.getFromLocation(mLatitude,mLongitude,1);
            address = list.get(0);
            String strAddress = "I'm in danger and here are my current coordinates: " + "\n" +
                    "City : " + address.getSubAdminArea() + "\n" +
                    "State : " + address.getAdminArea() + "\n" +
                    "Country : " + address.getCountryName() + "\n" +
                    "Country Code : " + address.getCountryCode() + "\n";
            SmsManager smsManager = SmsManager.getDefault();
            for(String num : numbers){
                smsManager.sendTextMessage(num,null,strAddress,null,null);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),"sent",Toast.LENGTH_SHORT).show();
        Intent n = new Intent(Home.this,TextSent.class);
        startActivity(n);
    }

    public static void updateAddress(Context context){
        numbers[0] = context.getSharedPreferences(myPreferences,Context.MODE_PRIVATE).getString(phoneNumber1,"0000000000");
        numbers[1] = context.getSharedPreferences(myPreferences,Context.MODE_PRIVATE).getString(phoneNumber2,"0000000000");
        numbers[2] = context.getSharedPreferences(myPreferences,Context.MODE_PRIVATE).getString(phoneNumber3,"0000000000");
        Geocoder geocoder = new Geocoder(context);
        try{
            List<Address> list = geocoder.getFromLocation(mLatitude,mLongitude,1);
            address = list.get(0);
        }
        catch (IOException e){
            //
        }
    }

    // Updating the global variable latitude and longitude from getLocation
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Permission for location is not granted",Toast.LENGTH_SHORT).show();
            return;
        }
       locationRequest = LocationRequest.create();
       locationRequest.setInterval(100000);
       locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if(location!=null){
                  mLatitude = location.getLatitude();
                  mLongitude = location.getLongitude();

                  latitude.setText((Double.toString(mLatitude)));
                  longitude.setText((Double.toString(mLongitude)));
            }
            else{
                Toast.makeText(getApplicationContext(),"Cannot get location",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}