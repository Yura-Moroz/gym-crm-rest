package com.yuramoroz.spring_crm_system.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @OneToMany(mappedBy = "trainer")
    @Builder.Default
    private List<Training> trainings = new ArrayList<>();
}