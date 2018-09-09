package com.lizardbuzzard.persistence;

import org.hibernate.dialect.MySQL57Dialect;

/**
 * works in conjunction with spring.jpa.properties.hibernate.dialect=com.lizardbuzzard.persistence.CustomMySQLDialect
 * should be tuned for correct working with MySql version currently installed. Otherwise gives an error
 */
public class CustomMySQLDialect extends MySQL57Dialect {
    @Override
    public boolean dropConstraints() {
        return false;
    }
}