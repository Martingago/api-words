package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permission")
@Entity
public class PermissionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_entity_seq")
    @SequenceGenerator(name = "permission_entity_seq", sequenceName = "permission_entity_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String name;
}
