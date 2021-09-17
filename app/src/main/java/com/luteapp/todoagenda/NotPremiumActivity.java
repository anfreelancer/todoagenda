package com.luteapp.todoagenda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotPremiumActivity extends AppCompatActivity {
    private Button btnPremium;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_premium);
        btnPremium = findViewById(R.id.btnPremium);
        btnPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotPremiumActivity.this, ShopActivity.class));
            }
        });

    }
}
