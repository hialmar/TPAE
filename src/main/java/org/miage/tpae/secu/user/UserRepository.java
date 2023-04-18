package org.miage.tpae.secu.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repo pour les utilisateurs
 */
public interface UserRepository extends JpaRepository<User, Integer> {

  /**
   * Permet de récupérer un utilisateur à partir de son e-mail
   * @param email l'e-mail
   * @return optionnellement un utilisateur
   */
  Optional<User> findByEmail(String email);

}
