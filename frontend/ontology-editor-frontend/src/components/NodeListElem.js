import React, { Component } from "react";

import {Table, Button} from 'react-bootstrap';

import "./styles/NodeDiv.css"

import ReadNodesService from "../services/read-nodes.service";

import axios from 'axios';

export default class NodeListElem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            id: props.id,
            node: undefined,
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ id: nextProps.id });
        this.setState({ node: undefined });
        console.log("NodeListElem::componentWillReceiveProps: this.state.id="+nextProps.id);
        axios.get("http://localhost:8080/node?id=" + nextProps.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
        console.log("NodeListElem::componentWillReceiveProps: this.state.node="+this.state.node);
    }

    componentDidMount(){
        console.log("NodeListElem::componentDidMount: this.state.id="+this.state.id);
        this.setState({ node: undefined });
        axios.get("http://localhost:8080/node?id=" + this.state.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
    }

    render() {
        var handleToUpdate = this.props.handleToUpdate;
        return (
            <div className={"node_div"}>
                <Table bordered hover striped varian="dark">
                    <thead>
                    <tr>
                        <th>
                            {this.state.node ? <label>Node {this.state.id}</label> : <label>Brak Noda</label>}
                            <Button variant="outline-primary" onClick={ () => handleToUpdate(this.state.id)}>Przejdź do Noda</Button>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        this.state.node ?
                            <tr align="center">
                                {<td>{this.state.node.label.map((label, i) => {
                                    return (<div>{label}</div>)
                                })}
                                </td>}
                            </tr> :
                            <tr align="center">
                                <td>Brak Nodów</td>
                            </tr>
                    }
                    {
                        this.state.node ?
                            <tr align="center">
                                {<td>{this.state.node.properties.map((property, i) => {
                                    return (
                                        <div>
                                            {property.name}: {property.value}
                                        </div>)
                                })}
                                </td>}
                            </tr> :
                            <tr align="center">
                                <td>Brak Nodów</td>
                            </tr>
                    }
                    </tbody>
                </Table>
            </div>
        );
    }
}
