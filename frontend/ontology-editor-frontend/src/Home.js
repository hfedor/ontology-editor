import React, { Component } from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';

class Home extends Component {
    render() {
        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <h1 className="text-center" style={{marginBottom: "100px"}}>Ontology Editor</h1>
                    <Button color="success" size="lg" block tag={Link} to="/ontologies">Manage ontologies</Button>
                    <Button color="success" size="lg" block tag={Link} to="#">Load new ontology</Button>
                </Container>
            </div>
        );
    }
}

export default Home;