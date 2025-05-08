package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.jboss.logging.Logger;


@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

    // Déclaration du champ personRepository
       private final PersonRepository personRepository;

    // Logger pour la classe
    private static final Logger LOG = Logger.getLogger(PersonResource.class);
    
  // Constructeur pour l'injection
  public PersonResource(PersonRepository personRepository) {
    this.personRepository = personRepository;
}

    @GET
    public List<Person> getAll() {
        // Récupérer tous les enregistrements de la table Person
        // et les retourner sous forme de liste
        // en utilisant la méthode listAll() de Panache
        LOG.info("GET Person List ");
        return personRepository.listAll();
    }

    @POST
    @Transactional
    public Response add(Person person) {
        boolean exists = personRepository.find("name", person.name).firstResult() != null;
        if (exists) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Person with this name already exists.")
                           .build();
        }
        personRepository.persist(person);
         return Response.status(Response.Status.CREATED).entity(person).build();
    }



@DELETE
@Path("/{id}")
@Transactional
public Response delete(@PathParam("id") Long id) {
    Person person = personRepository.findById(id);
    if (person == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Person not found").build();
    }
    personRepository.delete(person);
    return Response.status(Response.Status.NO_CONTENT).build(); // Code HTTP 204 : No Content
}

@DELETE
@Path("/name/{name}")
@Transactional
public Response deleteByName(@PathParam("name") String name) {
    List<Person> persons = personRepository.find("name", name).list(); // Trouve toutes les personnes avec ce nom
    if (persons.isEmpty()) {
        return Response.status(Response.Status.NOT_FOUND).entity("No persons found with name: " + name).build();
    }
    personRepository.delete("name", name); // Supprime toutes les personnes avec ce nom
    return Response.status(Response.Status.NO_CONTENT).build(); // Code HTTP 204 : No Content
}



@PUT
@Path("/{id}")
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response updatePerson(@PathParam("id") Long id, Person updatedPerson) {
    Person person = personRepository.findById(id); // Recherche la personne par ID
    if (person == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Person not found with ID: " + id).build();
    }

    // si il a un doublon, alors sortir en erreur
    boolean exists = personRepository.find("name", updatedPerson.name).firstResult() != null;
    if (exists) {
        return Response.status(Response.Status.CONFLICT)
                       .entity("Person with this name already exists.")
                       .build();
    }

    // Met à jour les champs de la personne
    person.name = updatedPerson.name != null ? updatedPerson.name : person.name; // Si le nom est fourni, on le met à jour
    person.birthdate = updatedPerson.birthdate != null ? updatedPerson.birthdate : person.birthdate; // Si la date est fournie, on la met à jour
    person.profilname = updatedPerson.profilname != null ? updatedPerson.profilname : person.profilname; // Si le profil est fourni, on le met à jour



    // Sauvegarde les modifications
    personRepository.persist(person);

    return Response.ok(person).build(); // Retourne la personne mise à jour
}

@PUT
@Path("/name/{name}")
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response updateByName(@PathParam("name") String name, Person updatedPerson) {
    Person person = personRepository.find("name", name).firstResult(); // Trouve la première personne avec ce nom
    if (person == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Person not found with name: " + name).build();
    }

    // si il a un doublon, alors sortir en erreur
    boolean exists = personRepository.find("name", updatedPerson.name).firstResult() != null;
    if (exists) {
        return Response.status(Response.Status.CONFLICT)
                       .entity("Person with this name already exists.")
                       .build();
    }

    // Met à jour les champs de la personne
    person.name = updatedPerson.name != null ? updatedPerson.name : person.name;
    person.birthdate = updatedPerson.birthdate != null ? updatedPerson.birthdate : person.birthdate;

    personRepository.persist(person); // Sauvegarde les modifications
    return Response.ok(person).build(); // Retourne la personne mise à jour
}



@GET
@Path("/export/csv")
public Response exportPersonsToCSV() {
    List<Person> persons = personRepository.listAll();

    // Générer l'horodatage
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String filename = "persons_" + timestamp + ".csv";

    StreamingOutput stream = out -> {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

        // En-tête CSV
        writer.println("id,name,birthdate");

        // Corps du CSV
        for (Person person : persons) {
            writer.printf("%d,%s,%s%n",
                person.id,
                person.name != null ? person.name : "",
                person.birthdate != null ? person.birthdate.toString() : ""
            );
        }
        writer.flush();
    };

    return Response.ok(stream, "text/csv")
            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
            .build();
}


}