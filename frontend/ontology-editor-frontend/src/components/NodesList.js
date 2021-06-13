import React, { Component } from "react";

import {Table} from 'react-bootstrap';
import NodeListElem from './NodeListElem';

import './styles/NodesList.css';

import axios from 'axios';

export default class NodesList extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            id: props.id,
            ids: [],
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ id: nextProps.id });
        axios.get("http://localhost:8080/related?id=" + nextProps.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({ids: data});
            });
        console.log("NodesList.ids: "+ this.state.ids);
    }

    componentDidMount(){
        axios.get("http://localhost:8080/related?id=" + this.state.id)
            .then(response => response.data)
            .then((data) => {
                this.setState({ids: data});
            });
    }

    render() {
        var handleToUpdate = this.props.handleToUpdate;
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
                        this.state.ids && this.state.ids.length > 0 ?
                            <tr align="center">
                                {<td>{this.state.ids.map((id, i) => {
                                    return (<NodeListElem id={id} handleToUpdate={handleToUpdate}/>)
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
