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

import com.example.car_rental.model.Car;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.CarService;
import com.example.car_rental.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetailsActivity extends AppCompatActivity {

    private CarService carService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve book details based on selected id

        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int carId = intent.getIntExtra("car_id", -1);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        carService = ApiUtils.getCarService();

        // execute the API query. send the token and book id
        carService.getCar(token, carId).enqueue(new Callback<Car>() {

            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get book object from response
                    Car car = response.body();

                    // get references to the view elements
                    TextView tvBrand = findViewById(R.id.tvBrand);
                    TextView tvModel = findViewById(R.id.tvModel);
                    TextView tvYear = findViewById(R.id.tvYear);
                    TextView tvColor = findViewById(R.id.tvColor);
                    TextView tvLicensePlate = findViewById(R.id.tvLicensePlate);
                    TextView tvStatus = findViewById(R.id.tvStatus);


                    // set values
                    tvBrand.setText(car.getBrand());
                    tvModel.setText(car.getModel());
                    tvYear.setText(car.getYear());
                    tvColor.setText(car.getColor());
                    tvLicensePlate.setText(car.getLicense_plate());
                    tvStatus.setText(car.getStatus());

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

        // terminate this activity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}