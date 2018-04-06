package cmpe275.lab2.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Plane {
    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "year", nullable = false)
    private int year;

    public int getCapacity() {
        return capacity;
    }

    public int getYear() {
        return year;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
