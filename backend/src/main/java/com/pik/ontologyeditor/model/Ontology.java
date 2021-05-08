package com.pik.ontologyeditor.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Ontology {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;
    private String description;
}
