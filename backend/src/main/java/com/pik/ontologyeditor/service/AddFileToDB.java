package com.pik.ontologyeditor.service;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;

public class AddFileToDB implements AutoCloseable
{
    private final Driver driver;

    public AddFileToDB( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void printGreeting()
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "Match (n:Greeting) return properties(n), ID(n) ");
                    return result.single().get(0).toString();
                }
            } );
        }
    }

    public void AddFile( final String path, final String format )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "CALL n10s.onto.import.fetch($path,$format)",
                            parameters( "path", path, "format", format ) );
                    return result.single().get( 0 ).asString();
                }
            } );
        }
    }


}