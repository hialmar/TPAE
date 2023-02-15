package org.miage.tpae.metier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.utilities.ClientInexistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour ServiceClient
 */
@DataJpaTest
class ServiceClientTest {

    /**
     * ClientRepository sera injecté par DataJpaTest
     */
    @Autowired
    ClientRepository clientRepository;

    /**
     * On construira le service client avec le repository injecté
     */
    ServiceClient serviceClient;

    /**
     * Appelé avant toute méthode de test
     */
    @BeforeEach
    void setup() {
        // on construit le service client avec le repository injecté
        serviceClient = new ServiceClient(clientRepository);
    }


    /**
     * Test de la méthode creerClient
     */
    @Test
    void creerClient() {
        // on essaie de créer un client
        Client client = serviceClient.creerClient("Edouard", "Test");
        // on vérifie que JPA nous a donné un id
        assertNotNull(client.getId());
        // On vérifie que le client apparait lors d'une recherche à partir du prénom et du nom
        List<Client> clientList = clientRepository.findByPrenomAndNom("Edouard", "Test");
        assertTrue(clientList.contains(client));
        // on essaie de recréer le même client
        Client client2 = serviceClient.creerClient("Edouard", "Test");
        // on vérifie que c'est bien le même client
        assertEquals(client2, client);
    }

    /**
     * Test de la méthode recupererClient
     */
    @Test
    void recupererClient() {
        // on crée le client
        Client client = new Client();
        client.setNom("Test");
        client.setPrenom("Jean");
        // on l'insère en base
        client = clientRepository.save(client);
        // on vérifie que JPA nous a bien donné un id
        assertNotNull(client.getId());
        // on vérifie qu'on peut le récupérer
        Client client1 = serviceClient.recupererClient(client.getId());
        // on vérifie que c'est bien le même client
        assertEquals(client1, client);
        // on tente de récupérer un client inexistant
        // on vérifie que ça lance bien l'exception ClientInexistant
        assertThrows(ClientInexistant.class, () -> serviceClient.recupererClient(9999L));
    }
}