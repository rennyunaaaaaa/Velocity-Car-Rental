package com.example.car_rental.model;

public class Car {
    private int car_id;
    private String brand;
    private String model;
    private String year;
    private String color;
    private String license_plate;
    private String status;

    public Car() {
    }
    public Car(int car_id, String brand, String model, String year, String color, String license_plate, String status) {
        this.car_id = car_id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.license_plate = license_plate;
        this.status = status;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Car{" +
                "car_id=" + car_id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                ", color='" + color + '\'' +
                ", license_plate='" + license_plate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
