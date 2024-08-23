package com.example.rentallife.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
@Table(name = "property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;  // 将 long 改为 Long

    private String address;
    private String city;
    private String state;
    private String zip;
    private int rooms;
    private double price;
    String term;

    // 连接到房东
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    public User getLandlord() {
        return landlord;
    }

    public void setLandlord(User landlord) {
        this.landlord = landlord;
    }

    // 连接到租客
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "property_tenants",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "tenant_id"))
    private List<User> tenants;

    public Property(String address, String city, String state, String zip, int rooms, double price, String term, User landlord) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.rooms = rooms;
        this.price = price;
        this.term = term;
        this.landlord = landlord;
    }
}