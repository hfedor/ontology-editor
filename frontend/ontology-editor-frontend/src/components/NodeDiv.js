import React, { Component } from "react";
import GetNode from "../services/GetNode";

import './NodeDiv.css';

export default class NodeDiv extends Component {

    constructor(props) {
        super(props);
        this.state = {
            node_id: GetNode.getId(206)
        };
    }

    render() {
        const {
            node_id,
        } = this.state;

        return (
            <div className={"node_div"}>
                Node
                {node_id => (
                            {node_id}
                    )
                }
            </div>
        )
    }
}