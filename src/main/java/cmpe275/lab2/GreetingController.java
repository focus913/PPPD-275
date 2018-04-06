package cmpe275.lab2;

import java.sql.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import cmpe275.lab2.domain.*;
import cmpe275.lab2.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private ReservationToFlightRepository reservationToFlightRepository;

    @Autowired
    private FlightToPassengerRepository flightToPassengerRepository;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @GetMapping(path = "/add")
    public @ResponseBody String addPassenger(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam int age,
            @RequestParam String gender,
            @RequestParam String phone) {
        Passenger passenger = new Passenger();
        passenger.setFirstName(firstname);
        passenger.setLastName(lastname);
        passenger.setAge(age);
        passenger.setGender(gender);
        passenger.setPhone(phone);
        passengerRepository.save(passenger);
        return "Saved";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    @GetMapping(path = "/reservation/add")
    public @ResponseBody String addReservation(
            @RequestParam String passengerId) {
        Reservation reservation = new Reservation();
        Passenger passenger = passengerRepository.findByPassengerId(passengerId);
        reservation.setPassenger(passenger);
        reservationRepository.save(reservation);
        for (int i = 0; i < 2; ++i) {
            ReservationToFlight reservationToFlight = new ReservationToFlight();
            reservationToFlight.setFlightNumber(String.valueOf(i));
            reservationToFlight.setReservationId(reservation.getReservationId());
            reservationToFlightRepository.save(reservationToFlight);
            FlightToPassenger flightToPassenger = new FlightToPassenger();
            flightToPassenger.setFlightNumber(String.valueOf(i));
            flightToPassenger.setPassengerId(passengerId);
            flightToPassengerRepository.save(flightToPassenger);
        }
        return "Saved";
    }

    @GetMapping(path = "/reservation/get")
    public @ResponseBody
    Optional<Reservation> getReservation(@RequestParam String reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @GetMapping(path = "/flight/add")
    public @ResponseBody String addFlight(
            @RequestParam String flightNumber,
            @RequestParam double price,
            @RequestParam String origin,
            @RequestParam String to,
            //@RequestParam Date departuretime,
            //@RequestParam Date arrivalTime,
            @RequestParam String description,
            @RequestParam int capacity,
            @RequestParam String model,
            @RequestParam String manufacturer,
            @RequestParam int year) {

        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setArrivalTime(Date.valueOf("1990-09-01"));
        flight.setDepartureTime(Date.valueOf("1990-09-01"));
        flight.setDescription(description);
        flight.setOrigin(origin);
        flight.setTo(to);
        flight.setPrice(price);
        Plane plane = new Plane();
        plane.setCapacity(capacity);
        plane.setManufacturer(manufacturer);
        plane.setModel(model);
        plane.setYear(year);
        flight.setPlane(plane);
        flightRepository.save(flight);
        return "Saved";
    }

    @GetMapping(path = "/flight/get")
    public @ResponseBody Optional<Flight> getFlight(@RequestParam String flightNumber) {
        return flightRepository.findById(flightNumber);
    }
}
