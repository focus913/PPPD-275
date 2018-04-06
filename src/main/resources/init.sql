CREATE DATABASE lab2;
CREATE TABLE passenger (
  passenger_id VARCHAR (256) PRIMARY KEY,
  first_name VARCHAR (256),
  last_name VARCHAR (256),
  age INT,
  gender VARCHAR (64),
  phone VARCHAR (64)
);

CREATE TABLE reservation (
  reservation_id VARCHAR (256),
  passenger_id VARCHAR (256),
);

CREATE TABLE reservation_to_flight (
  reservation_id VARCHAR (256),
  flight_number VARCHAR (256)
);

CREATE TABLE flight (
  flight_number VARCHAR (256),
  price DOUBLE,
  origin VARCHAR (256),
  to VARCHAR (256),
  departure_time DATE,
  arrival_time DATE,
  seats_left INT,
  description VARCHAR (256),
);

CREATE TABLE flight_to_passenger (
  flight_number VARCHAR (256),
  passenger_id VARCHAR (256)
);

CREATE TABLE plane (
  capacity INT,
  model VARCHAR (256),
  manufacturer VARCHAR (256),
  year INT
);
