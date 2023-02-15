package org.miage.tpae.metier;

import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.dao.CompteRepository;
import org.miage.tpae.dao.OperationCompteRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.entities.OperationCompte;
import org.miage.tpae.export.Position;
import org.miage.tpae.utilities.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

/**
 * Bean métier pour la gestion des comptes
 * Le Transactional sert à éviter que les opérations soient réalisées partiellement.
 * C'est surtout important pour le virement.
 */
@Service
@Transactional
public class ServiceCompte {

    /**
     * Bean repository pour les clients qui sera injecté
     */
    private final ClientRepository clientRepository;

    /**
     * Bean repository pour les comptes qui sera injecté
     */
    private final CompteRepository compteRepository;

    /**
     * Bean repository pour les opérations qui sera injecté
     */
    private final OperationCompteRepository operationCompteRepository;

    /**
     * Constructeur pour l'injection
     * @param clientRepository bean repo clients injecté
     * @param compteRepository bean repo comptes injecté
     * @param operationCompteRepository bean repo opérations injecté
     */
    public ServiceCompte(ClientRepository clientRepository, CompteRepository compteRepository, OperationCompteRepository operationCompteRepository) {
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.operationCompteRepository = operationCompteRepository;
    }

    /**
     * Permet la création d'un nouveau compte
     * @param idClient l'id du client pour lequel on ouvre le compte
     * @param soldeInitial le solde initial
     * @return le nouveau compte
     * @throws MontantInvalidException si le montant est incorrect (<0)
     * @throws ClientInexistant si le client n'existe pas
     */
    public Compte ouvrir(long idClient, double soldeInitial) throws MontantInvalidException, ClientInexistant {
        //Vérification des règles métiers
        if(soldeInitial < 0.) throw new MontantInvalidException("Le solde initial ne peut pas être négatif.");
        // on cherche le client
        Optional<Client> clientOptional = clientRepository.findById(idClient);
        // s'il n'existe pas on lance une exception
        if (clientOptional.isEmpty()) {
            throw new ClientInexistant("Le client d'id "+idClient+" n'existe pas.");
        }
        //Opération métier
        Compte compte = new Compte();
        // on lie le compte au client
        compte.setClient(clientOptional.get());
        compte.setSolde(soldeInitial);
        // on sauve le compte en BD
        compte = compteRepository.save(compte);
        // on y ajoute une opération pour indiquer son ouverture
        this.nouveauCompte(compte, soldeInitial);
        // on retourne le compte
        return compte;
    }

    /**
     * Fermeture d'un compte
     * @param idCompte id du compte
     * @throws CompteInconnuException si le compte n'existe pas
     */
    public void fermer(long idCompte) throws CompteInconnuException {
        //Récupération du compte et vérification des règles métiers
        Compte compte = findCompte(idCompte);
        //Opération métier
        compte.setActif(false);
        this.nouvelleCloture(compte);
    }

    /**
     * Retourne la position courante du compte
     * @param idCompte id du compte
     * @return la position
     * @throws CompteInconnuException si le compte n'existe pas
     * @throws CompteClotureException si le compte est cloturé
     */
    public Position consulter(long idCompte) throws CompteInconnuException, CompteClotureException {
        //Récupération du compte et vérification des règles métiers
        Compte c = findCompte(idCompte);
        final OperationCompte oc = this.nouvelleConsultation(c, c.getSolde());
        //Opération métier
        c.setDateInterrogation(oc.getDateOperation());
        return new Position(oc.getValeur(), oc.getDateOperation());
    }

    /**
     * Débite le compte
     * @param idCompte id du compte
     * @param montant le montant à débiter
     * @throws CompteInconnuException si le compte n'existe pas
     * @throws MontantInvalidException si le montant est invalide
     * @throws SoldeInsuffisantException si le solde est insuffisant
     * @throws CompteClotureException si le compte est cloturé
     */
    public void debiter(long idCompte, double montant) throws CompteInconnuException, MontantInvalidException, SoldeInsuffisantException, CompteClotureException {
        //Récupération du compte et vérification des règles métiers
        if (montant < 0.) {
            throw new MontantInvalidException("Le montant à débiter ne peut pas être négatif.");
        }
        Compte c = findCompte(idCompte);
        if (c.getSolde() - montant < 0) {
            throw new SoldeInsuffisantException("Solde sur le compte " + idCompte + " insuffisant pour un retrait de " + montant + ".");
        }
        //Opération métier
        c.setSolde(c.getSolde() - montant);
        this.nouveauDebit(c, montant);
    }

    /**
     * Crédite le compte
     * @param idCompte id du compte
     * @param montant le montant à créditer
     * @throws CompteInconnuException si le compte n'existe pas
     * @throws MontantInvalidException si le montant est invalide
     * @throws CompteClotureException si le compte est cloturé
     */
    public void crediter(long idCompte, double montant) throws CompteInconnuException, MontantInvalidException, CompteClotureException {
        //Récupération du compte et vérification des règles métiers
        if (montant < 0.) {
            throw new MontantInvalidException("Le montant à débiter ne peut pas être négatif.");
        }
        Compte c = findCompte(idCompte);
        //Opération métier
        c.setSolde(c.getSolde() + montant);
        this.nouveauCredit(c, montant);
    }


