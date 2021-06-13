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
//@CrossOrigin("http://localhost:8081")
public class NodeAPIController {

    private static final Gson gson = new Gson();

    @GetMapping("/node")
    public Node getNode(@RequestParam String id) {
//        Mapping mapping = new Mapping("bolt://localhost:7687", "neo4j", "koperwas123");
//        Node node = mapping.GetNodeByID(node_id);
//        String serialized_node = gson.toJson(node);
//        return node;
        List<String> arr = new ArrayList<String>();
        List<Property> arr2 = new ArrayList<Property>();
        arr.add("hello");
        arr.add("hej");
        arr2.add(new Property("name1", "value1"));
        arr2.add(new Property("name2", "value2"));
        return new Node(12, arr, arr2 );
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}