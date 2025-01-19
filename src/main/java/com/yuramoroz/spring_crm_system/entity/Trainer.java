package com.yuramoroz.spring_crm_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "trainers")
public class Trainer extends User{

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "specialization")
    private String specialization;

    public Trainer(String firstName, String lastName, Boolean isActive, String specialization) {
        super(firstName, lastName, isActive);
        this.specialization = specialization;
    }
}