package com.example.car_rental.remote;

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

public interface CarService {

    //retrieve car
    /*
    @GET("car")
    Call<List<Car>> getAllCars(@Header("api-key") String api_key);
    */

    //get car id from table car
    @GET("car/{car_id}")
    Call<Car> getCar(@Header("api-key") String api_key, @Path("car_id") int car_id);

    //retrieve car brand by ascending order
    @GET("car/?order=brand&orderType=asc")//retrieve
    Call<List<Car>> getAllCars(@Header("api-key") String api_key);

    //add new car info
    @FormUrlEncoded
    @POST("car")
    Call<Car> addCar(@Header ("api-key") String apiKey, @Field("brand") String brand,
                       @Field("model") String model, @Field("year") String year,
                       @Field("color") String color, @Field("license_plate") String license_plate,
                       @Field("status") String status);

    //delete the car info
    @DELETE("car/{car_id}")
    Call<DeleteResponse> deleteCar(@Header ("api-key") String apiKey, @Path("car_id") int car_id);

    //update the car info
    @FormUrlEncoded
    @POST("car/{car_id}")
    Call<Car> updateCar(@Header ("api-key") String apiKey,@Path("car_id") int car_id, @Field("brand") String brand,
                          @Field("model") String model, @Field("year") String year,
                          @Field("color") String color, @Field("license_plate") String license_plate,
                          @Field("status") String status);

}
