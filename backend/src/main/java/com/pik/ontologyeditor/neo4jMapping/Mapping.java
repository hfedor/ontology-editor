package com.pik.ontologyeditor.neo4jMapping;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;

import com.pik.ontologyeditor.neo4jMapping.Property;

public class Mapping {

    private final Driver driver;

    public Mapping( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    public List<Node> GetRoots(){

        List<Node> roots = new ArrayList<Node>();

        try ( Session session = driver.session() )
        {
            String ids_s = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "MATCH (root)" +
                            " WHERE NOT ()-->(root) AND size((root)-->()) > 0" +
                            " return ID(root)");

                    List<org.neo4j.driver.Record> records = result.list();

                    String s = "";

                    for ( org.neo4j.driver.Record r : records)
                        s += r.values();

                    return s;
                }
            } );

            List<Integer> ids = GetIDsFromString(ids_s);
            for(Integer i : ids)
                roots.add(GetNodeByID(i));
        }
        return roots;
    }

    public List<Node> GetChildrenByParentsID(int ID){

        List<Node> children = new ArrayList<Node>();

        try ( Session session = driver.session() )
        {
            String ids_s = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "MATCH (p)--(c)\n" +
                            " WHERE (p)-->(c) AND ID(p) = $ID\n" +
                            " return ID(c)",
                            parameters( "ID", ID ) );;

                    List<org.neo4j.driver.Record> records = result.list();

                    String s = "";

                    for ( org.neo4j.driver.Record r : records)
                        s += r.values();

                    return s;
                }
            } );

            List<Integer> ids = GetIDsFromString(ids_s);
            for(Integer i : ids)
                children.add(GetNodeByID(i));
        }
        return children;
    }

    public String UpdatePropertyValue(int ID, String name, String value){
        try ( Session session = driver.session() )
        {
            String str = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "MATCH (n) WHERE ID(n) = "+ID+"\n" +
                                    " SET n."+name+" = \""+value+"\"\n" +
                                    " RETURN n");;
                    return result.single().get(0).toString();
                }
            } );
            System.out.print(str);
            return str;
        }
    }

    public String UpdatePropertyName(int ID, String old_name, String new_name){
        try ( Session session = driver.session() )
        {
            String str = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "MATCH (n) WHERE ID(n) = "+ID+" AND EXISTS(n."+old_name+") AND NOT EXISTS(n."+new_name+")\n" +
                            "SET n."+new_name+"=n."+old_name+" \n" +
                            "REMOVE n."+old_name+"\n" +
                            "RETURN n;");;
                    return result.single().get(0).toString();
                }
            } );
            System.out.print(str);
            return str;
        }
    }

    public Property GetPropertyFromString( String s, AtomicInteger iter)
    {
        String name = "";
        String value = "";

        for (; iter.get() < s.length(); iter.incrementAndGet())
        {
            char c = s.charAt(iter.get());

            if(c == ':')
                break;
            else
                name += c;
        }
        iter.set(iter.get() + 3);

        for (; iter.get() < s.length(); iter.incrementAndGet())
        {
            char c = s.charAt(iter.get());

            if(c == '"')
                break;
            else
                value += c;
        }

        iter.set(iter.get() + 2);

        Property new_property = new Property(name, value);
        return new_property;
    }

    public List<Integer> GetIDsFromString( String s )
    {
        List<Integer> new_ids  = new ArrayList<Integer>();
        String id = "";

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if(c == '[')
                continue;

            if(c == ']')
            {
                new_ids.add(Integer.parseInt(id));
                id = "";

                continue;
            }

            id += c;
        }
        return new_ids;
    }

    public List<String> GetLabelsFromString( String s)
    {
        List<String> new_labels  = new ArrayList<String>();
        String label = "";

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if(c == '[' || c == ',' || c == ' ')
                continue;

            if(c == ']')
                break;

            if(c == '"')
            {
                if (label != "")
                {
                    new_labels.add(label);
                    label = "";

                }

                continue;
            }

            label += c;
        }
        return new_labels;
    }

    public Node GetNodeByID(int ID){
        try ( Session session = driver.session() )
        {
            String label = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "MATCH (n) WHERE ID(n) = $ID RETURN LABELS(n)",
                            parameters( "ID", ID ) );
                    return result.single().get( 0 ).toString();
                }
            } );

            List<String> new_labels = GetLabelsFromString(label);

            String properties = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "MATCH (n) WHERE ID(n) = $ID RETURN PROPERTIES(n)",
                            parameters( "ID", ID ) );
                    return result.single().get( 0 ).toString();
                }
            } );

            String properties_s = properties;
            List<Property> properties_list = new ArrayList<Property>();
            AtomicInteger i = new AtomicInteger(1);
            for (; i.get() < properties_s.length(); i.incrementAndGet() )
            {
                char c = properties_s.charAt(i.get());

                if (c == ',' || c == ' ')
                    continue;

                Property new_property;
                new_property = GetPropertyFromString(properties_s, i);
                properties_list.add(new_property);
            }

            Node new_node = new Node(ID, new_labels, properties_list);
            return new_node;
        }
    }

    public List<Integer> GetRelatedByParentsID(int ID){

        List<Integer> related = new ArrayList<Integer>();

        try ( Session session = driver.session() )
        {
            String ids_s = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "MATCH (p)--(r)\n" +
                                    " WHERE ((p)-->(r) OR (p)<--(r)) AND ID(p) = $ID\n" +
                                    " return ID(r)",
                            parameters( "ID", ID ) );;

                    List<org.neo4j.driver.Record> records = result.list();

                    String s = "";

                    for ( org.neo4j.driver.Record r : records)
                        s += r.values();

                    return s;
                }
            } );

            related = GetIDsFromString(ids_s);
        }
        return related;
    }

    public String NodesToJSON(List<Node> nodes)
    {
        String json = new Gson().toJson(nodes);

        System.out.print(json);
        return json;
    }
}