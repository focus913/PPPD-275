package cmpe275.lab2.service;

import cmpe275.lab2.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.*;

@Component("AirlineReservationService")
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
    public String createPassenger(
            String firstName, String lastName,
            int age, String gender, String phone) {
       Passenger passenger = new Passenger();
       passenger.setFirstName(firstName);
       passenger.setLastName(lastName);
       passenger.setAge(age);
       passenger.setGender(gender);
       passenger.setPhone(phone);

       passengerRepository.save(passenger);
       return passenger.getPassengerId();
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
    public String createReservation(String passengerId, List<String> flightsNumbers) {
        // 1. Check flights existence
        List<Flight> flights = new LinkedList<>();
        for (String flightNumber : flightsNumbers) {
            try {
                flights.add(getFlight(flightNumber));
            } catch (FlightNotExistException ex) {
                throw new InvalidRequestException("The reserved flight does not exist");
            }
        }

        // 2. Check time conflict for new flights
        checkConflict(flights);

        // 3. Check time conflict for existed flights
        Passenger passenger = getPassenger(passengerId);
        Iterable<Reservation> reservations = reservationRepository.findAllByPassenger(passenger);
        Iterator<Reservation> iterator = reservations.iterator();
        List<Flight> existed = new LinkedList<>();
        while (iterator.hasNext()) {
            Reservation reservation = iterator.next();
            existed.addAll(reservation.getFlights());
        }
        existed.addAll(flights);
        checkConflict(existed);

        // 3. Check left seats for each flight
        for (Flight flight : flights) {
            if (flight.getSeatsLeft() == 0) {
                throw new InvalidRequestException(
                        "No seats left for flight " + flight.getFlightNumber());
            }
            flight.setSeatsLeft(flight.getSeatsLeft() - 1);
        }

        Reservation reservation = new Reservation();
        reservation.setPassenger(passenger);
        addFlightsToReservation(passengerId, reservation, flights);
        return reservation.getReservationNumber();
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
            pre = cur;
        }
    }

    void addFlightsToReservation(
            String passengerId, Reservation reservation, List<Flight> flights) {
        if (null == flights || flights.isEmpty()) {
            return;
        }
        List<ReservationToFlight> reservationToFlights = new LinkedList<>();
        List<FlightToPassenger> flightToPassengers = new LinkedList<>();
        getOperatingEntities(
                passengerId, reservation, flights,
                reservationToFlights, flightToPassengers);
        //flightRepository.saveAll(flights);
        reservationRepository.save(reservation);
        reservationToFlightRepository.saveAll(reservationToFlights);
        flightToPassengerRepository.saveAll(flightToPassengers);
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
        if (null == flights || flights.isEmpty()) {
            return;
        }
        List<ReservationToFlight> reservationToFlights = new LinkedList<>();
        List<FlightToPassenger> flightToPassengers = new LinkedList<>();
        getOperatingEntities(
                passengerId, reservation, flights,
                reservationToFlights, flightToPassengers);

        //flightRepository.saveAll(flights);
        for (ReservationToFlight rf : reservationToFlights) {
            reservationToFlightRepository.
                    deleteByFlightNumberAndReservationId(rf.getFlightNumber(), rf.getReservationId());
        }
        for (FlightToPassenger fp : flightToPassengers) {
            flightToPassengerRepository.
                    deleteByFlightNumberAndPassengerId(fp.getFlightNumber(), fp.getPassengerId());
        }
    }

    @Transactional
    public void updateReservation(
            String reservationNumber, List<String> flightsAdded, List<String> flightsRemoved) {
        Reservation reservation = getReservation(reservationNumber);
        // 1. Check removed flights
        List<Flight> toRemove = new LinkedList<>();
        List<String> existedFlightNumber = new LinkedList<>();
        Iterator<Flight> iterator = reservation.getFlights().iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            existedFlightNumber.add(flight.getFlightNumber());
        }
        if (null != flightsRemoved) {
            for (String needRemove : flightsRemoved) {
                if (existedFlightNumber.indexOf(needRemove) == -1) {
                    throw new FlightNotExistException(
                            "Flight " + needRemove + " does not exist in current reservation");
                } else {
                    toRemove.add(getFlight(needRemove));
                }
            }
        }

        // 2. Check added flight
        iterator = reservation.getFlights().iterator();
        while (iterator.hasNext() && null != flightsAdded) {
            Flight flight = iterator.next();
            if (flightsAdded.indexOf(flight.getFlightNumber()) != -1) {
                throw new InvalidRequestException(
                        "Flight " + flight.getFlightNumber() + " already exists");
            }
        }
        List<Flight> toAdd = new LinkedList<>();
        List<Flight> allFlights = new LinkedList<>();
        for (Flight flight : reservation.getFlights()) {
            if (null == flightsRemoved ||
                    flightsRemoved.indexOf(flight.getFlightNumber()) == -1) {
                allFlights.add(flight);
            }
        }
        if (null != flightsAdded) {
            for(String addFlight : flightsAdded) {
                toAdd.add(getFlight(addFlight));
            }
        }
        allFlights.addAll(toAdd);

        // 3. Check time conflict
        checkConflict(allFlights);

        deleteFlightsFromReservation(
                reservation.getPassenger().getPassengerId(), reservation, toRemove);
        addFlightsToReservation(
                reservation.getPassenger().getPassengerId(), reservation, toAdd);
    }

    @Transactional
    public Iterable<Reservation> searchReservation(
            String passengerId, String origin, String to, String flightNumber) {
        if (null != passengerId && !passengerId.isEmpty()) {
            Passenger passenger = getPassenger(passengerId);
            Iterable<Reservation> reservations =
                    reservationRepository.findAllByPassenger(passenger);
            System.out.println("Step1");
            return filtReservation(reservations, null, origin, to, flightNumber);
        } else if (null != flightNumber && !flightNumber.isEmpty()) {
            Iterable<ReservationToFlight> reservationToFlights =
                    reservationToFlightRepository.findAllByFlightNumber(flightNumber);
            Set<String> reservationNums = new HashSet<>();
            Iterator<ReservationToFlight> iterator = reservationToFlights.iterator();
            while (iterator.hasNext()) {
                reservationNums.add(iterator.next().getReservationId());
            }
            Iterable<Reservation> reservations =
                    reservationRepository.findAllByReservationNumberIn(reservationNums);
            return filtReservation(reservations, passengerId, origin, to, null);
        } else {
            Iterable<Flight> flights = null;
            if (null == origin && null != to) {
                flights = flightRepository.findAllByTo(to);
            } else if (null != origin && null == to) {
                flights = flightRepository.findAllByOrigin(origin);
            } else if (null != origin && null != to) {
                flights = flightRepository.findAllByOriginAndTo(origin, to);
            } else {
                throw new InvalidRequestException("Invalid parameters");
            }
            return filtReservation(flights);
        }
    }

    Iterable<Reservation> filtReservation(
            Iterable<Reservation> reservations, String passengerId, String origin, String to, String flightNumber) {
        List<Reservation> ret = new LinkedList<>();
        for (Reservation reservation : reservations) {
            System.out.println(reservation.getReservationNumber());
            boolean hasPassengerId = (null == passengerId ||
                            reservation.getPassenger().getPassengerId().equals(passengerId));

            List<Flight> flights = reservation.getFlights();
            Set<String> origins = new HashSet<>();
            Set<String> tos = new HashSet<>();
            Set<String> flightNums = new HashSet<>();
            for (Flight flight : flights) {
                System.out.println(flight.getOrigin());
                System.out.println(flight.getTo());
                System.out.println(flight.getFlightNumber());
                origins.add(flight.getOrigin());
                tos.add(flight.getTo());
                flightNums.add(flight.getFlightNumber());
            }
            boolean hasOrigin = (null == origin) || origins.contains(origin);
            boolean hasTo = (null == to) || tos.contains(to);
            boolean hasFlightNum = (null == flightNumber) || flightNums.contains(flightNumber);
            if ((null != passengerId && !hasPassengerId)
             || (null != origin && !hasOrigin)
             || (null != to && !hasTo)
             || (null != flightNumber && !hasFlightNum)) {
                continue;
            }
            ret.add(reservation);
        }
        return ret;
    }

    Iterable<Reservation> filtReservation(Iterable<Flight> flights) {
        Set<String> reservationNums = new HashSet<>();
        Iterator<Flight> iterator = flights.iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            Iterable<ReservationToFlight> reservationToFlights =
                    reservationToFlightRepository.findAllByFlightNumber(flight.getFlightNumber());
            Iterator<ReservationToFlight> iterator1 = reservationToFlights.iterator();
            while (iterator1.hasNext()) {
                reservationNums.add(iterator1.next().getReservationId());
            }
        }
        Iterable<Reservation> reservations =
                reservationRepository.findAllByReservationNumberIn(reservationNums);
        return filtReservation(reservations, null, null, null, null);
    }

    @Transactional
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
            String to, Date departureTime, Date arrivalTime, String description,
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
        flight.getPlane().setCapacity(capacity);
        flight.setDescription(description);
        updateFlightInternal(flight);
    }

    @Transactional
    public Flight createFlight(
            String flightNumber, double price, String origin,
            String to, Date departureTime, Date arrivalTime, String description,
            int capacity, String model, String manufacturer, int year) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setPrice(price);
        flight.setOrigin(origin);
        flight.setTo(to);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setSeatsLeft(capacity);
        Plane plane = new Plane();
        plane.setManufacturer(manufacturer);
        plane.setModel(model);
        plane.setYear(year);
        plane.setCapacity(capacity);
        flight.setPlane(plane);
        flight.setDescription(description);

        flightRepository.save(flight);
        return flight;
    }

    @Transactional
    public void deleteFlight(String flightNumber) {
        Flight flight = getFlight(flightNumber);
        if (flight.getSeatsLeft() != flight.getPlane().getCapacity()) {
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
            //reservationToFlightRepository.
             //       deleteByReservationIdAndFlightNumber(reservation.getReservationNumber(), flightNumber);
            flight.setSeatsLeft(flight.getSeatsLeft() + 1);
            flightRepository.save(flight);
        }
        reservationRepository.deleteById(reservation.getReservationNumber());
    }

    @Transactional
    void deletePassengerFromFlight(String flightNumber, String passengerId) {
        flightToPassengerRepository.
                deleteByFlightNumberAndPassengerId(flightNumber, passengerId);
    }
}
