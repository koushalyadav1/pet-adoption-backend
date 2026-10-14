
package com.petadoption.pet_adoption_portal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRequestRepository
        extends JpaRepository<AdoptionRequest, Long> {

    void deleteByPet_Id(Long petId);
}
