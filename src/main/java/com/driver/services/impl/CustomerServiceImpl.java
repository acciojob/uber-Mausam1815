package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> driverList = driverRepository2.findAll();
		Driver driver = null;

		for(Driver d : driverList) {
			if(d.getCab().isAvailable()) {
				if(driver == null || driver.getDriverId() > d.getDriverId()) {
					driver = d;
				}
			}
		}
		if(driver == null) {
			throw new Exception("No cab available!");
		}
		TripBooking tripBooking = new TripBooking();
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);

		tripBooking.setDriver(driver);
		driver.getTripBookings().add(tripBooking);
		driver.getCab().setAvailable(false);
		Customer customer = customerRepository2.findById(customerId).get();
		customer.getTripBookings().add(tripBooking);
		tripBooking.setCustomer(customer);

		driverRepository2.save(driver);
		customerRepository2.save(customer);

		tripBookingRepository2.save(tripBooking);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		Driver driver = tripBooking.getDriver();
		driver.getCab().setAvailable(true);
		driver.getTripBookings().remove(tripBooking);
		Customer customer = tripBooking.getCustomer();
		customer.getTripBookings().remove(tripBooking);
		driverRepository2.save(driver);
		customerRepository2.save(customer);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
		Driver driver = tripBooking.getDriver();
		int bill = tripBooking.getDistanceInKm() * driver.getCab().getPerKmRate();
		tripBooking.setBill(bill);
		driver.getCab().setAvailable(true);
		driverRepository2.save(driver);
		tripBookingRepository2.save(tripBooking);
	}
}
