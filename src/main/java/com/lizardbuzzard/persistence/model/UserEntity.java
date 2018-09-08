package com.lizardbuzzard.persistence.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * CREATE TABLE `users` (
 *   `username` varchar(50) NOT NULL,
 *   `password` varchar(50) NOT NULL,
 *   `enabled` smallint(6) NOT NULL,
 *   PRIMARY KEY (`username`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 */
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "password", length = 64)
    @NotNull
    private String password;

    @Column(name = "enabled")
    @NotNull
    private short enabled;

    public UserEntity() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public short getEnabled() {
        return enabled;
    }

    public void setEnabled(short enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        UserEntity that = (UserEntity) o;
        return getEnabled() == that.getEnabled() &&
                Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getEnabled());
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
