package org.miage.tpae.metier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.utilities.ClientInexistant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceClientUnitTest {

    ClientRepository clientRepository;

    ServiceClient serviceClient;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        serviceClient = new ServiceClient(clientRepository);
    }

    @Test
    void creerClient() {
        // on crée un client
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);
        // on crée une liste vide qui sera renvoyée par findByPrenomAndNom la première fois
        List<Client> listeVide = new ArrayList<>();
        // on crée une liste contenant le client qui sera renvoyée par findByPrenomAndNom la seconde fois
        List<Client> liste = new ArrayList<>();
        liste.add(client);
        // on mocke la méthode save pour qu'elle retourne le client
        when(clientRepository.save(any())).thenReturn(client);
        // on mocke la méthode findByPrenomAndNom pour qu'elle retourne la liste vide puis la liste
        when(clientRepository.findByPrenomAndNom("Jean", "Test")).thenReturn(listeVide).thenReturn(liste);
        // on teste la méthode creerClient
        Client client1 = serviceClient.creerClient("Jean", "Test");
        // on vérifie qu'elle nous renvoie bien le client
        assertEquals(client1, client);
        // on teste à nouveau la méthdoe creerClient
        Client client2 = serviceClient.creerClient("Jean", "Test");
        // on vérifie qu'elle nous renvoie toujours le même client
        assertEquals(client2, client);
        // on vérifie que la méthode save n'a été appelée qu'une fois
        verify(clientRepository, times(1)).save(any());

        verify(clientRepository, times(2)).findByPrenomAndNom("Jean", "Test");
    }

    @Test
    void recupererClient() {
        // crée un client
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);
        // on mocke la méthode findById pour qu'elle renvoie le client si on demande l'id 0
        when(clientRepository.findById(0L)).thenReturn(Optional.of(client));
        // on mocke la méthode findById pour qu'elle renvoie un optionnel vide si on demande l'id 1
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        // on teste la méthode recupererClient pour récupérer le client
        Client client1 = serviceClient.recupererClient(0L);
        // on vérifie que c'est le bon client
        assertEquals(client1, client);
        // on vérifie que la méthode lance une exception ClientInexistant si on demande un autre client
        assertThrows(ClientInexistant.class, ()->serviceClient.recupererClient(1L));
        // on vérifie que la méthode findById a bien été appelée 2 fois
        verify(clientRepository, times(2)).findById(any());
    }
}