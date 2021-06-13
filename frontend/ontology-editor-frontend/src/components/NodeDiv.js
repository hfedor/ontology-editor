import React, { Component } from "react";

import {Table} from 'react-bootstrap';

import "./styles/NodeDiv.css"

import axios from 'axios';

export default class NodeDiv extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            id: props.id,
            node: undefined,
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ id: nextProps.id });
        console.log("NodeDIv.componentWillReceiveProps: "+nextProps.id);
        axios.get("http://localhost:8080/node?id=" + nextProps.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
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
                <div>{this.state.id}</div>
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
