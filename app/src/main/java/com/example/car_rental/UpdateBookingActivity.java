package com.example.car_rental;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.car_rental.BookingListActivity;
import com.example.car_rental.LoginActivity;
import com.example.car_rental.R;
import com.example.car_rental.model.Booking;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.BookingService;
import com.example.car_rental.sharedpref.SharedPrefManager;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBookingActivity extends AppCompatActivity {

    private BookingService bookingService;
    private Booking booking;
    private static TextView tvBorrowDate; // static because need to be accessed by DatePickerFragment
    private static Date borrowAtDate; // static because need to be accessed by DatePickerFragment
    private static TextView tvReturnDate; // static because need to be accessed by DatePickerFragment
    private static Date returnDate; // static because need to be accessed by DatePickerFragment
    private Spinner spStatusBook;
    private TextView tvBookingId;
    private TextView tvCustomerId;
    private EditText txtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spStatusBook = findViewById(R.id.spStatusBook);

        // Set up the Spinner with the array of booking statuses
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.booking_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatusBook.setAdapter(adapter);
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
                    tvBookingId = findViewById(R.id.tvBookingid);
                    tvCustomerId = findViewById(R.id.tvCustid);
                    TextView tvBrand = findViewById(R.id.tvBrand);
                    TextView tvModel = findViewById(R.id.tvModel);
                    TextView tvYear = findViewById(R.id.tvYear);
                    TextView tvColor = findViewById(R.id.tvColor);
                    TextView tvPickUp = findViewById(R.id.tvPickup);
                    TextView tvDropOff = findViewById(R.id.tvDropoff);
                    TextView tvLicensePlate = findViewById(R.id.tvLicensePlate);
                    TextView tvStatusCar = findViewById(R.id.tvStatuscar);
                    //TextView tvStatusBook = findViewById(R.id.tvStatusbook);
                    txtNote = findViewById(R.id.txtNote);

                    // set values
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
                    //tvStatusBook.setText(booking.getStatus_booking());
                    txtNote.setText(booking.getNotes());

                    // Set the selected item of the spinner based on the booking status
                    String statusBooking = booking.getStatus_booking();
                    if (statusBooking != null) {
                        int spinnerPosition = adapter.getPosition(statusBooking);
                        spStatusBook.setSelection(spinnerPosition);
                    }

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


    public void updateBooking(View v) {
        // get values in form
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        String statusBooking = spStatusBook.getSelectedItem().toString();
        int bookingId = Integer.parseInt(String.valueOf(tvBookingId.getText()));
        String note = txtNote.getText().toString();

        // send request to add new borrow record to the REST API
        BookingService bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.updateBooking(user.getToken(), bookingId,
                            user.getId(), statusBooking,note);

        // execute
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // borrow record updated successfully
                    Booking updatedBooking = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            updatedBooking.getCar().getModel() + " successfully update booking record.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), BookingListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
}