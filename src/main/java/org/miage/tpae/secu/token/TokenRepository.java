package org.miage.tpae.secu.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repo pour les jetons
 */
public interface TokenRepository extends JpaRepository<Token, Integer> {

  /**
   * Permet de trouver tous les jetons valides
   * @param id id de l'utilisateur
   * @return la liste des jetons valides
   */
  @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<Token> findAllValidTokenByUser(Integer id);

  /**
   * Cherche une entité jeton à partir du jeton transmis
   * @param token le jeton
   * @return un optionnel qui peut comprendre l'entité
   */
  Optional<Token> findByToken(String token);
}
