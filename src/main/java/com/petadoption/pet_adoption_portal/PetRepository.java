
package com.petadoption.pet_adoption_portal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByStatus(String status);

    List<Pet> findBySpeciesIgnoreCase(String species);
}
