import React, { Component } from "react";

import {Table, ButtonGroup, ToggleButton} from 'react-bootstrap';

import "./styles/NodeEditor.css"

export default class NodeEditor extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            radioValue: 1
        };
    }

    render() {

        const radios = [
            { name: 'Edytuj', value: '1' },
            { name: 'Tłumacz', value: '2' },
        ];
        return (
            <div className={"node_editor"}>
                <ButtonGroup toggle>
                    {radios.map((radio, idx) => (
                        <ToggleButton
                            key={idx}
                            type="radio"
                            variant="outline-primary"
                            name="radio"
                            value={radio.value}
                            checked={this.state.radioValue === radio.value}
                            onChange={(e) => {this.state.radioValue = e.currentTarget.value; this.forceUpdate()}}
                        >
                            {radio.name}
                        </ToggleButton>
                    ))}
                </ButtonGroup>

            </div>
        );
    }
}
