package com.lizardbuzzard.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * CREATE TABLE `authorities` (
 * `username` varchar(50) NOT NULL,
 * `authority` varchar(50) NOT NULL,
 * UNIQUE KEY `ix_auth_username` (`username`,`authority`),
 * CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 */
@Entity
@Table(name = "authorities")
@IdClass(AuthorityId.class)
public class AuthorityEntity {
    @Id
    @Column(name = "username", length = 50)
    @NotNull
    private String username;

    @Id
    @Column(name = "authority", length = 50)
    @NotNull
    private String authority;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    })
    @JsonIgnore
    private UserEntity user;

    public AuthorityEntity() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorityEntity)) return false;
        AuthorityEntity that = (AuthorityEntity) o;
        return Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getAuthority(), that.getAuthority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getAuthority());
    }

    @Override
    public String toString() {
        return "AuthorityEntity{" +
                "username='" + username + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}