package com.example.car_rental;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.car_rental.model.Booking;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.BookingService;
import com.example.car_rental.remote.CarService;
import com.example.car_rental.sharedpref.SharedPrefManager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingCarActivity extends AppCompatActivity {


    // form fields
    private TextView tvBrand;
    private TextView tvModel;
    private TextView tvYear;
    private TextView tvColor;
    private TextView tvLicensePlate;
    private TextView tvStatus;
    private EditText txtRemark;

    private static TextView tvPickup; // static because need to be accessed by DatePickerFragment
    private static Date pickUp; // static because need to be accessed by DatePickerFragment
    private static TextView tvDropoff;// static because need to be accessed by DatePickerFragment
    private static Date dropOff;// static because need to be accessed by DatePickerFragment

    private Car car;  // current car to be updated
    private Booking booking;


    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */

    public static class DateTimePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
        private boolean isPickup;
        private int year, month, day;

        public DateTimePickerFragment(boolean isPickup) {
            this.isPickup = isPickup;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            if (isPickup) {
                c.setTime(pickUp);
            } else {
                c.setTime(dropOff);
            }
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;

            // Create a new instance of TimePickerDialog and show it
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            new TimePickerDialog(getActivity(), this, hour, minute, true).show();
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Create a date object from the selected date and time
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, hourOfDay, minute);

            if (isPickup) {
                pickUp = c.getTime();
                tvPickup.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).format(pickUp));
            } else {
                dropOff = c.getTime();
                tvDropoff.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).format(dropOff));
            }
        }
    }




    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v
     */
    public void showDateTimePickerDialog(View v) {
        boolean isPickup = v.getId() == R.id.btnPickup; // Check which button was clicked
        DialogFragment newFragment = new DateTimePickerFragment(isPickup);
        newFragment.show(getSupportFragmentManager(), "dateTimePicker");
    }




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve car id from intent
        // get car id sent by CarListActivity, -1 if not found
        Intent intent = getIntent();
        int car_id = intent.getIntExtra("car_id", -1);

        // get references to the form fields in layout
        tvBrand = findViewById(R.id.tvBrand);
        tvModel = findViewById(R.id.tvModel);
        tvYear = findViewById(R.id.tvYear);
        tvColor = findViewById(R.id.tvColor);
        tvLicensePlate = findViewById(R.id.tvLicensePlate);
        tvStatus = findViewById(R.id.tvStatus);
        tvPickup = findViewById(R.id.tvPickup);
        tvDropoff = findViewById(R.id.tvDropoff);
        txtRemark = findViewById(R.id.txtRemark);


        // initialize createdAt to today's date
        pickUp = new Date();
        dropOff = new Date();
        // display in the label beside the button with specific date format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK);
        tvPickup.setText( sdf.format(pickUp) );
        tvDropoff.setText(sdf.format(dropOff));

        // retrieve car info from database using the car id
        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // get car service instance
        CarService carService = ApiUtils.getCarService();

        // execute the API query. send the token and car id
        carService.getCar(user.getToken(), car_id).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                // for debug purpose
                Log.d("MyApp:", "Update Form Populate Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success
                    // get car object from response
                    car = response.body();

                    // set values into forms
                    tvBrand.setText(car.getBrand());
                    tvModel.setText(car.getModel());
                    tvYear.setText(car.getYear());
                    tvColor.setText(car.getColor());
                    tvLicensePlate.setText(car.getLicense_plate());
                    tvStatus.setText(car.getStatus());

                    //tvCreated.setText(car.getCreatedAt());

                    // parse created_at date to date object
                    /*
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        // parse created date string to date object
                        createdAt = sdf.parse(car.getCreatedAt());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    */

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
            public void onFailure(Call<Car> call, Throwable t) {
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

    /**
     * Update Car info in database when the user click Update Car button
     * @param view
     */
    public void bookCar(View view) {
        // get values in form
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();


        int carId = car.getCar_id();
        int userId = user.getId();
        String status = "New";
        String note = txtRemark.getText().toString();


        // convert createdAt date to format in DB
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        String pDate = sdf.format(pickUp);
        String dDate = sdf.format(dropOff);

        // send request to add new borrow record to the REST API
        BookingService bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.bookCar(user.getToken(), carId, userId, pDate, dDate,status,note);

        // execute
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // borrow record added successfully
                    Booking bookedCar = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            bookedCar.getCar().getBrand()+" "+bookedCar.getCar().getModel() + " successfully borrowed.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), CarListCustomerActivity.class);
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
