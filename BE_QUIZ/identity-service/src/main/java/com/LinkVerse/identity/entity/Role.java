package com.LinkVerse.identity.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {
       @Id
      String name;

    String description;

       @ManyToMany
@JoinTable(
    name = "role_permissions",
    joinColumns = @JoinColumn(name = "role_name", referencedColumnName = "name"),  // bên Role
    inverseJoinColumns = @JoinColumn(name = "permission_name", referencedColumnName = "name")  // bên Permission
)
Set<Permission> permissions;

}
