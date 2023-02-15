package org.miage.tpae.metier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.dao.CompteRepository;
import org.miage.tpae.dao.OperationCompteRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.entities.OperationCompte;
import org.miage.tpae.export.Position;
import org.miage.tpae.utilities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour ServiceCompte.
 * Note : ces tests ne fonctionnent que via Maven.
 */
@DataJpaTest // pour tester en intégration avec la base de donnée H2
class ServiceCompteTest {

    /**
     * ClientRepository sera injecté par DataJpaTest
     */
    @Autowired
    ClientRepository clientRepository;

    /**
     * CompteRepository sera injecté par DataJpaTest
     */
    @Autowired
    CompteRepository compteRepository;

    /**
     * OperationCompteRepository sera injecté par DataJpaTest
     */
    @Autowired
    OperationCompteRepository operationCompteRepository;

    /**
     * On construira le service compte avec les repositories injectés
     */
    ServiceCompte serviceCompte;

    /**
     * Un client de test
     */
    Client client;

    /**
     * Méthode exécutée avant chaque test
     */
    @BeforeEach
    void setUp() {
        // On construit le service compte avec les repositories injectés
        serviceCompte = new ServiceCompte(clientRepository, compteRepository, operationCompteRepository);
        // On crée un client de test
        client = new Client();
        client.setNom("Test");
        client.setPrenom("Jean");
        // On l'insère en base
        client = clientRepository.save(client);
        // On vérifie que JPA nous a bien donné un Id
        assertNotNull(client.getId());
    }

    /**
     * Test de la méthode ouvrir
     */
    @Test
    void ouvrir() {
        // on ouvre un compte
        double solde = 1000;
        Compte compte = serviceCompte.ouvrir(client.getId(), solde);
        // on vérifie que JPA nous a bien donné un Id pour ce nouveau compte
        assertNotNull(compte.getId());
        // on vérifie que le solde est bien le même
        assertEquals(compte.getSolde(), solde, 0.1);
        // on tente d'ouvrir un compte pour un client inexistant
        // on vérifie que ça lance bien l'exception ClientInexistant
        assertThrows(ClientInexistant.class,
                () -> serviceCompte.ouvrir(9999L, 1000));
        // on tente d'ouvrir un compte avec un solde négatif,
        // on vérifie que ça lance bien l'exception MontantInvalidException
        assertThrows(MontantInvalidException.class,
                () -> serviceCompte.ouvrir(client.getId(), -1000));
    }

    /**
     * Test de la méthode fermer
     */
    @Test
    void fermer() {
        // on crée un compte
        // note : on aurait pu le créer à la main pour éviter la dépendance avec la méthode ouvrir
        double solde = 1000;
        Compte compte = serviceCompte.ouvrir(client.getId(), solde);
        assertNotNull(compte.getId());
        assertEquals(compte.getSolde(), solde, 0.1);
        // on teste la méthode
        serviceCompte.fermer(compte.getId());
        // on récupère le compte à nouveau
        Optional<Compte> compte1 = compteRepository.findById(compte.getId());
        // on vérifie qu'on le trouve bien en base
        assertTrue(compte1.isPresent());
        // mais qu'il n'est plus actif
        assertFalse(compte1.get().isActif());
        // on tente de fermer un compte inexistant
        // on vérifie que ça lance bien l'exception CompteInconnuException
        assertThrows(CompteInconnuException.class, () -> serviceCompte.fermer(9999L));
        // on tente de fermer un compte déjà fermé
        // on vérifie que ça lance bien l'exception CompteClotureException
        assertThrows(CompteClotureException.class, () -> serviceCompte.fermer(compte.getId()));
    }

