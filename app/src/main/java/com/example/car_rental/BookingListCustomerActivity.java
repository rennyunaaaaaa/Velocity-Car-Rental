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

import com.example.car_rental.LoginActivity;
import com.example.car_rental.R;
import com.example.car_rental.adapter.BookingAdapter;
import com.example.car_rental.model.Booking;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.DeleteResponse;
import com.example.car_rental.model.User;
import com.example.car_rental.remote.ApiUtils;
import com.example.car_rental.remote.BookingService;
import com.example.car_rental.remote.CarService;
import com.example.car_rental.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingListCustomerActivity extends AppCompatActivity {

    private BookingService bookingService;
    private RecyclerView rvBookingListCustomer;
    private BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_list_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView rvBorrowedList
        rvBookingListCustomer = findViewById(R.id.rvBookingListCustomer);

        //register for context menu
        registerForContextMenu(rvBookingListCustomer);

        // fetch and update borrowed list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        bookingService = ApiUtils.getBookingService();

        // execute the call. send the user token when sending the query
        bookingService.getAllBooking(token).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of borrow objects from response
                    List<Booking> bookings = response.body();

                    // initialize adapter
                    adapter = new BookingAdapter(getApplicationContext(), bookings);

                    // set adapter to the RecyclerView
                    rvBookingListCustomer.setAdapter(adapter);

                    // set layout to recycler view
                    rvBookingListCustomer.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBookingListCustomer.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvBookingListCustomer.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookingcustomer_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Booking selectedBooking = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedBooking.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {
            // user clicked details contextual menu
            doViewDetails(selectedBooking);
        }
        else if (item.getItemId() == R.id.menu_delete) {
            // user clicked the update contextual menu
            doCancelBooking(selectedBooking);
        }

        return super.onContextItemSelected(item);
    }
    private void doViewDetails(Booking selectedBooking) {
        Log.d("MyApp:", "viewing details: " + selectedBooking.toString());
        // forward user to BorrowDetailsActivity, passing the selected borrow id
        Intent intent = new Intent(getApplicationContext(), BookingDetailsActivity.class);
        intent.putExtra("booking_id", selectedBooking.getBooking_id());
        startActivity(intent);

    }

    /**
     * Delete book record. Called by contextual menu "Delete"
     * @param selectedBooking - book selected by user
     */
    private void doCancelBooking(Booking selectedBooking) {

        if (!"New".equals(selectedBooking.getStatus_booking())) {
            // Display a message if the booking cannot be deleted
            Toast.makeText(getApplicationContext(), "Only 'New' bookings can be cancel.", Toast.LENGTH_LONG).show();
            return;
        }

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // prepare REST API call
        BookingService bookingService = ApiUtils.getBookingService();
        Call<DeleteResponse> call = bookingService.deleteBooking(user.getToken(), selectedBooking.getBooking_id());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Cancelation Done");
                    // update data in list view
                    updateRecyclerView();
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
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }
}