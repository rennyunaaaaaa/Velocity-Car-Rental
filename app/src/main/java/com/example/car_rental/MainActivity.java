package com.example.car_rental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.car_rental.model.User;
import com.example.car_rental.sharedpref.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //checking role from shared preferences
        //checkUserRole();

        // get references
        tvHello = findViewById(R.id.tvHello);

        // greet the user
        // if the user is not logged in we will directly them to LoginActivity
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) { // no session record
        // stop this MainActivity
            finish();
        // forward to Login Page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
        // Greet user
            User user = spm.getUser();
            tvHello.setText("Hello " + user.getUsername());
        }
    }

    public void logoutClicked(View view) {
            // clear the shared preferences
            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
            spm.logout();

            // display message
            Toast.makeText(getApplicationContext(),
                    "You have successfully logged out.",
                    Toast.LENGTH_LONG).show();

            // terminate this MainActivity
            finish();

            // forward to Login Page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
    }

    //check role if customer
    private void checkUserRole() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        if (user != null) {

            if (user.getRole().equals("customer")) {
                // Redirect to CustomerActivity
                startActivity(new Intent(MainActivity.this, CustomerActivity.class));
                finish();  // Finish MainActivity
            }
        }
    }
    public void carListClicked(View view) {
        // forward user to carListActivity
        Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
        startActivity(intent);

    }

    public void bookListClicked(View view) {
        // forward user to BookingListActivity
        Intent intent = new Intent(getApplicationContext(), BookingListActivity.class);
        startActivity(intent);
    }
}