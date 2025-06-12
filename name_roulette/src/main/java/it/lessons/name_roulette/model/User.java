package it.lessons.name_roulette.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Inserisci un username valido")
    private String username;

    @NotBlank(message = "Inserisci una email valida")
    private String email;

    @NotBlank(message = "Inserisci una password valida")
    private String password;

    private String gender;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "chose_id", nullable = true)
    private Chose chose;

    @ManyToMany
    @JoinTable(
        name = "genitore_parenti",
        joinColumns = @JoinColumn(name = "genitore_id"),
        inverseJoinColumns = @JoinColumn(name = "parente_id")
    )
    private List<User> listaParenti;



    public Chose getChose() {
        return chose;
    }

    public void setChose(Chose chose) {
        this.chose = chose;
    }

    public List<User> getListaParenti() {
        return listaParenti;
    }

    public void setListaParenti(List<User> listaParenti) {
        this.listaParenti = listaParenti;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
