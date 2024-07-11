package com.example.car_rental;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_rental.adapter.CarAdapter;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.DeleteResponse;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.CarService;
import com.example.car_rental.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarListCustomerActivity extends AppCompatActivity {

    private CarService carService;
    private RecyclerView rvCarListCustomer;
    private CarAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_list_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView bookList
        rvCarListCustomer = findViewById(R.id.rvCarListCustomer);

        //register for context menu
        registerForContextMenu(rvCarListCustomer);

        // fetch and update book list
        updateListView();
    }

    private void updateListView(){
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        carService = ApiUtils.getCarService();

        // execute the call. send the user token when sending the query
        carService.getAllCars(token).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of book object from response
                    List<Car> books = response.body();

                    // initialize adapter
                    adapter = new CarAdapter(getApplicationContext(), books);

                    // set adapter to the RecyclerView
                    rvCarListCustomer.setAdapter(adapter);

                    // set layout to recycler view
                    rvCarListCustomer.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvCarListCustomer.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvCarListCustomer.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.carcustomer_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Car selectedCar = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedCar.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {    // user clicked details contextual menu
            // user clicked the update contextual menu
            doBookingCar(selectedCar);
        }

        return super.onContextItemSelected(item);
    }

    private void doBookingCar(Car selectedCar) {
        Log.d("MyApp:", "updating car: " + selectedCar.toString());
        // forward user to UpdateCarActivity, passing the selected car id
        Intent intent = new Intent(getApplicationContext(), BookingCarActivity.class);
        intent.putExtra("car_id", selectedCar.getCar_id());
        startActivity(intent);
    }

}