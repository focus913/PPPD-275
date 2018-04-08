package cmpe275.lab2.service;

import cmpe275.lab2.domain.Passenger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AirlineReservationService {
    @Autowired
    PassengerRepository passengerRepository;

    /*Passenger getPassenger(String passengerId) {
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
    }*/
}
