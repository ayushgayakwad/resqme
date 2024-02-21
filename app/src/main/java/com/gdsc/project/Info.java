package com.gdsc.project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Info extends AppCompatActivity {
    private LinearLayout linearx;
    private LinearLayout linears;
    private LinearLayout linear7;
    private LinearLayout linear8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        linear7 = findViewById(R.id.linear7);
        linear8 = findViewById(R.id.linear8);
        linearx = findViewById(R.id.linearx);
        linears = findViewById(R.id.linears);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(0xFFE7EEFB);
        Window window = this.getWindow();
        window.setNavigationBarColor(Color.parseColor("#E23744"));

        linear8.setOnClickListener(v -> {
            Intent n = new Intent(this,Profile.class);
            startActivity(n);
            finish();
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        });

        linears.setOnClickListener(v -> {
            Intent n = new Intent(this,SafeRoutes.class);
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
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}