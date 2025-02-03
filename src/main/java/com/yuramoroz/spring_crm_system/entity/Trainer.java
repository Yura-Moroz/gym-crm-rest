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
@SuperBuilder
@Entity
@Table(name = "trainers")
public class Trainer extends User{

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Training> trainings;
}