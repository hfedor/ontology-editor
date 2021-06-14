import React, { Component } from "react";

import {Table, Button} from 'react-bootstrap';

import "./styles/NodeDiv.css"

import ReadNodesService from "../services/read-nodes.service";

import axios from 'axios';

export default class NodeListElem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            id: this.props.id,
            node: undefined,
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ id: nextProps.id });
        axios.get("http://localhost:8080/node?id=" + nextProps.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
    }

    componentDidMount(){
        console.log("NodeListElem.componentDidMount")
        axios.get("http://localhost:8080/node?id=" + this.state.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
    }

    componenDidUbdate(){
        console.log("NodeListElem.componenDidUbdate")
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
                            {this.state.node ? <div id={"node_elem_title"}>Node {this.state.id}</div> : <label>Brak Noda</label>}
                            <Button id={"go_to_node_button"} variant="outline-primary" onClick={ () => handleToUpdate(this.state.id)}>Przejdź do Noda</Button>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        this.state.node ?
                            <tr align="center">
                                {<td>
                                    <div id={"labels_label"}>Label'e: </div>
                                    {this.state.node.label.map((label, i) => {
                                        return (<div className={"label_div"}>{label}</div>)
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
                                {<td>
                                    <div id={"properties_label"}>Propety'sy: </div>
                                    {this.state.node.properties.map((property, i) => {
                                        return (
                                            <div className={"property_div"}>
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
