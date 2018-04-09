package cmpe275.lab2.service;

import cmpe275.lab2.domain.Flight;

public class FlightNotExistException extends RuntimeException {
    private String msg;

    FlightNotExistException(String msg) {
        this.msg = msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
