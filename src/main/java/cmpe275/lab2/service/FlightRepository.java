package cmpe275.lab2.service;

import cmpe275.lab2.domain.Flight;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface FlightRepository
        extends CrudRepository<Flight, String>, JpaSpecificationExecutor<Flight> {
    Iterable<Flight> findAllByOriginAndTo(String origin, String to);
    Iterable<Flight> findAllByOrigin(String origin);
    Iterable<Flight> findAllByTo(String to);
}
