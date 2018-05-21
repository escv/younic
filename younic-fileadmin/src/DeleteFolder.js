import React, { Component } from 'react';
import Modal from 'react-modal';
import EventBus from './EventBus';
var axios = require('axios');

export default class DeleteFolder extends Component {


	constructor(props) {
	    super(props);
	    this.state = {
    		currentFolder: '/',
    	}
	    this.api = axios.create({
		  baseURL: '/api/dir/',
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
		EventBus.addListener('folderSelected', (entryFQN) => {
			self.setState({
				currentFolder: entryFQN
			})
		});
	}

	handleDelete() {
		if (window.confirm("Realy delete Folder "+this.state.currentFolder)) {
			this.api.delete(this.state.currentFolder);
		}
	}

	render() {
		return (<li className="nav-item">
		        	<a className="nav-link" onClick={this.handleDelete}>Delete Folder</a>
		      	</li>)
	}
}
