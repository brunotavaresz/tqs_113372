package com.example.bookingMoliceiro.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationCode = UUID.randomUUID().toString();
    private boolean checkedIn = false;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToOne
    private Meal meal;

    private String customerName;
}
