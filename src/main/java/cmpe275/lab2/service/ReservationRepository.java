package cmpe275.lab2.service;

import cmpe275.lab2.domain.Passenger;
import cmpe275.lab2.domain.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation, String> {
    Iterable<Reservation> findAllByPassenger(Passenger passenger);
    void deleteAllByPassenger(Passenger passenger);
}
