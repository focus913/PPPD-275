package cmpe275.lab2.service;

public class PassengerNotExistException extends RuntimeException {
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PassengerNotExistException(String msg) {
        this.msg = msg;
    }
}
