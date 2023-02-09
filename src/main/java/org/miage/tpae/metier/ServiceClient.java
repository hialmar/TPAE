package org.miage.tpae.metier;

import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.dao.CompteRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.utilities.ClientInexistant;
import org.miage.tpae.utilities.MontantInvalidException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Bean métier pour la gestion des clients
 */
@Service
public class ServiceClient {
    /**
     * Bean repository qui sera injecté par le constructeur
     */
    private final ClientRepository clientRepository;

    /**
     * Constructeur pour l'injection du bean repository
     * @param clientRepository le bean repository à injecter
     */
    public ServiceClient(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Demande la création d'un nouveau client
     * Si le client existe déjà on le retourne
     * @param prenom prénom du client
     * @param nom nom du client
     * @return le nouveau client ou l'ancien client
     */
    public Client creerClient(String prenom, String nom) {
        //Opération métier
        //On cherche si le client est déjà présent
        List<Client> clients = clientRepository.findByPrenomAndNom(prenom, nom);
        Client client;
        // s'il n'est pas présent
        if (clients.isEmpty()) {
            // on le crée
            client = new Client();
            client.setPrenom(prenom);
            client.setNom(nom);
            // on l'ajoute à la BD
            client = clientRepository.save(client);
        } else {
            // sinon, on retournera l'ancien
            client = clients.get(0);
        }
        // retourne le client
        return client;
    }

    /**
     * Permet de récupérer les infos d'un client
     * @param idClient id du client
     * @return infos du client
     * @throws ClientInexistant s'il n'existe pas de client avec cet id
     */
    public Client recupererClient(long idClient) throws ClientInexistant {
        // on cherche le client
        final Optional<Client> optionalClient = clientRepository.findById(idClient);
        // s'il n'existe pas on lance une exception
        if(optionalClient.isEmpty())
            throw new ClientInexistant("Le client d'id "+idClient+" n'existe pas.");
        // sinon, on renvoie les infos
        return optionalClient.get();
    }
}
