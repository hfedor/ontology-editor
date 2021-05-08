import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import OntologyList from './OntologyList';
import OntologyEdit from "./OntologyEdit";

class App extends Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={Home}/>
                    <Route path='/ontologies' exact={true} component={OntologyList}/>
                    <Route path='/ontologies/:id' component={OntologyEdit}/>
                </Switch>
            </Router>
        )
    }
}

export default App;
