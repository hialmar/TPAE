package org.miage.tpae.dao;

import org.miage.tpae.entities.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * DAO pour les entités représentant les clients de la Banque
 */
public interface ClientRepository extends CrudRepository<Client, Long> {

    /**
     * Recherche des clients par prénom et nom
     * @param prenom le prénom
     * @param nom le nom
     * @return la liste des clients qui correspondent
     */
    List<Client> findByPrenomAndNom(String prenom, String nom);

}
