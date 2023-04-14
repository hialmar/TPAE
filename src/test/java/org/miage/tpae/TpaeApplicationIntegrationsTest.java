package org.miage.tpae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.metier.ServiceClient;
import org.miage.tpae.metier.ServiceCompte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests créés à partir de https://www.baeldung.com/spring-boot-testing
 * Attention ici on est resté avec Junit5 donc les configurations sont un peu différentes
 *
 * Ici, on a les tests d'intégration qui utilisent tout le logiciel
 * Rien n'est mocké dans l'application, mais on utilise comme BD H2 en mémoire
 * Cf. le fichier src/test/resources/application-integrationtest.properties
 *
 * Note : la plupart de ces tests ne fonctionnent que via Maven puisqu'ils utilisent le plugin Spring Boot
 * Pour voir un exemple de test n'utilisant pas Spring Boot : src/test/java/org.miage.tpae/ServiceClientUnitTest.java
 * Le test ci-dessous fonctionne aussi directement, car on utilise toute l'application.
 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TpaeApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
class TpaeApplicationIntegrationsTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServiceClient serviceClient;

    @Autowired
    private ServiceCompte serviceCompte;

    private Client client;
    private Compte compte;

    @BeforeEach
    void setUp() {
        // on s'arrange pour avoir un client, un compte et une opération déjà créés
        // note si les client existait déjà on le récupère donc pas de problème
        client = serviceClient.creerClient("Jean", "Dupond");
        assertNotNull(client.getId());
        // ceci crée un compte et son opération d'ouverture
        compte = serviceCompte.ouvrir(client.getId(), 1000);
    }

    @Test
    @WithMockUser
    void getClient() throws Exception {
        mvc.perform(get("/api/clients/{id}", client.getId())
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is(client.getNom())));
    }

    @Test
    @WithMockUser
    void creerClient() throws Exception {
        mvc.perform(post("/api/clients")
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"nom\" : \"Test\", \"prenom\":\"Jean\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", isA(Integer.class)))
                .andExpect(jsonPath("$.prenom", is("Jean")))
                .andExpect(jsonPath("$.nom", is("Test")));
    }

    @Test
    @WithMockUser
    void ouvrirCompte() throws Exception {
        mvc.perform(post("/api/clients/{id}/comptes", client.getId())
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"solde\" : 1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", isA(Integer.class)))
                .andExpect(jsonPath("$.solde", is(1000.0)));
    }

    @Test
    @WithMockUser
    void listerComptes() throws Exception {
        mvc.perform(get("/api/clients/{id}/comptes", client.getId())
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()))
                .andExpect(jsonPath("$[0].id", isA(Integer.class)))
                .andExpect(jsonPath("$[0].solde", isA(Double.class)));
    }

}