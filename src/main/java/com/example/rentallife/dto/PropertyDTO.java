package com.example.rentallife.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PropertyDTO {
    private long id;
    private String address;
    private String city;
    private String state;
    private String zip;
    private int rooms;
    private double price;
    private String term;
    private long landlordId;

    // Constructor with fields
    public PropertyDTO(long id, String address, String city, String state, String zip, int rooms, double price, String term, long landlordId) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.rooms = rooms;
        this.price = price;
        this.term = term;
        this.landlordId = landlordId;
    }
}
