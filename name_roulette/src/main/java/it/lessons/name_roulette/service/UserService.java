package it.lessons.name_roulette.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import it.lessons.name_roulette.model.User;
import it.lessons.name_roulette.repository.UserRepository;
import it.lessons.name_roulette.security.DatabaseUserDetails;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //Aggiungere Parenti alla lista
    public User aggiungiParenteAllaLista(Integer genitoreId, String parenteUsername){
        
        Optional<User> optGenitore = userRepository.findById(genitoreId);
        Optional<User> optParente = userRepository.findByUsername(parenteUsername);
    
        if (!optGenitore.isPresent()) {
            throw new IllegalArgumentException("Genitore non trovato.");
        }
        if (!optParente.isPresent()) {
            throw new IllegalArgumentException("Utente con username '" + parenteUsername + "' non trovato.");
        }
    
        User genitore = optGenitore.get();
        User parente = optParente.get();
    
        if (!"GENITORE".equals(genitore.getRole().getName())) {
            throw new IllegalArgumentException("L'utente non è un genitore.");
        }

        if (!"PARENTE".equals(parente.getRole().getName())) {
            throw new IllegalArgumentException("L'utente non è un parente.");
        }
    
        if (genitore.getListaParenti().contains(parente)) {
            throw new IllegalArgumentException("Questo utente è già nella tua lista.");
        }

        List<User> listaGenitori = userRepository.findByRoleName("GENITORE");
        boolean parenteAssociato = false;
        User genitoreAssociato;

        for (User userGenitore : listaGenitori) {

            List<User> listaParenti = userGenitore.getListaParenti();
            if (listaParenti != null) {
                for (User user : listaParenti) {
                    if (user.getId().equals(parente.getId())) {
                        parenteAssociato = true;
                        genitoreAssociato = userGenitore;
                        break;
                    }
                }
            }
            if (parenteAssociato) {
                break;
            }
        }

        if (parenteAssociato == true) {
            throw new IllegalArgumentException("Questo utente è già presente in un'altra lista.");
        }
    
        genitore.getListaParenti().add(parente);
        return userRepository.save(genitore);
    }

    //Lista parenti Autorizzati
    public List<User> getParentiAutorizzati(Integer genitoreId){

        Optional<User> optGenitore = userRepository.findById(genitoreId);

        if (!optGenitore.isPresent()) {
            throw new IllegalArgumentException("Genitore con ID " + genitoreId + " non trovato.");
        }

        User genitore = optGenitore.get();

        return genitore.getListaParenti();
    }

    //Utente autenticato
    public User utenteAutenticato(){

        // Ottieni l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DatabaseUserDetails userDetails = (DatabaseUserDetails) authentication.getPrincipal();

        // Carica l'entità User dal database usando l'id
        User user = userRepository.findById(userDetails.getId()).orElseThrow();

        return user;
    }
}
