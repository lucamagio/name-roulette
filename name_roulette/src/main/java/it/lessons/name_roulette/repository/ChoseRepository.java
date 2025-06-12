package it.lessons.name_roulette.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.lessons.name_roulette.model.Chose;

public interface ChoseRepository extends JpaRepository<Chose, Integer>{

}
