import React, { Component } from "react";

import {Card, Table, Image, ButtonGroup, Button, InputGroup, FormControl} from 'react-bootstrap';
import NodeDiv from './NodeDiv';

import './styles/NodesList.css';

import axios from 'axios';

export default class NodesList extends Component {
    constructor(props) {
        super(props);

        this.state = {
            id: props.id,
            ids: [],
        };
    }

    componentDidMount(){
        axios.get("http://localhost:8080/related?id=" + this.state.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({ids: data});
            });
    }

    render() {
        return (
            <div className={"nodes_list"}>
                <Table bordered hover striped varian="dark">
                    <thead>
                    <tr>
                        <th>
                            Nody Powiązane z Nodem {this.state.id}
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        this.state.ids ?
                            <tr align="center">
                                {<td>{this.state.ids.map((id, i) => {
                                    return (<NodeDiv id={id}/>)
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
