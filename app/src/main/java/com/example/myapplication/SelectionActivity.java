//registration er shomoy kon type er account select korte

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectionActivity extends AppCompatActivity {

    private Button employeeRegisterButton;
    private Button userRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        employeeRegisterButton = findViewById(R.id.employeeRegisterButton);
        userRegisterButton = findViewById(R.id.userRegisterButton);

        employeeRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, EmployeeRegisterActivity.class);
                startActivity(intent);
            }
        });

        userRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}