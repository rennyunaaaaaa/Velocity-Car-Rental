package com.example.car_rental.remote;

import com.example.car_rental.model.Booking;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.DeleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingService {

    //booking car
    @FormUrlEncoded
    @POST("booking")
    Call<Booking> bookCar(@Header ("api-key") String apiKey, @Field("car_id") int carid,
                          @Field("user_id") int userid,@Field("pickup_date") String pickupdate,
                          @Field("dropoff_date") String dropoffdate,@Field("status_booking") String statusbooking,
                          @Field("note") String note);

    //list booking by user id
    @GET("booking")
    Call<List<Booking>> getAllBookingByUserId(@Header("api-key") String apiKey, @Query("user_id[e]") int userId);

    //retrieve all Booking
    @GET("booking")//retrieve
    Call<List<Booking>> getAllBooking(@Header("api-key") String api_key);

    //retrieve the detail booking by id
    @GET("booking/{booking_id}")
    Call<Booking> getBooking(@Header("api-key") String apiKey, @Path("booking_id") int bookingId);

    //update the booking detail
    @FormUrlEncoded
    @POST("booking/{booking_id}")
    Call<Booking> updateBooking(@Header("api-key") String apiKey, @Path("booking_id") int bookingId, @Field("user_id") int userId,
                              @Field("status_booking") String statusBooking,@Field("note") String note);

    //delete the car info
    @DELETE("booking/{booking_id}")
    Call<DeleteResponse> deleteBooking(@Header ("api-key") String apiKey, @Path("booking_id") int booking_id);


}
