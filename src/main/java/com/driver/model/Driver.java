package com.driver.model;

import com.driver.repository.DriverRepository;
import sun.net.util.IPAddressUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int driverId;
    private String mobile;
    private String password;
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    Cab cab;
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<TripBooking> tripBookings = new ArrayList<>();

    public Driver() {

    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Cab getCab() {
        return cab;
    }

    public void setCab(Cab cab) {
        this.cab = cab;
    }

    public List<TripBooking> getTripBookings() {
        return tripBookings;
    }

    public void setTripBookings(List<TripBooking> tripBookings) {
        this.tripBookings = tripBookings;
    }
}