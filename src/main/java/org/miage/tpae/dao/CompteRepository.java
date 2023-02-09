package org.miage.tpae.dao;

import org.miage.tpae.entities.Compte;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO pour les entités de type Compte
 */
public interface CompteRepository extends CrudRepository<Compte, Long> {

}
