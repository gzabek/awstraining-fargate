package com.awstraining.backend.users;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@ConfigurationProperties(value = "backend.security")
public class Users {
    public static final String USER = "USER";
    private List<User> users = new ArrayList<>();

    /**
     *
     */
    public static class User {
        private String username;
        private String password;
        private Set<String> roles = new HashSet<>();

        /**
         *
         * @return
         */
        public String getUsername() {
            return username;
        }

        /**
         *
         * @param username
         */
        public void setUsername(final String username) {
            this.username = username;
        }

        /**
         *
         * @return
         */
        public String getPassword() {
            return password;
        }

        /**
         *
         * @param password
         */
        public void setPassword(final String password) {
            this.password = password;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(final Set<String> roles) {
            this.roles = roles;
        }

        @Override
        public String toString() {
            return "User{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", roles=" + roles
                    + '}';
        }
    }

    /**
     *
     * @return
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     *
     * @param users
     */
    public void setUsers(final List<User> users) {
        this.users = users;
    }
}
