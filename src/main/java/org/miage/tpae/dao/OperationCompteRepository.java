package org.miage.tpae.dao;

import org.miage.tpae.entities.OperationCompte;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO pour les entités représentant les opérations
 */
public interface OperationCompteRepository extends CrudRepository<OperationCompte, Long> {
}
