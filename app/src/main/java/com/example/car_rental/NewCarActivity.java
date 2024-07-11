package com.example.car_rental;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.car_rental.model.Car;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.CarService;
import com.example.car_rental.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCarActivity extends AppCompatActivity {

    private EditText txtBrand;
    private EditText txtModel;
    private EditText txtYear;
    private EditText txtColor;
    private EditText txtLicensePlate;
    private EditText txtStatus;
    //private static TextView tvCreated; // static because need to be accessed by DatePickerFragment
   // private static Date createdAt; // static because need to be accessed by DatePickerFragment

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    /*
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            // create a date object from selected year, month and day
            createdAt = new GregorianCalendar(year, month, day).getTime();

            // display in the label beside the button with specific date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            tvCreated.setText( sdf.format(createdAt) );
        }
    }*/

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get view objects references
        txtBrand = findViewById(R.id.txtBrand);
        txtModel = findViewById(R.id.txtModel);
        txtYear = findViewById(R.id.txtYear);
        txtColor = findViewById(R.id.txtColor);
        txtLicensePlate = findViewById(R.id.txtLicensePlate);
        txtStatus = findViewById(R.id.txtStatus);
        //tvCreated = findViewById(R.id.tvCreated);

        /*
        // set default createdAt value to current date
        createdAt = new Date();
        // display in the label beside the button with specific date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        tvCreated.setText( sdf.format(createdAt) );
         */
    }

    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v

    /*
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    */

    /**
     * Called when Add Book button is clicked
     * @param v
     */
    public void addNewCar(View v) {
        // get values in form
        String brand = txtBrand.getText().toString();
        String model = txtModel.getText().toString();
        String year = txtYear.getText().toString();
        String color = txtColor.getText().toString();
        String licenseplate = txtLicensePlate.getText().toString();
        String status = txtStatus.getText().toString();

        // convert createdAt date to format in DB
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String created_at = sdf.format(createdAt);

        // set updated_at with the same value as created_at
        String updated_at = created_at;
        */

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // send request to add new book to the REST API
        CarService carService = ApiUtils.getCarService();
        /*
        Call<Car> call = carService.addCar(user.getToken(), brand, model, year, color, licenseplate,status,
                "default.jpg", created_at, updated_at);
         */
        Call<Car> call = carService.addCar(user.getToken(), brand, model, year, color,
                licenseplate,status);

        // execute
        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // book added successfully
                    Car addedCar = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            addedCar.getBrand() +" "+ addedCar.getModel() +" added successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
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
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
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