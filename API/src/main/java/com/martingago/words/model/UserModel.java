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
@Table(name = "users")
@Entity
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true)
    private String username;
    private String password;

    @Column(name="is_enabled")
    private Boolean isEnabled;

    @Column(name="account_non_expired")
    private Boolean accountNonExpired;

    @Column(name="account_non_locked")
    private Boolean accountNonLocked;

    @Column(name="credential_non_expired")
    private Boolean CredentialNonExpired;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="users_roles",
            joinColumns = @JoinColumn(name= "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleModel> roleModelSet = new HashSet<>();

}
