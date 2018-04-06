package cmpe275.lab2.service;

import cmpe275.lab2.domain.Passenger;
import org.springframework.data.repository.CrudRepository;

public interface PassengerRepository extends CrudRepository<Passenger, String> {
    Passenger findByPassengerId(String passengerId);
}
