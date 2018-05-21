import React, { Component } from 'react';
import Modal from 'react-modal';
import EventBus from './EventBus';
var axios = require('axios');

export default class DeleteEntry extends Component {


	constructor(props) {
	    super(props);
	    this.state = {
    		currentFile: '/',
    	}
	    this.api = axios.create({
		  baseURL: 'http://localhost:8282/api/txt/',
		  timeout: 10000,
		  headers: {
		    'Accept': 'text/plain',
		    'Content-Type': 'text/plain',
		  }
		});

	    this.handleDelete = this.handleDelete.bind(this);
	}

	componentWillMount() {
		const self = this;
		EventBus.addListener('entrySelected', (entryFQN) => {
			self.setState({
				currentFile: entryFQN
			})
		});
	}

	handleDelete() {
		if (window.confirm("Realy delete File "+this.state.currentFile)) {
			this.api.delete(this.state.currentFile);
		}
	}

	render() {
		return (<li className="nav-item">
		        	<a className="nav-link" onClick={this.handleDelete}>Delete Entry</a>
		      	</li>)
	}
}
