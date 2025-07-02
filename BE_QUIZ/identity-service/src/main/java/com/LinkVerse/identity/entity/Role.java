package com.LinkVerse.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {

    @Id
    @Column(name = "name", nullable = false, updatable = false)
    String name;

    @Column(name = "description")
    String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_name", referencedColumnName = "name"),
        inverseJoinColumns = @JoinColumn(name = "permission_name", referencedColumnName = "name")
    )
    Set<Permission> permissions;
}
