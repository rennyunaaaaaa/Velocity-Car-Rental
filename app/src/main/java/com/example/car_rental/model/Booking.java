package com.example.car_rental.model;

public class Booking {
    private int booking_id;
    private int car_id;
    private Car car;
    private int id;
    private User user;
    private String booking_date;
    private String pickup_date;
    private String dropoff_date;
    private String status_booking;
    private String notes;
    private String progress;
    private String admin_id;

    @Override
    public String toString() {
        return "Booking{" +
                "booking_id=" + booking_id +
                ", car_id=" + car_id +
                ", car=" + car +
                ", id=" + id +
                ", user=" + user +
                ", booking_date='" + booking_date + '\'' +
                ", pickup_date='" + pickup_date + '\'' +
                ", dropoff_date='" + dropoff_date + '\'' +
                ", status_booking='" + status_booking + '\'' +
                ", notes='" + notes + '\'' +
                ", progress='" + progress + '\'' +
                ", admin_id='" + admin_id + '\'' +
                '}';
    }

    public Booking(int booking_id, int car_id, Car car, int id, User user, String booking_date, String pickup_date, String dropoff_date, String status_booking, String notes, String progress, String admin_id) {
        this.booking_id = booking_id;
        this.car_id = car_id;
        this.car = car;
        this.id = id;
        this.user = user;
        this.booking_date = booking_date;
        this.pickup_date = pickup_date;
        this.dropoff_date = dropoff_date;
        this.status_booking = status_booking;
        this.notes = notes;
        this.progress = progress;
        this.admin_id = admin_id;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getDropoff_date() {
        return dropoff_date;
    }

    public void setDropoff_date(String dropoff_date) {
        this.dropoff_date = dropoff_date;
    }

    public String getStatus_booking() {
        return status_booking;
    }

    public void setStatus_booking(String status_booking) {
        this.status_booking = status_booking;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

}
