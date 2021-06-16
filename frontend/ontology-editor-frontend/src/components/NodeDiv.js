import React, { Component } from "react";

import {Table, Button} from 'react-bootstrap';

import "./styles/NodeDiv.css"

import NodeEditor from "./NodeEditor";

import axios from 'axios';

export default class NodeDiv extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            id: props.id,
            node: undefined,
            edited_property: undefined,
        };

        this.setEditedProperty = this.setEditedProperty.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
    }

    handleUpdate = () => {
        this.forceUpdate();
    };

    setEditedProperty(property){
        this.setState({edited_property : property});
        console.log(this.state.edited_property);
        this.handleUpdate();
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ id: nextProps.id });
        this.setState({ edited_property: nextProps.edited_property });
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
                <Table className={"node_table"} bordered hover striped varian="dark">
                    <thead>
                        <tr>
                            <th>
                                {this.state.node ? <label>Node {this.state.node.id}</label> : <label>Brak Noda</label>}
                                <Button variant="outline-primary" icon="refresh" onClick={() => {this.handleUpdate()}}>
                                    Odśwież
                                </Button>
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
                                (<tr align="center">
                                    {<td>
                                        <div id={"properties_label"}>Propety'sy: </div>
                                        {this.state.node.properties.map((property, i) => {
                                        return (
                                            <div class={"property_button_div"}>
                                                <Button  variant="outline-primary"
                                                        onClick={() => this.setEditedProperty(property)}
                                                >
                                                    {property.name}: {property.value}
                                                </Button>
                                            </div>
                                        )
                                    })}
                                    </td>}
                                </tr>
                                ) :
                                <tr align="center">
                                    <td>Brak Nodów</td>
                                </tr>
                        }
                        {
                            this.state.edited_property ?
                                (<tr align="center">
                                        {<td>
                                            <NodeEditor
                                                property_name={this.state.edited_property.name}
                                                property_value={this.state.edited_property.value}
                                                handleToUpdate={this.handleUpdate}
                                                id={this.state.id}
                                            />
                                        </td>}
                                    </tr>
                                ) :
                                <tr align="center">
                                </tr>
                        }
                    </tbody>
                </Table>
            </div>
        );
    }
}
