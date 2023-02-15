package org.miage.tpae.dao;

import org.junit.jupiter.api.Test;
import org.miage.tpae.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour ClientRepository
 */
@DataJpaTest  // pour tester en intégration avec la base de donnée H2
// Si on veut tester avec la vraie BD, il faut en plus :
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryTest {

    @Autowired
    ClientRepository clientRepository;

    /**
     * Test de la méthode findByPrenomAndNom
     */
    @Test
    void findByPrenomAndNom() {
        // on vérifie que le repository a bien été injecté
        assertNotNull(clientRepository);
        // on crée le client
        Client client = new Client();
        client.setNom("Test");
        client.setPrenom("Jean");
        // on l'insère en base
        client = clientRepository.save(client);
        // on vérifie que JPA nous a bien donné un id
        assertNotNull(client.getId());
        // on teste la méthode
        List<Client> liste =  clientRepository.findByPrenomAndNom("Jean", "Test");
        // on vérifie que la liste n'est pas nulle
        assertNotNull(liste);
        // et qu'elle contient bien le client inséré en base
        assertTrue(liste.contains(client));
        // on teste la méthode avec un autre nom
        liste =  clientRepository.findByPrenomAndNom("Jean", "Test2");
        // on vérifie que la liste n'est pas nulle
        assertNotNull(liste);
        // et qu'elle est bien vide
        assertTrue(liste.isEmpty());
    }
}