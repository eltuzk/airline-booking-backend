package com.airlinebooking.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassengerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passengerId;

    private String fullName;

    private LocalDate dateOfBirth;

    private String gender; // MALE, FEMALE, OTHER

    private String passengerType; // ADULT, CHILD, INFANT

    private String passportNumber;

    private String identityCard;

    private Integer countryId;

    @OneToMany(mappedBy = "passenger")
    private List<PassengerTicketEntity> passengerTicketEntityList;

}
