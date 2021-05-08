import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class OntologyList extends Component {

    constructor(props) {
        super(props);
        this.state = {ontologies: [], isLoading: true};
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        this.setState({isLoading: true});

        fetch('api/ontologies')
            .then(response => response.json())
            .then(data => this.setState({ontologies: data, isLoading: false}));
    }

    async remove(id) {
        await fetch(`/api/ontology/${id}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(() => {
            let updatedOntologies = [...this.state.ontologies].filter(i => i.id !== id);
            this.setState({ontologies: updatedOntologies});
        });
    }

    render() {
        const {ontologies, isLoading} = this.state;
        if (isLoading) {
            return <p>Loading...</p>
        }

        const ontologyList = ontologies.map(ontology => {
            return <tr key={ontology.id}>
                <td style={{whiteSpace: 'nowrap'}}>{ontology.name}</td>
                <td>{ontology.description}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/ontologies/" + ontology.id}>Edit</Button>
                        <Button size="sm" color="danger" onClick={() => this.remove(ontology.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <div className="float-right">
                        <Button color="success" tag={Link} to="/ontologies/new">Add ontology manually</Button>
                    </div>
                    <h3>Ontologies</h3>
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th width="20%">Name</th>
                            <th width="70%">Description</th>
                        </tr>
                        </thead>
                        <tbody>
                        {ontologyList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}

export default OntologyList;