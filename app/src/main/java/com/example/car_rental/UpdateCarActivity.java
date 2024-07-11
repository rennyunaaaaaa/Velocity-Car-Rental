package com.example.car_rental;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.car_rental.model.Car;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.CarService;
import com.example.car_rental.sharedpref.SharedPrefManager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCarActivity extends AppCompatActivity {

    // form fields
    private EditText txtBrand;
    private EditText txtModel;
    private EditText txtYear;
    private EditText txtColor;
    private EditText txtLicensePlate;
    private EditText txtStatus;
    //private static EditText tvPickup; // static because need to be accessed by DatePickerFragment
    //private static Date pickUp; // static because need to be accessed by DatePickerFragment

    private Car car;  // current car to be updated

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    /*
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            c.setTime(pickUp); // Use the current book created date as the default date in the picker
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            // create a date object from selected year, month and day
            pickUp = new GregorianCalendar(year, month, day).getTime();

            // display in the label beside the button with specific date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            tvPickup.setText( sdf.format(pickUp) );
        }
    }

     */


    /*
    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v
     */

    /*
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve car id from intent
        // get car id sent by CarListActivity, -1 if not found
        Intent intent = getIntent();
        int car_id = intent.getIntExtra("car_id", -1);

        // initialize createdAt to today's date
        //pickUp = new Date();

        // get references to the form fields in layout
        txtBrand = findViewById(R.id.txtBrand);
        txtModel = findViewById(R.id.txtModel);
        txtYear = findViewById(R.id.txtYear);
        txtColor = findViewById(R.id.txtColor);
        txtLicensePlate = findViewById(R.id.txtLicensePlate);
        txtStatus = findViewById(R.id.txtStatus);
        //tvPickup = findViewById(R.id.tvPickup);


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
                    txtBrand.setText(car.getBrand());
                    txtModel.setText(car.getModel());
                    txtYear.setText(car.getYear());
                    txtColor.setText(car.getColor());
                    txtLicensePlate.setText(car.getLicense_plate());
                    txtStatus.setText(car.getStatus());
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
    public void updateCar(View view) {
        // get values in form

        String brand = txtBrand.getText().toString();
        String model = txtModel.getText().toString();
        String year = txtYear.getText().toString();
        String color = txtColor.getText().toString();
        String licenseplate = txtLicensePlate.getText().toString();
        String status = txtStatus.getText().toString();


        // convert createdAt date to format required by Database - yyyy-MM-dd HH:mm:ss
        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String created_at = sdf.format(createdAt);

        // set updated_at to current date and time
        String updated_at = sdf.format(new Date());
        */

        Log.d("MyApp:", "Old Car info: " + car.toString());

        // update the car object retrieved in when populating the form with the new data.
        // update all fields excluding the id

        car.setBrand(brand);
        car.setModel(model);
        car.setYear(year);
        car.setColor(color);
        car.setLicense_plate(licenseplate);
        car.setStatus(status);
        //car.setCreatedAt(created_at);
        //car.setUpdatedAt(updated_at);

        Log.d("MyApp:", "New Car info: " + car.toString());

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // send request to update the book record to the REST API
        CarService carService = ApiUtils.getCarService();
        /*Call<Car> call = carService.updateCar(user.getToken(), book.getId(), book.getIsbn(),
                book.getName(), book.getYear(), book.getAuthor(), book.getDescription(),
                book.getImage(), book.getCreatedAt(), book.getUpdatedAt());*/

        Call<Car> call = carService.updateCar(user.getToken(),car.getCar_id(),car.getBrand(), car.getModel(),
                car.getYear(), car.getColor(), car.getLicense_plate(), car.getStatus());

        // execute
        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {

                // for debug purpose
                Log.d("MyApp:", "Update Request Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success code for update request
                    // get updated car object from response
                    Car updatedCar = response.body();

                    // display message
                    displayUpdateSuccess(updatedCar.getBrand() +" "+ updatedCar.getModel()+ " updated successfully.");


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
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // end this activity and forward user to BookListActivity
                        Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
                        startActivity(intent);
                        finish();

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}