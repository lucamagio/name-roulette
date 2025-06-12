package it.lessons.name_roulette.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.lessons.name_roulette.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

}
