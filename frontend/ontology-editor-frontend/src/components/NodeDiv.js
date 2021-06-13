import React, { Component } from "react";

import {Card, Table, Image, ButtonGroup, Button, InputGroup, FormControl} from 'react-bootstrap';

import "./styles/NodeDiv.css"

import ReadNodesService from "../services/read-nodes.service";

import axios from 'axios';

export default class NodeDiv extends Component {
    constructor(props) {
        super(props);

        this.state = {
            id: props.id,
            node: undefined,
        };
    }

    componentDidMount(){
        axios.get("http://localhost:8080/node?id=" + this.state.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
    }

    render() {
        return (
            <div className={"node_div"}>
                <Table bordered hover striped varian="dark">
                    <thead>
                        <tr>
                            <th>
                                {this.state.node ? <label>Node {this.state.node.id}</label> : <label>Brak Noda</label>}
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
