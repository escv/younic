import React, { Component } from 'react';
import Modal from 'react-modal';
import EventBus from './EventBus';

var axios = require('axios');

Modal.setAppElement('#root')

export default class AddFolder extends Component {

	constructor(props) {
	    super(props);
	    this.state = {
			modalIsOpen: false,
    		currentFolder: '/'
    	}
	    this.api = axios.create({
		  baseURL: '/api/dir/',
		  timeout: 10000,
		  headers: {
		    'Accept': 'text/plain',
		    'Content-Type': 'text/plain',
		  }
		});
		this.openModal = this.openModal.bind(this);
		this.closeModal = this.closeModal.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}
	componentWillMount() {
		const self = this;
		EventBus.addListener('folderSelected', (entryFQN) => {
			self.setState({
				currentFolder: entryFQN
			})
		});
    }
	openModal() {
		this.setState({modalIsOpen: true});
	}

	closeModal() {
		this.setState({modalIsOpen: false});
	}

	handleSubmit(event) {
		this.api.post(this.state.currentFolder+'/'+event.currentTarget.elements.namedItem("entryName").value, '');
	    event.preventDefault();
	}

	render() {
		return (<li className="nav-item">
		        	<a className="nav-link" onClick={this.openModal}>Add Folder</a>
		        	<Modal isOpen={this.state.modalIsOpen}
						onRequestClose={this.closeModal}
				        contentLabel="Add/Edit Modal">

				        <b>Hello World</b><p>{this.state.currentFolder}</p>
				        <form onSubmit={this.handleSubmit}>
					        <input type="text" name="entryName"/>
							<input type="submit"/>
				        </form>
				        <button onClick={this.closeModal}>close</button>

					</Modal>
		      	</li>)
	}
}
