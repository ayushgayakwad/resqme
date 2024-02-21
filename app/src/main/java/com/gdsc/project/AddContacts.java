package com.gdsc.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;

public class AddContacts extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private HashMap<String, Object> map = new HashMap<>();

    EditText phone1,phone2,phone3;
    Button save;
    public static SharedPreferences sharedPreferences;
    public static final String myPreferences = "PhoneNumber";
    public static final String phoneNumber1 = "First";
    public static final String phoneNumber2 = "Second";
    public static final String phoneNumber3= "Third";
    public String input1;
    public String input2;
    public String input3;

    private LinearLayout linearx;
    private LinearLayout linears;
    private LinearLayout linear7;
    private LinearLayout linear8;

    private DatabaseReference user = _firebase.getReference("user");
    private ChildEventListener _user_child_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        phone1 = findViewById(R.id.contact1);
        phone2 = findViewById(R.id.contact2);
        phone3 = findViewById(R.id.contact3);
        save = findViewById(R.id.svBtn);

        linear7 = findViewById(R.id.linear7);
        linear8 = findViewById(R.id.linear8);
        linearx = findViewById(R.id.linearx);
        linears = findViewById(R.id.linears);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(0xFFE7EEFB);
        Window window = this.getWindow();
        window.setNavigationBarColor(Color.parseColor("#E23744"));

        _rippleRoundStroke(save, "#E23744", "#f5f5f5", 100, 2, "#eeeeee");

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

        sharedPreferences = getSharedPreferences(myPreferences,Context.MODE_PRIVATE);

        phone1.setText(sharedPreferences.getString(phoneNumber1,""));
        phone2.setText(sharedPreferences.getString(phoneNumber2,""));
        phone3.setText(sharedPreferences.getString(phoneNumber3,""));

        save.setOnClickListener(v -> {
            if(phone1.getText().toString().trim().equals("")||phone2.getText().toString().trim().equals("")||phone3.getText().toString().trim().equals("")||phone1.getText().toString().trim().length()<10||phone1.getText().toString().trim().length()>15||phone2.getText().toString().trim().length()<10||phone2.getText().toString().trim().length()>15||phone3.getText().toString().trim().length()<10||phone3.getText().toString().trim().length()>15) {
                Toast.makeText(AddContacts.this,"Please add 3 valid contacts...",Toast.LENGTH_SHORT).show();
            }
            else {
                input1 = phone1.getText().toString();
                input2 = phone2.getText().toString();
                input3 = phone3.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(phoneNumber1, input1);
                editor.putString(phoneNumber2, input2);
                editor.putString(phoneNumber3, input3);
                editor.apply();

                map = new HashMap<>();
                map.put("c1", input1);
                map.put("c2", input2);
                map.put("c3", input3);
                user.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);
                map.clear();
                Toast.makeText(AddContacts.this, "Contacts saved", Toast.LENGTH_SHORT).show();
                Intent n = new Intent(this, Profile.class);
                startActivity(n);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        Intent n = new Intent(this, Profile.class);
        startActivity(n);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}