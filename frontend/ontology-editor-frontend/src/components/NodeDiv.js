import React, { Component } from "react";

import "./styles/NodeDiv.css"

export default class NodeDiv extends Component {
    constructor(props) {
        super(props);

        this.state = {
            labels: [],
            paremeters: [],
        };
    }

    render() {
        return (
            <div className={"node_div"}>
                Node
            </div>
        );
    }
}