    /**
     * Test de la méthode consulter
     */
    @Test
    void consulter() {
        // on ouvre un compte
        double solde = 1000;
        Compte compte = serviceCompte.ouvrir(client.getId(), solde);
        assertNotNull(compte.getId());
        assertEquals(compte.getSolde(), solde, 0.1);

        // on appelle la méthode
        Position position = serviceCompte.consulter(compte.getId());
        // on vérifie que la position est non nulle
        assertNotNull(position);
        // on vérifie que le solde est ok
        assertEquals(compte.getSolde(), position.getSolde(), 0.1);
        // on vérifie que la date de dernière opération est ok
        assertEquals(compte.getDateInterrogation(), position.getDateInterrogation());
        // on tente de consulter un compte inexistant
        // on vérifie que ça lance bien l'exception CompteInconnuException
        assertThrows(CompteInconnuException.class, () -> serviceCompte.consulter(9999L));
        // on ferme le compte
        serviceCompte.fermer(compte.getId());
        // on tente de consulter un compte déjà fermé
        // on vérifie que ça lance bien l'exception CompteClotureException
        assertThrows(CompteClotureException.class, () -> serviceCompte.fermer(compte.getId()));
    }

    /**
     * Test de la méthode debiter
     */
    @Test
    void debiter() {
        // on crée un compte
        double solde = 1000;
        Compte compte = serviceCompte.ouvrir(client.getId(), solde);
        assertNotNull(compte.getId());
        assertEquals(compte.getSolde(), solde, 0.1);
        // on appelle la méthode
        serviceCompte.debiter(compte.getId(), 100);
        // on modifie la variable référence
        solde -= 100;
        // on consulte le compte
        Position position = serviceCompte.consulter(compte.getId());
        // on vérifie que le solde est bien modifié
        assertEquals(solde, position.getSolde(), 0.1);
        // on tente de débiter un compte inexistant
        // on vérifie que ça lance bien l'exception CompteInconnuException
        assertThrows(CompteInconnuException.class,
                () -> serviceCompte.debiter(9999L, 100));
        // on tente de débiter un compte d'une somme négative,
        // on vérifie que ça lance bien l'exception MontantInvalidException
        assertThrows(MontantInvalidException.class,
                () -> serviceCompte.debiter(compte.getId(), -100));
        // on tente de débiter un compte d'une somme trop importante
        // on vérifie que ça lance bien l'exception SoldeInsuffisantException
        assertThrows(SoldeInsuffisantException.class,
                () -> serviceCompte.debiter(compte.getId(), 1000000));
        // on ferme le compte
        serviceCompte.fermer(compte.getId());
        // on tente de débiter un compte déjà fermé
        // on vérifie que ça lance bien l'exception CompteClotureException
        assertThrows(CompteClotureException.class,
                () -> serviceCompte.debiter(compte.getId(), 100));
    }

    /**
     * Test de la méthode crediter
     */
    @Test
    void crediter() {
        // on crée un compte
        double solde = 1000;
        Compte compte = serviceCompte.ouvrir(client.getId(), solde);
        assertNotNull(compte.getId());
        assertEquals(compte.getSolde(), solde, 0.1);
        // on appelle la méthode
        serviceCompte.crediter(compte.getId(), 100);
        // on modifie la variable référence
        solde += 100;
        // on consulte le compte
        Position position = serviceCompte.consulter(compte.getId());
        // on vérifie que le solde est bien modifié
        assertEquals(solde, position.getSolde(), 0.1);
        // on tente de créditer un compte inexistant
        // on vérifie que ça lance bien l'exception CompteInconnuException
        assertThrows(CompteInconnuException.class,
                () -> serviceCompte.debiter(9999L, 100));
        // on tente de créditer un compte d'une somme négative,
        // on vérifie que ça lance bien l'exception MontantInvalidException
        assertThrows(MontantInvalidException.class,
                () -> serviceCompte.debiter(compte.getId(), -100));
        // on ferme le compte
        serviceCompte.fermer(compte.getId());
        // on tente de créditer un compte déjà fermé
        // on vérifie que ça lance bien l'exception CompteClotureException
        assertThrows(CompteClotureException.class,
                () -> serviceCompte.debiter(compte.getId(), 100));
    }

