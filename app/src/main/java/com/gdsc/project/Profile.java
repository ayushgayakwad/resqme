package com.gdsc.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private HashMap<String, Object> map = new HashMap<>();

    private String versionName="";

    private LinearLayout linearx;
    private LinearLayout linears;
    private LinearLayout linear7;
    private LinearLayout linear8;
    private LinearLayout updatecontacts;
    private LinearLayout chbx;
    private CheckBox checkbox;
    private LinearLayout address;
    private LinearLayout ec;
    private Button logout;
    private TextView name;
    private TextView age;
    private TextView gender;
    private TextView city;
    private TextView district;
    private TextView state;
    private TextView insurance;
    private TextView od;
    private TextView c1;
    private TextView c2;
    private TextView c3;
    private TextView bg;
    private ScrollView vscroll1;
    private Intent i = new Intent();
    private SharedPreferences userdata;
    public static SharedPreferences sharedPreferences;
    public static final String myPreferences = "PhoneNumber";
    public static final String phoneNumber1 = "First";
    public static final String phoneNumber2 = "Second";
    public static final String phoneNumber3= "Third";

    private DatabaseReference user = _firebase.getReference("user");
    private ChildEventListener _user_child_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        linear7 = findViewById(R.id.linear7);
        linear8 = findViewById(R.id.linear8);
        linearx = findViewById(R.id.linearx);
        linears = findViewById(R.id.linears);
        updatecontacts = findViewById(R.id.updatecontacts);
        logout = findViewById(R.id.logout);
        vscroll1 = findViewById(R.id.vscroll1);
        chbx = findViewById(R.id.chbx);
        checkbox = findViewById(R.id.checkbox);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        bg = findViewById(R.id.bg);
        gender = findViewById(R.id.gender);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        insurance = findViewById(R.id.insurance);
        od = findViewById(R.id.od);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        address = findViewById(R.id.address);
        ec = findViewById(R.id.ec);

        sharedPreferences = getSharedPreferences(myPreferences,Context.MODE_PRIVATE);
        userdata = getSharedPreferences("userdata", Activity.MODE_PRIVATE);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(0xFFE7EEFB);
        Window window = this.getWindow();
        window.setNavigationBarColor(Color.parseColor("#E23744"));

        vscroll1.setVerticalScrollBarEnabled(false);

        _rippleRoundStroke(logout, "#E23744", "#f5f5f5", 100, 2, "#eeeeee");

        address.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; }}.getIns((int)16, (int)0, 0xFFFFFFFF, 0xFFE7EEFB));
        ec.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; }}.getIns((int)16, (int)0, 0xFFFFFFFF, 0xFFE7EEFB));

        name.setText(userdata.getString("name", ""));
        age.setText("Age: "+userdata.getString("age", ""));
        gender.setText("Gender: "+userdata.getString("gender", ""));
        bg.setText("Blood Group: "+userdata.getString("bg", ""));
        city.setText("City: "+userdata.getString("city", ""));
        district.setText("District: "+userdata.getString("district", ""));
        state.setText("State: "+userdata.getString("state", ""));
        insurance.setText("Insurance: "+userdata.getString("insurance", ""));
        od.setText("Organ Donor: "+userdata.getString("od", ""));
        if(userdata.getString("volunteer", "").equals("true")){
            checkbox.setChecked(true);
        }
        else {
            checkbox.setChecked(false);
        }
        c1.setText("Contact 1: "+sharedPreferences.getString(phoneNumber1,""));
        c2.setText("Contact 2: "+sharedPreferences.getString(phoneNumber2,""));
        c3.setText("Contact 3: "+sharedPreferences.getString(phoneNumber3,""));

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
                final boolean _isChecked = _param2;
                if(_isChecked){
                    userdata.edit().putString("volunteer", "true").commit();
                    map = new HashMap<>();
                    map.put("volunteer", "true");
                    user.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);
                    map.clear();
                }
                else {
                    userdata.edit().putString("volunteer", "false").commit();
                    map = new HashMap<>();
                    map.put("volunteer", "false");
                    user.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);
                    map.clear();
                }
            }
        });

        linears.setOnClickListener(v -> {
            Intent n = new Intent(this,SafeRoutes.class);
            startActivity(n);
            finish();
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });

        linear7.setOnClickListener(v -> {
            Intent n = new Intent(this,Info.class);
            startActivity(n);
            finish();
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });

        linearx.setOnClickListener(v -> {
            Intent n = new Intent(this, Home.class);
            startActivity(n);
            finish();
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });

        updatecontacts.setOnClickListener(v -> {
            Intent n = new Intent(this, AddContacts.class);
            startActivity(n);
            finish();
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                FirebaseAuth.getInstance().signOut();
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setClass(getApplicationContext(), Auth.class);
                startActivity(i);
                finish();
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

    }

    public void _rippleRoundStroke(final View _view, final String _focus, final String _pressed, final double _round, final double _stroke, final String _strokeclr) {
        android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
        GG.setColor(Color.parseColor(_focus));
        GG.setCornerRadius((float)_round);
        GG.setStroke((int) _stroke,
                Color.parseColor("#" + _strokeclr.replace("#", "")));
        android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{ Color.parseColor(_pressed)}), GG, null);
        _view.setBackground(RE);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}