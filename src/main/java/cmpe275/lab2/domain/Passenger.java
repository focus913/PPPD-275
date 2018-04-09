package cmpe275.lab2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Embeddable
@Table(name = "passenger")
@JacksonXmlRootElement(localName = "passenger")
public class Passenger {
    @Id
    @Column(name = "passenger_id")
    private String passengerId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone; // Phone numbers must be unique

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "passengers")
    private List<Flight> flights = new LinkedList<>();

    // private1
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "passenger")
    @JacksonXmlElementWrapper(localName = "reservations")
    @JacksonXmlProperty(localName = "reservation")
    private List<Reservation> reservations = new LinkedList<>();

    public Passenger() {
        this.passengerId = "passenger" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void setPassengerId(String id) {
        this.passengerId = id;
    }

    @JsonView(Views.Public.class)
    @JsonProperty("id")
    public String getPassengerId() {
        return passengerId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonView(Views.Public.class)
    @JsonProperty("firstname")
    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonView(Views.Public.class)
    @JsonProperty("lastname")
    public String getLastName() {
        return lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @JsonView(Views.Public.class)
    public int getAge() {
        return age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonView(Views.Public.class)
    public String getGender() {
        return gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonView(Views.Public.class)
    public String getPhone() {
        return phone;
    }

    @JsonView(Views.Private1.class)
    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
