package it.lessons.name_roulette.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import it.lessons.name_roulette.model.Chose;
import it.lessons.name_roulette.model.User;

public interface ChoseRepository extends JpaRepository<Chose, Integer>{

    Optional<Chose> findByName(String name);
}
