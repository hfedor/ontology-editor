import React, { Component } from "react";

import {Button} from 'react-bootstrap';

import './styles/PropertyEditor.css'

import axios from 'axios';

export default class PropertyEditor extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            old_name: props.name,
            old_value: props.value,
            name:   props.name,
            value:  props.value,
            id: props.id,
        };

        this.handleChangeValue = this.handleChangeValue.bind(this);
        this.handleChangeName = this.handleChangeName.bind(this);
        this.updatePropertyValue = this.updatePropertyValue.bind(this);
        this.updatePropertyName = this.updatePropertyName.bind(this);
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ name: nextProps.name });
        this.setState({ value: nextProps.value });
        this.setState({ id: nextProps.id });
    }

    handleChangeValue(event) {
        this.setState({value: event.target.value});
    }

    handleChangeName(event) {
        this.setState({name: event.target.value});
    }

    updatePropertyValue(){
        this.forceUpdate();
        axios.get("http://localhost:8080/update/property/value?id=" + this.state.id
            + "&&name=" + this.state.old_name
            + "&&value=" + this.state.value)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
    }

    updatePropertyName(){
        this.forceUpdate();
        axios.get("http://localhost:8080/update/property/name?id=" + this.state.id
            + "&&old_name=" + this.state.old_name
            + "&&new_name=" + this.state.name)
            .then(response => response.data)
            .then((data) => {
                this.setState({node: data});
            });
    }

    render() {
        return (
            <div id={"property_editor"}>
                <div className={"textarea_div"}>
                    <div>Nazwa właściwości</div>
                    <input className={"property_textarea"}
                       type="textarea"
                       name="property_name"
                       value={this.state.name}
                       onChange={this.handleChangeName}
                    />
                    <div>
                        <Button className={"textarea_button"} variant="outline-primary" block onClick={this.updatePropertyName}>Zmień</Button>
                    </div>
                </div>
                <div className={"textarea_div"}>
                    <div>Wartość właściwości</div>
                    <input className={"property_textarea"}
                       type="textarea"
                       name="property_value"
                       value={this.state.value}
                       onChange={this.handleChangeValue}
                    />
                    <div>
                        <Button className={"textarea_button"} variant="outline-primary" block onClick={this.updatePropertyValue}>Zmień</Button>
                    </div>
                </div>
            </div>
        );
    }
}
