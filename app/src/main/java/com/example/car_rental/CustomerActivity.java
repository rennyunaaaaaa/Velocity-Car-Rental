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

public class CustomerActivity extends AppCompatActivity {


    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

    public void carListClicked(View view) {
        // forward user to BookListActivity
        Intent intent = new Intent(getApplicationContext(), CarListCustomerActivity.class);
        startActivity(intent);

    }


    public void bookListClicked(View view) {
        // forward user to BookListActivity
        Intent intent = new Intent(getApplicationContext(), BookingListCustomerActivity.class);
        startActivity(intent);
    }
}