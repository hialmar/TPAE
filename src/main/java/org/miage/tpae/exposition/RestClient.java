package org.miage.tpae.exposition;

import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.metier.ServiceClient;
import org.miage.tpae.metier.ServiceCompte;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la ressource clients
 */
@RestController
@RequestMapping("/api/clients")
public class RestClient {

    /**
     * Injection du bean métier pour les clients
     * Ici on a utilisé la nouvelle solution pour l'injection via le constructeur
     */
    private final ServiceClient serviceClient;

    /**
     * Injection du bean métier pour les comptes
     * Ici on a utilisé la nouvelle solution pour l'injection via le constructeur
     */
    private final ServiceCompte serviceCompte;

    /**
     * Constructeur pour l'injection (remplace les @Autowired)
     * @param serviceClient le bean métier client injecté
     * @param serviceCompte le bean métier compte injecté
     */
    public RestClient(ServiceClient serviceClient, ServiceCompte serviceCompte) {
        this.serviceClient = serviceClient;
        this.serviceCompte = serviceCompte;
    }

    /**
     * Permet de récupérer les détails d'un client
     * GET sur http://localhost:8080/api/clients/1
     * @param idClient id du client
     * @return le client qui sera traduit en JSON
     */
    @GetMapping("{id}")
    public Client getClient(@PathVariable("id") long idClient) {
        return serviceClient.recupererClient(idClient);
    }

    /**
     * Permet de créer un nouveau client
     * POST sur http://localhost:8080/api/clients
     * @param client les détails du client envoyés par le front
     *               uniquement prénom et nom sont utiles
     *               Exemple : { "nom" : "Durand", "prenom" : "Marcel" }
     * @return le nouveau client en JSON
     */
    @PostMapping
    public Client creerClient(@RequestBody Client client) {
        return serviceClient.creerClient(client.getPrenom(), client.getNom());
    }

    /**
     * Permet de créer un compte pour ce client
     * POST sur http://localhost:8080/api/clients/1/comptes
     * Note : c'est dans ce contrôleur car la ressource est ici considéré comme une sous-ressource
     * @param idClient id du client
     * @param compte les détails du compte qui seront envoyés par le front
     *               ici seul le solde est utile
     *               Exemple : { "solde" : 10000 }
     * @return le nouveau compte
     */
    @PostMapping("{id}/comptes")
    public Compte ouvrirCompte(@PathVariable("id") long idClient, @RequestBody Compte compte) {
        return serviceCompte.ouvrir(idClient, compte.getSolde());
    }

    /**
     * Permet de récupérer la liste des comptes d'un client donné
     * GET sur http://localhost:8080/api/clients/1/comptes
     * @param idClient id du client
     * @return la liste des comptes en JSON
     */
    @GetMapping("{id}/comptes")
    public List<Compte> listerComptes(@PathVariable("id") long idClient) {
        return serviceClient.recupererClient(idClient).getComptes();
    }
}
