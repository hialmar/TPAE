package org.miage.tpae;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.dao.CompteRepository;
import org.miage.tpae.dao.OperationCompteRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.entities.OperationCompte;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Application Spring Boot
 */
@SpringBootApplication
public class TpaeApplication implements CommandLineRunner {

    /**
     * logger
     */
    private static Logger logger = LogManager.getLogger(TpaeApplication.class);

    /**
     * repo pour tests
     */
    private final ClientRepository clientRepository;
    /**
     * repo pour tests
     */
    private final CompteRepository compteRepository;
    /**
     * repo pour tests
     */
    private final OperationCompteRepository operationCompteRepository;

    /**
     * constructeur pour l'injection de dépendances
     * @param clientRepository bean à injecter
     * @param compteRepository bean à injecter
     * @param operationCompteRepository bean à injecter
     */
    public TpaeApplication(ClientRepository clientRepository, CompteRepository compteRepository, OperationCompteRepository operationCompteRepository) {
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.operationCompteRepository = operationCompteRepository;
    }

    /**
     * Main de l'application
     * @param args arguments pour Spring
     */
    public static void main(String[] args) {
        SpringApplication.run(TpaeApplication.class, args);
    }

    /**
     * Méthode pour tester les répositories
     * On a besoin du Transactionnal pour que les listes de comptes remontent avec les clients
     * @param args arguments non utilisés
     * @throws Exception en cas de problème avec la BD
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
    public void run(String... args) throws Exception {
        List<Client> clientList = clientRepository.findByPrenomAndNom("Jean", "Dupond");

        if (clientList.isEmpty()) {
            Client c = new Client();
            c.setNom("Dupond");
            c.setPrenom("Jean");
            c = clientRepository.save(c);

            logger.info("Client " + c);

            Compte cpt = new Compte();
            cpt.setClient(c);
            cpt.setSolde(1000);
            cpt = compteRepository.save(cpt);

            logger.info("Compte " + cpt);

            OperationCompte operationCompte = new OperationCompte();
            operationCompte.setCompte(cpt);
            operationCompte.setOperationType(OperationCompte.OperationType.OUVERTURE);
            operationCompte.setDateOperation(GregorianCalendar.getInstance());
            operationCompte = operationCompteRepository.save(operationCompte);

            logger.info("Operation " + operationCompte);

            clientList = clientRepository.findByPrenomAndNom("Jean", "Dupond");
        }

        logger.info("Liste : "+clientList);
    }
}
