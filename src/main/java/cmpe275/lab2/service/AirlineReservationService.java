package cmpe275.lab2.service;

import cmpe275.lab2.domain.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.*;

public class AirlineReservationService {
    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    FlightToPassengerRepository flightToPassengerRepository;

    @Autowired
    ReservationToFlightRepository reservationToFlightRepository;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    ReservationRepository reservationRepository;

    private class SortByDate implements Comparator<Flight> {

        @Override
        public int compare(Flight o1, Flight o2) {
            if(o1.getDepartureTime().before(o2.getDepartureTime())) {
                return -1;
            }
            return 1;
        }
    }

    public Passenger getPassenger(String passengerId) {
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
        if (!passenger.isPresent()) {
            String errMsg = "Sorry, the requested passenger with id " + passengerId + " does not exist";
            throw new PassengerNotExistException(errMsg);
        }
        return passenger.get();
    }

    @Transactional
    public void createPassenger(
            String firstName, String lastName,
            int age, String gender, String phone) {
       Passenger passenger = new Passenger();
       passenger.setFirstName(firstName);
       passenger.setLastName(lastName);
       passenger.setAge(age);
       passenger.setGender(gender);
       passenger.setPhone(phone);

       passengerRepository.save(passenger);
       return;
    }

    @Transactional
    public void updatePassenger(
            String passengerId, String firstName,
            String lastName,int age, String gender, String phone) {
        Passenger passenger = getPassenger(passengerId);
        passenger.setFirstName(firstName);
        passenger.setLastName(lastName);
        passenger.setAge(age);
        passenger.setGender(gender);
        passenger.setPhone(phone);

        passengerRepository.save(passenger);
    }

    @Transactional
    public void deletePassenger(String passengerId) {
        Passenger passenger = getPassenger(passengerId);
        Iterable<Reservation> reservations =
                reservationRepository.findAllByPassenger(passenger);

        // Delete reservations for the passenger
        reservations.forEach(reservation -> {
            deleteReservationInternal(reservation);
        });

        // Delete passenger in passenger table
        passengerRepository.delete(passenger);
    }