    /**
     * Test de la méthode virer
     */
    @Test
    void virer() {
        // on crée 2 comptes
        double solde = 1000;
        Compte compte = serviceCompte.ouvrir(client.getId(), solde);
        assertNotNull(compte.getId());
        assertEquals(compte.getSolde(), solde, 0.1);
        Compte compte2 = serviceCompte.ouvrir(client.getId(), solde);
        assertNotNull(compte2.getId());
        assertEquals(compte2.getSolde(), solde, 0.1);
        // on teste la méthode
        serviceCompte.virer(compte.getId(), compte2.getId(), 100);
        // on vérifie que les deux soldes sont bien modifiés
        Position position = serviceCompte.consulter(compte.getId());
        assertEquals(solde-100, position.getSolde(), 0.1);
        Position position2 = serviceCompte.consulter(compte2.getId());
        assertEquals(solde+100, position2.getSolde(), 0.1);
        // on tente de virer depuis un compte inexistant
        // on vérifie que ça lance bien l'exception CompteInconnuException
        assertThrows(CompteInconnuException.class, () -> serviceCompte.virer(9999L, compte2.getId(), 100));
        // on tente de virer vers un compte inexistant
        // on vérifie que ça lance bien l'exception CompteInconnuException
        assertThrows(CompteInconnuException.class, () -> serviceCompte.virer(compte.getId(), 9999L, 100));
        // on tente de virer une somme négative,
        // on vérifie que ça lance bien l'exception MontantInvalidException
        assertThrows(MontantInvalidException.class, () -> serviceCompte.virer(compte.getId(), compte2.getId(), -100));
        // on tente de virer une somme trop importante
        // on vérifie que ça lance bien l'exception SoldeInsuffisantException
        assertThrows(SoldeInsuffisantException.class, () -> serviceCompte.virer(compte.getId(), compte2.getId(), 1000000));
        // on ferme le second compte
        serviceCompte.fermer(compte2.getId());
        // on tente de virer vers un compte déjà fermé
        // on vérifie que ça lance bien l'exception CompteClotureException
        assertThrows(CompteClotureException.class, () -> serviceCompte.virer(compte.getId(), compte2.getId(), 1000000));
        // on ferme le premier compte
        serviceCompte.fermer(compte.getId());
        // on tente de virer depuis un compte déjà fermé
        // on vérifie que ça lance bien l'exception CompteClotureException
        assertThrows(CompteClotureException.class, () -> serviceCompte.virer(compte.getId(), compte2.getId(), 1000000));
    }

    /**
     * Test de la méthode recupererOperations
     */
    @Test
    void recupererOperations() {
        // on crée un compte
        double solde = 1000;
        Compte compte = serviceCompte.ouvrir(client.getId(), solde);
        assertNotNull(compte.getId());
        assertEquals(compte.getSolde(), solde, 0.1);
        // on récupère la liste des opérations
        Collection<OperationCompte> operations = serviceCompte.recupererOperations(compte.getId());
        // il doit y avoir une seule opération : l'ouverture du compte
        assertEquals(operations.size(), 1);
        // on crédite le compte
        serviceCompte.crediter(compte.getId(), 100);
        // on récupère la liste des opérations
        operations = serviceCompte.recupererOperations(compte.getId());
        // il doit y avoir maintenant deux opérations : l'ouverture du compte et le crédit
        assertEquals(operations.size(), 2);
        // on ferme le compte
        serviceCompte.fermer(compte.getId());
        // on récupère la liste des opérations
        operations = serviceCompte.recupererOperations(compte.getId());
        // il doit y avoir maintenant trois opérationq : l'ouverture du compte, le crédit et la fermeture
        assertEquals(operations.size(), 3);
        // on tente de récupérer les opérations d'un compte inexistant
        // on vérifie que ça lance bien l'exception CompteInconnuException
        assertThrows(CompteInconnuException.class, () -> serviceCompte.recupererOperations(9999L));
    }
}