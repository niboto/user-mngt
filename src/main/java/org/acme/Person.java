package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;




/**
 * Example JPA entity defined as a Panache Entity.
 * An ID field of Long type is provided, if you want to define your own ID field extends <code>PanacheEntityBase</code> instead.
 *
 * This uses the active record pattern, you can also use the repository pattern instead:
 * .
 *
 * Usage (more example on the documentation)
 *
 * {@code
 *     public void doSomething() {
 *         MyEntity entity1 = new MyEntity();
 *         entity1.field = "field-1";
 *         entity1.persist();
 *
 *         List<MyEntity> entities = MyEntity.listAll();
 *     }
 * }
 */
@Entity
@Table(name = "person", uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = "name"))
public class Person extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // compatible MariaDB
    public Long id;
    
    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public LocalDate birthdate;

    @Column(nullable = false)
    public String profilname;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Format ISO 8601 pour la création
    public LocalDateTime createdAt;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Format ISO 8601 pour la création
    public LocalDateTime updatedAt;


   // Méthode appelée automatiquement avant insertion
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }


       @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    
}
