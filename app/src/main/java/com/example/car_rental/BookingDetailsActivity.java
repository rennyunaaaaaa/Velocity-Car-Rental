package com.example.car_rental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.car_rental.R;
import com.example.car_rental.model.Booking;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.BookingService;
import com.example.car_rental.sharedpref.SharedPrefManager;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsActivity extends AppCompatActivity {

    private BookingService bookingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve borrow details based on selected id

        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int bookingId = intent.getIntExtra("booking_id", -1);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        bookingService = ApiUtils.getBookingService();

        // execute the API query. send the token and book id
        bookingService.getBooking(token, bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get booking object from response
                    Booking booking = response.body();

                    // get references to the view elements
                    TextView tvTitle = findViewById(R.id.tvTitle);
                    TextView tvBookingId = findViewById(R.id.tvBookingid);
                    TextView tvCustomerId = findViewById(R.id.tvCustid);
                    TextView tvBrand = findViewById(R.id.tvBrand);
                    TextView tvModel = findViewById(R.id.tvModel);
                    TextView tvYear = findViewById(R.id.tvYear);
                    TextView tvColor = findViewById(R.id.tvColor);
                    TextView tvPickUp = findViewById(R.id.tvPickup);
                    TextView tvDropOff = findViewById(R.id.tvDropoff);
                    TextView tvLicensePlate = findViewById(R.id.tvLicensePlate);
                    TextView tvStatusCar = findViewById(R.id.tvStatuscar);
                    TextView tvStatusBook = findViewById(R.id.tvStatusbook);
                    TextView tvNote = findViewById(R.id.tvNotes);

                    // set values
                    tvTitle.setText(booking.getCar().getBrand() +" "+ booking.getCar().getModel());
                    tvBookingId.setText(String.valueOf(booking.getBooking_id()));
                    tvCustomerId.setText(String.valueOf(booking.getUser().getId()));
                    tvBrand.setText(booking.getCar().getBrand());
                    tvModel.setText(booking.getCar().getModel());
                    tvYear.setText(booking.getCar().getYear());
                    tvColor.setText(booking.getCar().getColor());
                    tvPickUp.setText(booking.getPickup_date());
                    tvDropOff.setText(booking.getDropoff_date());
                    tvLicensePlate.setText(booking.getCar().getLicense_plate());
                    tvStatusCar.setText(booking.getCar().getStatus());
                    tvStatusBook.setText(booking.getStatus_booking());
                    //tvNote.setText(booking.getNotes());

                }
                else if (response.code() == 401) {
                    // unauthorized error. invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    // server return other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}