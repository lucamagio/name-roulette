package it.lessons.name_roulette.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.lessons.name_roulette.model.Role;
import it.lessons.name_roulette.model.User;


public class DatabaseUserDetails implements UserDetails{

    private final Integer id;
    private final String email;
    private final String password;
    private final String username;
    private final List<GrantedAuthority> authorities;


    public DatabaseUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.authorities = new ArrayList<>();

        Role ruolo = user.getRole();
        if (ruolo != null) {
            this.authorities.add(new SimpleGrantedAuthority(ruolo.getName()));
        }
    }

    public Integer getId() {
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}
