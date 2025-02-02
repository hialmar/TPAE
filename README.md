# TP AE
## TP Banque pour Applications d'Entreprises

Travaux Pratiques sur les Applications d'Entreprises avec Spring

Sujets des TPs sous moodle :
https://moodle-miage-toulouse.westeurope.cloudapp.azure.com/course/view.php?id=22


### Configuration

Ce TP utilise une base de données MySQL qu'on peut créer sous Docker avec la commande :
docker run --name=mysqltp -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=test -p 3306:3306 mysql

Si vous avez des problèmes avec Docker pour Windows on vous recommande WAMP : https://www.wampserver.com/

### Sécurité

La sécurité est désactivée par défaut.
Si vous voulez l'activer, il faut aller dans la classe org.miage.tpae.secu.config.SecurityConfiguration,
dans la méthode securityFilterChain et commenter la ligne suivante :
http.authorizeHttpRequests().requestMatchers("/**").permitAll();

Si cette ligne n'est pas présente, il faut d'abord créer un utilisateur avec :

POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{"firstname": "tutu","lastname": "tutu","email": "tutu@toto.com","password": "password"}

Puis pour s'authentifier :

POST http://localhost:8080/api/v1/auth/authenticate
Content-Type: application/json

{"email": "tutu@toto.com","password": "password"}

Cette requête retourne deux tokens :
{
"access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b3RvQHRvdG8uY29tIiwiaWF0IjoxNzM3NjUwNjY4LCJleHAiOjE3Mzc2NTEyNjh9.PBFNQJ_ocS2zI8ozQ33HsIrkyakXYspm4ly5Ah3fNIE",
"refresh_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b3RvQHRvdG8uY29tIiwiaWF0IjoxNzM3NjUwNjY4LCJleHAiOjE3MzgyNTU0Njh9.s3weYGHE_7lAcwZnwSdqWqmx4c4D1UG0PTAB7kLaqL4"
}

Le premier doit être utilisé en tant que Bearer :

GET http://localhost:8080/api/clients/1
Authorization: Bearer {{auth_token}}

Le second sert pour rafraichir le premier token comme ceci :

POST http://localhost:8080/api/v1/auth/refresh-token
Content-Type: application/json
Authorization: Bearer {{refresh_token}}

Pour les autres fonctionnalités, voir la classe org.miage.tpae.secu.auth.AuthenticationController


### Infos sur les Tests

Les tests ont été créés à partir de https://www.baeldung.com/spring-boot-testing
Attention ici on est resté avec Junit5 donc les configurations sont un peu différentes

Vous avez un exemple de tests unitaires dans le cadre de Spring Boot :
src/test/java/org.miage.tpae/exposition/RestClientTest

Des tests unitaires utilisant uniquement Mockito :
src/test/java/org.miage.tpae/ServiceClientUnitTest.java

Des tests d'intégration avec une base de donnée en mémoire H2 :
src/test/java/org.miage.tpae/metier/ServiceClientTest

Des tests d'intégration avec tout le logiciel :
src/test/java/org.miage.tpae/TpaeApplicationIntegrationsTest

Note : la plupart de ces tests ne fonctionnent que via Maven puisqu'ils utilisent le plugin Spring Boot

---

* [Documentation technique du projet (incluant les Javadocs) ](https://hialmar.github.io/TPAE/)

  
---

# Tutoriels

Ce qui suit est le fichier Readme original (utile pour les liens vers les tutoriels).

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/#build-image)
* [Validation](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#io.validation)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
