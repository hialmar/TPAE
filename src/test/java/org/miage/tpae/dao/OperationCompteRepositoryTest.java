package org.miage.tpae.dao;

import org.junit.jupiter.api.Test;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.entities.OperationCompte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collection;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour OperationCompteRepository
 */
@DataJpaTest
class OperationCompteRepositoryTest {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    CompteRepository compteRepository;

    @Autowired
    OperationCompteRepository operationCompteRepository;

    @Test
    void findAllByCompteId() {
        // on vérifie que les repositories sont bien injectés
        assertNotNull(clientRepository);
        assertNotNull(compteRepository);
        assertNotNull(operationCompteRepository);
        // on crée le client
        Client client = new Client();
        client.setNom("Test");
        client.setPrenom("Jean");
        // on l'insère en base
        client = clientRepository.save(client);
        // on vérifie que JPA nous a bien donné un id
        assertNotNull(client.getId());
        // on crée le compte
        Compte compte = new Compte();
        compte.setClient(client);
        compte.setSolde(1000);
        // on l'insère en base
        compte = compteRepository.save(compte);
        // on vérifie que JPA nous a bien donné un id
        assertNotNull(compte.getId());
        // on crée une opération
        OperationCompte operationCompte = new OperationCompte();
        operationCompte.setCompte(compte);
        operationCompte.setOperationType(OperationCompte.OperationType.OUVERTURE);
        operationCompte.setDateOperation(GregorianCalendar.getInstance());
        operationCompte.setValeur(1000);
        // on l'insère en base
        operationCompte = operationCompteRepository.save(operationCompte);
        // on appelle la méthode
        Collection<OperationCompte> operations = operationCompteRepository.findAllByCompteId(compte.getId());
        // on vérifie que la collection n'est pas nulle
        assertNotNull(operations);
        // on vérifie qu'elle contient bien 1 opération
        assertEquals(operations.size(), 1);
        // on vérifie qu'elle contient bien l'opération
        assertTrue(operations.contains(operationCompte));
        // on teste la méthode avec un id inconnu
        operations = operationCompteRepository.findAllByCompteId(9999L);
        // on vérifie que la collection n'est pas nulle
        assertNotNull(operations);
        // on vérifie que la collection est vide
        assertTrue(operations.isEmpty());
    }
}