package com.pik.ontologyeditor.controller;

import com.pik.ontologyeditor.message.ResponseMessage;
import com.pik.ontologyeditor.model.FileInfo;
import com.pik.ontologyeditor.neo4jMapping.Mapping;
import com.pik.ontologyeditor.neo4jMapping.Node;
import com.pik.ontologyeditor.service.NodeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.pik.ontologyeditor.service.NodeService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@CrossOrigin("http://localhost:8081")
public class NodeController {

    private static String createNodeUrl;
    private static RestTemplate restTemplate;
    private static String updateNodeUrl;
    private static HttpHeaders headers;
    private static JSONObject nodeJsonObject;
    Mapping mapping;
    private NodeService nodeService;

    @PostMapping("/node")
    public ResponseEntity<Node> GetNodeById(@RequestParam("id") int id){
        return ResponseEntity.status(HttpStatus.OK).body(mapping.GetNodeByID(id));
    }

    @PostMapping("/node_id")
    public ResponseEntity<String> GetIDById(@RequestParam("id") int id){
        Node node = mapping.GetNodeByID(id);

        Integer nodes_id = node.getID();

        return ResponseEntity.status(HttpStatus.OK).body(nodes_id.toString());
    }

    @PostMapping(value = "/node2", consumes = "application/json", produces = "application/json")
    public Node createNode(@RequestBody Node node){
        return nodeService.saveNode(node);
    }


    @PostMapping(
            value = "/updateNode", consumes = "application/json", produces = "application/json")
    public Node updatePerson(@RequestBody Node node, HttpServletResponse response) {
        response.setHeader("Location", ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/findNode/" + node.getID()).toUriString());

        return nodeService.saveNode(node);
    }

    @BeforeClass
    public static void runBeforeAllTestMethods() throws JSONException {
        createNodeUrl = "http://localhost:8082/spring-rest/createNode";
        updateNodeUrl = "http://localhost:8082/spring-rest/updateNode";

        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        nodeJsonObject = new JSONObject();
        nodeJsonObject.put("id", 1);
        nodeJsonObject.put("name", "John");
    }
}
