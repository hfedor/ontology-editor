package com.pik.ontologyeditor.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OntologyRepository extends JpaRepository<Ontology, Long> {
    Ontology findByName(String name);
}
