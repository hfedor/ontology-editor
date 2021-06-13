import React, { Component } from "react";

import SearchInput, {createFilter} from 'react-search-input'

import {Button} from 'react-bootstrap';

import NodesList from './NodesList';
import NodeDiv from './NodeDiv';

import "./styles/SearchByID.css"

import axios from 'axios';

export default class SearchByID extends React.Component {
    constructor (props) {
        super(props)
        this.state = {
            searchID: 206,
            searchIDInput: 206,
        };
        this.searchUpdate = this.searchUpdate.bind(this);
        this.searchSetID = this.searchSetID.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.changeSearchInput = this.changeSearchInput.bind(this);
        this.clickSearchButton = this.clickSearchButton.bind(this);
    }

    componentWillReceiveProps(nextProps) {
        this.setState({ searchID: nextProps.searchID });
    }

    handleUpdate = () => {
        this.forceUpdate();
    };

    render () {

        return (
            <div>
                <div className={"search_by_id"}>
                    <div id={"search_label"}>Wyszukiwanie po ID</div>
                    <SearchInput id="search-input" onChange={this.changeSearchInput} />
                    <Button id={"search_button"} variant="outline-primary" onClick={this.clickSearchButton}>Szukaj</Button>
                </div>
                <NodeDiv id={this.state.searchID} />
                <NodesList id={this.state.searchID} handleToUpdate={this.searchUpdate.bind(this)}/>
            </div>
        )
    }

    searchSetID(term) {
        this.setState({searchID: term});
    }

    searchUpdate(term){
        this.setState({searchID: term});
        this.handleUpdate();
    }

    changeSearchInput(term){
        this.setState({searchIDInput: term});
    }

    clickSearchButton(){
        this.setState({searchID: this.state.searchIDInput});
    }
}
