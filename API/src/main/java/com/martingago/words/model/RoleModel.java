package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_entity_seq")
    @SequenceGenerator(name = "role_entity_seq", sequenceName = "role_entity_seq", allocationSize = 1)
    private Long id;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="roles_permissions",
            joinColumns = @JoinColumn(name= "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionModel> permissionModelSet = new HashSet<>();
}