    /**
     * Virement entre deux comptes
     * @param idCompteDebiteur id du compte à débiter
     * @param idCompteCrediteur id du compte à créditer
     * @param montant montant à virer
     * @throws CompteInconnuException si un des comptes n'existe pas
     * @throws MontantInvalidException si le montant est invalide
     * @throws SoldeInsuffisantException si le solde du compte à débiter est insuffisant
     * @throws CompteClotureException si l'un des comptes est cloturé
     */
    public void virer(long idCompteDebiteur, long idCompteCrediteur, double montant) throws CompteInconnuException, MontantInvalidException, SoldeInsuffisantException, CompteClotureException {
        //Récupération des comptes et vérification des règles métiers
        final Compte cDebiteur = this.findCompte(idCompteDebiteur);
        final Compte cCrediteur = this.findCompte(idCompteCrediteur);
        if (montant < 0.) {
            throw new MontantInvalidException("Le montant à virer ne peut pas être négatif.");
        }
        if (cDebiteur.getSolde() - montant < 0) {
            throw new SoldeInsuffisantException("Solde sur le compte " + idCompteDebiteur + " insuffisant pour un retrait de " + montant + ".");
        }
        //Opération métier
        cCrediteur.setSolde(cCrediteur.getSolde() + montant);
        cDebiteur.setSolde(cDebiteur.getSolde() - montant);
        this.nouveauVirementCredit(cCrediteur, montant);
        this.nouveauVirementDebit(cDebiteur, montant);
    }

    /**
     * Retourne la liste des opérations d'un compte
     * @param idCompte id du compte
     * @return la liste d'opérations
     */
    public Collection<OperationCompte> recupererOperations(long idCompte) {
        // Ceci marche à distance mais pose des problèmes pour les tests
        // Compte compte = findCompte(idCompte);
        // return compte.getOperations();
        return operationCompteRepository.findAllByCompteId(idCompte);
    }

    /**
     * Recherche un compte
     * @param idCompte id du compte
     * @return le compte s'il existe
     * @throws CompteInconnuException si le compte n'existe pas
     * @throws CompteClotureException si le compte est cloturé
     */
    private Compte findCompte(long idCompte) throws CompteInconnuException, CompteClotureException {
        final Optional<Compte> c = this.compteRepository.findById(idCompte);
        if (c.isEmpty()) {
            throw new CompteInconnuException("Le compte d'id " + idCompte + " est inconnu.");
        }
        if (!c.get().isActif()) {
            throw new CompteClotureException("Compte " + idCompte + " clôturé.");
        }
        return c.get();
    }

    /**
     * Crée une opération d'ouverture
     * @param compte le compte sur lequel l'opération est effectuée
     * @param soldeInitial le solde du compte
     * @return l'opération
     */
    private OperationCompte nouveauCompte(Compte compte, double soldeInitial) {
        OperationCompte oc = new OperationCompte(compte, OperationCompte.OperationType.OUVERTURE, soldeInitial);
        return this.operationCompteRepository.save(oc);
    }

    /**
     * Crée une opération de consultation
     * @param compte le compte sur lequel l'opération est effectuée
     * @param solde le solde du compte
     * @return l'opération
     */
    private OperationCompte nouvelleConsultation(Compte compte, double solde){
        OperationCompte oc = new OperationCompte(compte, OperationCompte.OperationType.CONSULTATION, solde);
        return this.operationCompteRepository.save(oc);
    }

    /**
     * Crée une opération de crédit
     * @param compte le compte sur lequel l'opération est effectuée
     * @param montant le montant crédité
     * @return l'opération
     */
    private OperationCompte nouveauCredit(Compte compte, double montant) {
        OperationCompte oc = new OperationCompte(compte, OperationCompte.OperationType.CREDIT, montant);
        return this.operationCompteRepository.save(oc);
    }

    /**
     * Crée une opération de débit
     * @param compte le compte sur lequel l'opération est effectuée
     * @param montant le montant débité
     * @return l'opération
     */
    private OperationCompte nouveauDebit(Compte compte, double montant) {
        OperationCompte oc = new OperationCompte(compte, OperationCompte.OperationType.DEBIT, montant);
        return this.operationCompteRepository.save(oc);
    }

    /**
     * Crée une opération de crédit pour un virement
     * @param compte le compte sur lequel l'opération est effectuée
     * @param montant le montant crédité
     * @return l'opération
     */
    private OperationCompte nouveauVirementCredit(Compte compte, double montant) {
        OperationCompte oc = new OperationCompte(compte, OperationCompte.OperationType.VIREMENT_CREDIT, montant);
        return this.operationCompteRepository.save(oc);
    }

    /**
     * Crée une opération de débit pour un virement
     * @param compte le compte sur lequel l'opération est effectuée
     * @param montant le montant débité
     * @return l'opération
     */
    private OperationCompte nouveauVirementDebit(Compte compte, double montant) {
        OperationCompte oc = new OperationCompte(compte, OperationCompte.OperationType.VIREMENT_DEBIT, montant);
        return this.operationCompteRepository.save(oc);
    }

    /**
     * Crée une opération de cloture de compte
     * @param compte le compte sur lequel l'opération est effectuée
     * @return l'opération
     */
    private OperationCompte nouvelleCloture(Compte compte) {
        OperationCompte oc = new OperationCompte(compte, OperationCompte.OperationType.CLOTURE, 0);
        return this.operationCompteRepository.save(oc);
    }
}
