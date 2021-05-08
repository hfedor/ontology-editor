package com.pik.ontologyeditor.web;

import com.pik.ontologyeditor.model.Ontology;
import com.pik.ontologyeditor.model.OntologyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OntologyController {

    private final Logger logger = LoggerFactory.getLogger(OntologyController.class);
    private final OntologyRepository ontologyRepository;

    public OntologyController(OntologyRepository ontologyRepository) {
        this.ontologyRepository = ontologyRepository;
    }

    @GetMapping("/ontologies")
    Collection<Ontology> ontologies() {
        return ontologyRepository.findAll();
    }

    @GetMapping("/ontology/{id}")
    ResponseEntity<?> getOntology(@PathVariable Long id) {
        Optional<Ontology> ontology = ontologyRepository.findById(id);
        return ontology.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/ontology")
    ResponseEntity<Ontology> createOntology(@Valid @RequestBody Ontology ontology) throws URISyntaxException {
        logger.info("Request to create an ontology: {}", ontology);
        Ontology resultOntology = ontologyRepository.save(ontology);
        return ResponseEntity.created(new URI("/api/ontology/" + resultOntology.getId()))
                .body(resultOntology);
    }

    @PutMapping("/ontology/{id}")
    ResponseEntity<Ontology> updateOntology(@Valid @RequestBody Ontology ontology) {
        logger.info("Request to update the ontology: {}", ontology);
        Ontology resultOntology = ontologyRepository.save(ontology);
        return ResponseEntity.ok(resultOntology);
    }

    @DeleteMapping("/ontology/{id}")
    public ResponseEntity<?> deleteOntology(@PathVariable Long id) {
        logger.info("Request to delete the ontology: {}", id);
        ontologyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

