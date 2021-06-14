import React, { Component } from "react";

import {Table, ButtonGroup, ToggleButton} from 'react-bootstrap';

import "./styles/NodeEditor.css"

import PropertyEditor from './PropertyEditor'

export default class NodeEditor extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            property_name: props.property_name,
            property_value: props.property_value,
            radioValue: 1,
            id: props.id,
        };
        this.handleUpdate = this.handleUpdate.bind(this);
        this.handleToUpdate = this.handleToUpdate.bind(this);
    }

    handleUpdate(){
        this.forceUpdate();
    }

    handleToUpdate(){
        this.props.handleToUpdate();
        this.handleUpdate();
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ property_name: nextProps.property_name });
        this.setState({ property_value: nextProps.property_value });
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
                            onChange={(e) => {this.state.radioValue = e.currentTarget.value; this.handleUpdate()}}
                        >
                            {radio.name}
                        </ToggleButton>
                    ))}
                </ButtonGroup>
                {
                    this.state.radioValue == 1 ?
                        <PropertyEditor
                            name={this.state.property_name}
                            value={this.state.property_value}
                            id={this.state.id}
                        /> :
                        <div></div>
                }
            </div>
        );
    }
}
