package org.miage.tpae.exposition;

import org.junit.jupiter.api.Test;
import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.dao.CompteRepository;
import org.miage.tpae.dao.OperationCompteRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.metier.ServiceClient;
import org.miage.tpae.metier.ServiceCompte;
import org.miage.tpae.secu.config.JwtService;
import org.miage.tpae.secu.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;


/**
 * Classe de test unitaire pour le contrôleur REST RestClient
 */
@WebMvcTest(RestClient.class)
class RestClientTest {
    @Autowired
    private MockMvc mvc; // injecté automatiquement

    // Tous les autres beans sont mockés
    @MockBean
    private ServiceClient serviceClient;

    @MockBean
    private ServiceCompte serviceCompte;

    @MockBean // on en a besoin pour le démarrage des tests car il est utilisé par serviceCompte
    private ClientRepository clientRepository;

    @MockBean // on en a besoin pour le démarrage des tests car il est utilisé par serviceCompte
    private CompteRepository compteRepository;

    @MockBean
    OperationCompteRepository operationCompteRepository;

    @MockBean
    JwtService jwtService;
    @MockBean
    TokenRepository tokenRepository;

    /**
     * Test de la méthode GET getClient
     * @throws Exception en cas de problème avec MockMvc
     */
    @Test
    @WithMockUser
    void getClient() throws Exception {
        // On crée un client en dur (y compris son id - pas de JPA ici)
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);
        // On mocke la méthode métier
        given(serviceClient.recupererClient(0L)).willReturn(client);
        // On appelle la méthode GET
        mvc.perform(get("/api/clients/0")
                        .contentType("application/json;charset=UTF-8")) // précise le content-type
                .andExpect(status().isOk()) // vérifie que tout s'est bien passé
                .andExpect(jsonPath("$.nom", is(client.getNom()))); // vérifie qu'il y a bien des infos
    }

    /**
     * Test de la méthode POST creerClient
     * @throws Exception en cas de problème avec MockMvc
     */
    @Test
    @WithMockUser
    void creerClient() throws Exception {
        // On crée un client en dur (y compris son id - pas de JPA ici)
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);
        // On mocke la méthode métier
        given(serviceClient.creerClient("Jean", "Test")).willReturn(client);
        // On appelle la méthode POST
        mvc.perform(post("/api/clients")
                        .with(csrf())
                        .contentType("application/json;charset=UTF-8") // précise le content-type
                        .content("{\"nom\" : \"Test\", \"prenom\":\"Jean\"}")) // précise le contenu envoyé
                .andExpect(status().isOk()) // vérifie que tout s'est bien passé
                .andExpect(jsonPath("$.id", is(0))); // vérifie qu'il y a bien des infos
    }

    /**
     * Test de la méthode POST ouvrirCompte
     * @throws Exception en cas de problème avec MockMvc
     */
    @Test
    @WithMockUser
    void ouvrirCompte() throws Exception {
        // On crée un client en dur (y compris son id - pas de JPA ici)
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);
        // On crée un compte en dur (y compris son id - pas de JPA ici)
        Compte compte = new Compte();
        compte.setId(1L);
        compte.setClient(client);
        compte.setSolde(1000);
        compte.setDateInterrogation(null);
        // On mocke les méthodes métier
        given(serviceClient.recupererClient(0L)).willReturn(client);
        given(serviceCompte.ouvrir(0L, 1000)).willReturn(compte);
        // On appelle la méthode POST
        mvc.perform(post("/api/clients/0/comptes")
                        .with(csrf())
                        .contentType("application/json;charset=UTF-8") // précise le content-type
                        .content("{\"solde\" : 1000}")) // précise le contenu envoyé
                .andExpect(status().isOk()) // vérifie que tout s'est bien passé
                .andExpect(jsonPath("$.id", is(1))); // vérifie qu'il y a bien des infos
    }

    /**
     * Test de la méthode GET listerComptes
     * @throws Exception en cas de problème avec MockMvc
     */
    @Test
    @WithMockUser
    void listerComptes() throws Exception {
        // On crée un client en dur (y compris son id - pas de JPA ici)
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);
        // On crée un compte en dur (y compris son id - pas de JPA ici)
        Compte compte = new Compte();
        compte.setId(1L);
        compte.setClient(client);
        compte.setSolde(1000);
        compte.setDateInterrogation(null);
        // On doit gérer la liste nous-mêmes (pas de JPA ici)
        List<Compte> compteList = Arrays.asList(compte);
        client.setComptes(compteList);
        // On mocke la méthode métier
        given(serviceClient.recupererClient(0L)).willReturn(client);
        // On appelle la méthode GET
        mvc.perform(get("/api/clients/0/comptes")
                        .contentType("application/json;charset=UTF-8")) // précise le content-type
                .andExpect(status().isOk()) // vérifie que tout s'est bien passé
                .andExpect(jsonPath("$", hasSize(1))) // vérifie qu'il y a bien des infos dans la liste
                .andExpect(jsonPath("$[0].id", is(1))); // vérifie qu'il y a bien des infos
    }

}