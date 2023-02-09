package org.miage.tpae.exposition;

import org.miage.tpae.entities.Compte;
import org.miage.tpae.entities.OperationCompte;
import org.miage.tpae.export.OperationImport;
import org.miage.tpae.export.Position;
import org.miage.tpae.export.VirementImport;
import org.miage.tpae.metier.ServiceCompte;
import org.miage.tpae.utilities.OperationNonConforme;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Contrôleur REST pour la ressource comptes
 */
@RestController
@RequestMapping("/api/comptes/{id}")
public class RestCompte {

    /**
     * Bean métier qui sera injecté par le constructeur
     * Note : on n'utilise pas @Autowired ici
     */
    public final ServiceCompte serviceCompte;

    /**
     * Constructeur pour l'injection du bean métier
     * Note : remplace le @Autowired
     * @param serviceCompte
     */
    public RestCompte(ServiceCompte serviceCompte) {
        this.serviceCompte = serviceCompte;
    }

    /**
     * Permet de récupérer les détails utiles d'un compte
     * GET sur http://localhost:8080/api/comptes/1
     * @param idCompte id du compte
     * @return la position du compte en JSON
     */
    @GetMapping
    public Position getCompte(@PathVariable("id") long idCompte) {
        return this.serviceCompte.consulter(idCompte);
    }

    /**
     * Permet de récupérer la liste des opérations
     * GET sur http://localhost:8080/api/comptes/1/operations
     * @param idCompte id du compte
     * @return la liste d'opérations en JSON
     */
    @GetMapping("/operations")
    public Collection<OperationCompte> recupererOperations(@PathVariable("id") long idCompte) {
        return this.serviceCompte.recupererOperations(idCompte);
    }

    /**
     * Permet de faire des opérations de crédit et de débit sur le compte
     * POST sur http://localhost:8080/api/comptes/1/operations
     * @param idCompte id du compte
     * @param operationImport détails de l'opération à réaliser
     *                        contient le type d'opération et la somme (valeur)
     *                        Exemple : { "valeur" : 100, "operationType" : "CREDIT" }
     *                        Exemple 2 : { "valeur" : 100, "operationType" : "DEBIT" }
     * @return la nouvelle position du compte
     * @throws OperationNonConforme si l'opération n'est ni DEBIT ni CREDIT
     */
    @PostMapping("operations")
    public Position operationCompte(@PathVariable("id") long idCompte, @RequestBody OperationImport operationImport) throws OperationNonConforme {
        if (operationImport.getOperationType() == OperationCompte.OperationType.CREDIT)
            this.serviceCompte.crediter(idCompte, operationImport.getValeur());
        else if (operationImport.getOperationType() == OperationCompte.OperationType.DEBIT)
            this.serviceCompte.debiter(idCompte, operationImport.getValeur());
        else
            throw new OperationNonConforme("L'operation de type "+operationImport.getOperationType()+" n'est pas conforme");
        return this.serviceCompte.consulter(idCompte);
    }

    /**
     * Permet de faire un virement entre deux comptes
     * POST sur http://localhost:8080/api/comptes/1/operations/virements
     * @param idCompte id du compte
     * @param virementImport détails du virement
     *                       contient la valeur (somme) et l'id du compte destinataire
     *                       Exemple : { "valeur" : 100, "idCompteDestinataire" : 2 }
     * @return la nouvelle position du compte débité
     */
    @PostMapping("operations/virements")
    public Position virementCompte(@PathVariable("id") long idCompte, @RequestBody VirementImport virementImport) {
        this.serviceCompte.virer(idCompte, virementImport.getIdCompteDestinataire(), virementImport.getValeur());
        return this.serviceCompte.consulter(idCompte);
    }

    /**
     * Permet de cloturer un compte
     * DELETE sur http://localhost:8080/api/comptes/1
     * @param idCompte id du compte à fermer
     */
    @DeleteMapping
    public void fermerCompte(@PathVariable("id") long idCompte) {
        this.serviceCompte.fermer(idCompte);
    }


}
