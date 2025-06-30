package it.lessons.name_roulette.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.lessons.name_roulette.model.Chose;
import it.lessons.name_roulette.repository.ChoseRepository;

@Service
public class ChoseService {

    @Autowired
    private ChoseRepository choseRepository;

    /**
     * Restituisce la Chose esistente, oppure la crea se il nome è nuovo.
     * Se non è cambiata rispetto all'utente attuale, restituisce quella vecchia.
     */
    public Chose aggiornamentoChose(Chose currentChose, Chose newChose) {
        if (newChose == null || newChose.getName() == null || newChose.getName().trim().isEmpty()) {
            return null; // Nessuna scelta fornita
        }

        String newName = newChose.getName().trim();

        if (currentChose != null && newName.equalsIgnoreCase(currentChose.getName())) {
            return currentChose; // Nessuna modifica
        }

        // Cerca nel DB
        Optional<Chose> existing = choseRepository.findByName(newName);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Altrimenti crea nuova
        Chose c = new Chose();
        c.setName(newName.substring(0, 1).toUpperCase() + newName.substring(1).toLowerCase());
        return choseRepository.save(c);
    }

    public Chose assegnazioneChose(String nameChose){
            
        Optional<Chose> optionalChose = choseRepository.findByName(nameChose.trim());
        Chose chose;
    
        if (optionalChose.isPresent()) {
            chose = optionalChose.get();
        } else {
            chose = new Chose();
            chose.setName(nameChose.trim().substring(0, 1).toUpperCase() + nameChose.substring(1).toLowerCase());
            chose = choseRepository.save(chose);
        }

        return chose;
    }
}
