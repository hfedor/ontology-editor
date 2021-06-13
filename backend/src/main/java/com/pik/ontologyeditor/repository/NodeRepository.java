package com.pik.ontologyeditor.repository;

import com.google.gson.Gson;
import com.pik.ontologyeditor.neo4jMapping.Mapping;
import com.pik.ontologyeditor.neo4jMapping.Node;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public interface NodeRepository {
    @GetMapping("/node_by_id")
    public default Node nodeById(@RequestParam(value = "id", defaultValue = "206") Integer id)  {
        Mapping mapping = new Mapping("bolt://localhost:7687", "neo4j", "koperwas123");

        return mapping.GetNodeByID(id);
    }
}

