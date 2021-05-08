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
                    <h3 className="text-center">Welcome to the Ontology Editor</h3>
                    <Button color="primary"><Link to="/ontologies">Manage your ontologies</Link></Button>
                </Container>
            </div>
        );
    }
}

export default Home;