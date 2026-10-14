package com.petadoption.pet_adoption_portal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadPets(PetRepository petRepository) {
        return args -> {

            if (petRepository.count() == 0) {

                Pet dog = new Pet();
                dog.setName("Salaar");
                dog.setSpecies("Dog");
                dog.setBreed("Labrador");
                dog.setAge(2);
                dog.setDescription(
                    "A friendly and playful dog looking for a loving home."
                );
                dog.setStatus("Available");
                dog.setImageUrl("/images/pets/salaar.jpg.webp");
                petRepository.save(dog);

                Pet cat = new Pet();
                cat.setName("Scotch");
                cat.setSpecies("Cat");
                cat.setBreed("Persian");
                cat.setAge(1);
                cat.setDescription(
                    "A gentle cat who enjoys attention and a comfortable home."
                );
                cat.setStatus("Available");
                cat.setImageUrl("/images/pets/scotch.jpg");
                petRepository.save(cat);

                Pet puppy = new Pet();
                puppy.setName("Whisky");
                puppy.setSpecies("Dog");
                puppy.setBreed("Beagle");
                puppy.setAge(1);
                puppy.setDescription(
                    "An energetic puppy who loves playing and going for walks."
                );
                puppy.setStatus("Available");
                puppy.setImageUrl("/images/pets/whisky.jpg");
                petRepository.save(puppy);

            } else {

                petRepository.findAll().forEach(pet -> {

                    if ("Salaar".equalsIgnoreCase(pet.getName())) {
                        pet.setImageUrl("/images/pets/salaar.jpg.webp");
                        petRepository.save(pet);
                    }

                    if ("Scotch".equalsIgnoreCase(pet.getName())) {
                        pet.setImageUrl("/images/pets/scotch.jpg");
                        petRepository.save(pet);
                    }

                    if ("Whisky".equalsIgnoreCase(pet.getName())) {
                        pet.setImageUrl("/images/pets/whisky.jpg");
                        petRepository.save(pet);
                    }

                });
            }

            System.out.println("Pet data and image paths are ready!");
        };
    }
}
