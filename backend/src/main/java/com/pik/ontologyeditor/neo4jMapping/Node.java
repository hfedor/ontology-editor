package com.pik.ontologyeditor.neo4jMapping;

import java.util.List;

public class Node {

    private int ID = -1;
    private List<String> labels = null;
    private List<Property> properties = null;

    public Node(){}

    public Node(int id, List<String> labels) {
        ID = id;
        this.labels = labels;
    }

    public Node(int id, List<String> labels, List<Property> properties) {
        ID = id;
        this.labels = labels;
        this.properties = properties;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<String> getLabel() {
        return labels;
    }

    public void setLabel(List<String> labels) {
        this.labels = labels;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Node{" +
                "ID=" + ID +
                ", labels='" + labels.toString() + '\'' +
                ", properties=" + properties.toString() +
                '}';
    }
}
