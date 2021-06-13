package com.pik.ontologyeditor.controller;

import com.pik.ontologyeditor.neo4jMapping.Mapping;
import com.pik.ontologyeditor.neo4jMapping.Node;

import com.pik.ontologyeditor.neo4jMapping.Property;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:8081")
public class NodeAPIController {

    private static final Gson gson = new Gson();

    @GetMapping("/node")
    public Node getNode(@RequestParam Integer id) {
        Mapping mapping = new Mapping("bolt://localhost:7687", "neo4j", "koperwas123");
        Node node = mapping.GetNodeByID(id);
        return node;
    }

    @GetMapping("/related")
    public List<Integer> getIds(@RequestParam Integer id) {
        Mapping mapping = new Mapping("bolt://localhost:7687", "neo4j", "koperwas123");
        return mapping.GetRelatedByParentsID(id);
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}