    public Reservation getReservation(String reservationNumber) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationNumber);
        if (!reservation.isPresent()) {
            String errMsg = "Reservation with number " + reservationNumber + " does not exist.";
            throw new ReservationNotExistException(errMsg);
        }

        Iterable<ReservationToFlight> reservationToFlights =
                reservationToFlightRepository.findAllByReservationId(reservationNumber);
        double totalPrice = 0;
        Iterator<ReservationToFlight> iterator = reservationToFlights.iterator();
        while (iterator.hasNext()) {
            totalPrice += iterator.next().getPrice();
        }
        reservation.get().setPrice(totalPrice);
        return reservation.get();
    }

    @Transactional
    public void createReservation(String passengerId, List<String> flightsNumbers) {
        // 1. Check flights existence
        List<Flight> flights = new LinkedList<>();
        for (String flightNumber : flightsNumbers) {
            try {
                flights.add(getFlight(flightNumber));
            } catch (FlightNotExistException ex) {
                throw new InvalidRequestException("The reserved flight does not exist");
            }
        }

        // 2. Check time conflict
        checkConflict(flights);

        // 3. Check left seats for each flight
        for (Flight flight : flights) {
            if (flight.getSeatsLeft() == 0) {
                throw new InvalidRequestException(
                        "No seats left for flight " + flight.getFlightNumber());
            }
            flight.setSeatsLeft(flight.getSeatsLeft() - 1);
        }

        Passenger passenger = getPassenger(passengerId);
        Reservation reservation = new Reservation();
        reservation.setPassenger(passenger);
        addFlightsToReservation(passengerId, reservation, flights);
    }

    private void checkConflict(List<Flight> flights) {
        Collections.sort(flights, new SortByDate());
        Flight pre = null;
        for (Flight cur : flights) {
            if (null == pre) {
                pre = cur;
                continue;
            }
            if (cur.getDepartureTime().before(pre.getArrivalTime())) {
                throw new InvalidRequestException(
                        "Time conflict between " + pre.getFlightNumber() + " and " + cur.getFlightNumber());
            }
        }
    }

    @Transactional
    void addFlightsToReservation(
            String passengerId, Reservation reservation, List<Flight> flights) {
        List<ReservationToFlight> reservationToFlights = new LinkedList<>();
        List<FlightToPassenger> flightToPassengers = new LinkedList<>();
        getOperatingEntities(
                passengerId, reservation, flights,
                reservationToFlights, flightToPassengers);
        flightRepository.saveAll(flights);
        reservationToFlightRepository.saveAll(reservationToFlights);
        flightToPassengerRepository.saveAll(flightToPassengers);
        reservationRepository.save(reservation);
    }

    private void getOperatingEntities(
            final String passengerId, final Reservation reservation,
            final List<Flight> flights, List<ReservationToFlight> reservationToFlights,
            List<FlightToPassenger> flightToPassengers) {
        for (Flight flight : flights) {
            ReservationToFlight reservationToFlight = new ReservationToFlight();
            reservationToFlight.setFlightNumber(flight.getFlightNumber());
            reservationToFlight.setReservationId(reservation.getReservationNumber());
            reservationToFlight.setPrice(flight.getPrice());
            reservationToFlights.add(reservationToFlight);
            FlightToPassenger flightToPassenger = new FlightToPassenger();
            flightToPassenger.setFlightNumber(flight.getFlightNumber());
            flightToPassenger.setPassengerId(passengerId);
            flightToPassengers.add(flightToPassenger);
        }
    }

    @Transactional
    void deleteFlightsFromReservation(
            String passengerId, Reservation reservation, List<Flight> flights) {
        List<ReservationToFlight> reservationToFlights = new LinkedList<>();
        List<FlightToPassenger> flightToPassengers = new LinkedList<>();
        getOperatingEntities(
                passengerId, reservation, flights,
                reservationToFlights, flightToPassengers);
        flightRepository.saveAll(flights);
        reservationToFlightRepository.deleteAll(reservationToFlights);
        flightToPassengerRepository.deleteAll(flightToPassengers);
        reservationRepository.delete(reservation);
    }

    @Transactional
    public void updateReservation(
            String reservationNumber, List<String> flightsAdded, List<String> flightsRemoved) {
        Reservation reservation = getReservation(reservationNumber);
        // 1. Check removed flights
        List<Flight> toRemove = new LinkedList<>();
        Iterator<Flight> iterator = reservation.getFlights().iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            if (flightsRemoved.indexOf(flight.getFlightNumber()) != -1) {
                toRemove.add(flight);
                iterator.remove();
            } else {
                throw new FlightNotExistException(
                        "Flight " + flight.getFlightNumber() + " does not exist in current reservation");
            }
        }

        // 2. Check added flight
        iterator = reservation.getFlights().iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            if (flightsAdded.indexOf(flight.getFlightNumber()) != -1) {
                throw new InvalidRequestException(
                        "Flight " + flight.getFlightNumber() + " already exists");
            }
        }
        List<Flight> toAdd = new LinkedList<>();
        for(String addFlight : flightsAdded) {
            toAdd.add(getFlight(addFlight));
        }

        // 3. Check time conflict
        checkConflict(toAdd);

        deleteFlightsFromReservation(
                reservation.getPassenger().getPassengerId(), reservation, toRemove);
        addFlightsToReservation(
                reservation.getPassenger().getPassengerId(), reservation, toAdd);
    }

    public Flight getFlight(String flightNumber) {
        Optional<Flight> flight = flightRepository.findById(flightNumber);
        if (!flight.isPresent()) {
            String errMsg = "Flight with number " + flightNumber + " does not exist.";
            throw new FlightNotExistException(errMsg);
        }
        return flight.get();
    }

    @Transactional
    public void updateFlight(
            String flightNumber, double price, String origin,
            String to, Date departureTime, Date arrivalTime,
            int capacity, String model, String manufacuturer, int year) {
        Flight flight = getFlight(flightNumber);
        int occupied = flight.getPlane().getCapacity() - flight.getSeatsLeft();
        if (capacity < occupied) {
            String errMsg = "Reservation number is larger than new capacity " + capacity;
            throw new InvalidRequestException(errMsg);
        }
        // TODO: Change arrivalTime or departureTime need to check passenger's reservations
        flight.setPrice(price);
        flight.setOrigin(origin);
        flight.setTo(to);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setSeatsLeft(capacity - occupied);
        flight.getPlane().setYear(year);
        flight.getPlane().setModel(model);
        flight.getPlane().setManufacturer(manufacuturer);
        updateFlightInternal(flight);
    }

    @Transactional
    public Flight createFlight(
            String flightNumber, double price, String origin,
            String to, Date departureTime, Date arrivalTime,
            int capacity, String model, String manufacuturer, int year) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setPrice(price);
        flight.setOrigin(origin);
        flight.setTo(to);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setSeatsLeft(capacity);
        flight.getPlane().setYear(year);
        flight.getPlane().setModel(model);
        flight.getPlane().setManufacturer(manufacuturer);

        flightRepository.save(flight);
        return flight;
    }

    @Transactional
    public void deleteFlight(String flightNumber) {
        Flight flight = getFlight(flightNumber);
        if (flight.getSeatsLeft() != 0) {
            throw new InvalidRequestException("Flight is reserved");
        }
        flightRepository.deleteById(flightNumber);
    }

    @Transactional
    public void updateFlightInternal(Flight flight) {
        flightRepository.save(flight);
    }

    @Transactional
    public void deleteReservation(String reservationNumber) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationNumber);
        if (!reservation.isPresent()) {
            String errMsg = "Reservation with number " + reservationNumber + " does not exist.";
            throw new ReservationNotExistException(errMsg);
        }

        deleteReservationInternal(reservation.get());
    }

    @Transactional
    void deleteReservationInternal(Reservation reservation) {
        Iterator<Flight> flightIterator = reservation.getFlights().iterator();
        while (flightIterator.hasNext()) {
            Flight flight = flightIterator.next();
            String flightNumber = flight.getFlightNumber();
            deletePassengerFromFlight(
                    flightNumber,
                    reservation.getPassenger().getPassengerId());
            // Delete Reservation to Flight mapping
            reservationToFlightRepository.
                    deleteAllByReservationIdAndFlightNumber(reservation.getReservationNumber(), flightNumber);
            flight.setSeatsLeft(flight.getSeatsLeft() + 1);
            flightRepository.save(flight);
        }
        reservationRepository.deleteById(reservation.getReservationNumber());
    }

    @Transactional
    void deletePassengerFromFlight(String flightNumber, String passengerId) {
        flightToPassengerRepository.
                deleteAllByFlightNumberAndPassengerId(flightNumber, passengerId);
    }
}
