package cmpe275.lab2.domain;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Embeddable
@Table(name = "passenger")
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

    public Passenger() {
        this.passengerId = "passenger" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void setPassengerId(String id) {
        this.passengerId = id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
