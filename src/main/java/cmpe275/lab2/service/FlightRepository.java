package cmpe275.lab2.service;

import cmpe275.lab2.domain.Flight;
import org.springframework.data.repository.CrudRepository;

public interface FlightRepository extends CrudRepository<Flight, String> {
}
