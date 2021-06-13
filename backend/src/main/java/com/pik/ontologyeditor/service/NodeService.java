package com.pik.ontologyeditor.service;

import com.pik.ontologyeditor.neo4jMapping.Node;

public interface NodeService {
    public Node saveNode(Node node);
    public Node findNodeById(Integer id);
}
