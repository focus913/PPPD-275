package cmpe275.lab2.service;

import cmpe275.lab2.domain.FlightToPassenger;
import org.springframework.data.repository.CrudRepository;

public interface FlightToPassengerRepository extends CrudRepository<FlightToPassenger, Long> {
    void deleteByFlightNumberAndPassengerId(String flightNumber, String passengerId);
}
