package org.miage.tpae.dao;

import org.miage.tpae.entities.OperationCompte;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

/**
 * DAO pour les entités représentant les opérations
 */
public interface OperationCompteRepository extends CrudRepository<OperationCompte, Long> {
    Collection<OperationCompte> findAllByCompteId(long idCompte);
}
