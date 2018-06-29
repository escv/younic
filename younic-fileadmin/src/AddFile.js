import React, { Component } from 'react';
import Modal from 'react-modal';
import EventBus from './EventBus';
var axios = require('axios');

Modal.setAppElement('#root')

export default class AddFile extends Component {

	constructor(props) {
	    super(props);
	    this.state = {
			modalIsOpen: false,
    		currentFolder: '/',
    		editorData: '',
    		currentEntry: ''
    	};
    	this.openModal = this.openModal.bind(this);
		this.closeModal = this.closeModal.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChange = this.handleChange.bind(this);
		this.api = axios.create({
		  baseURL: '/api/txt/',
		  timeout: 10000,
		  headers: {
		    'Accept': 'text/plain',
		    'Content-Type': 'text/plain',
		  }
		});
	}

	componentWillMount() {
		const self = this;
		EventBus.addListener('entrySelected', (entryFQN) => {
			self.setState({
				currentEntry: entryFQN
			})
		});
		EventBus.addListener('folderSelected', (entryFQN) => {
			self.setState({
				currentFolder: entryFQN
			})
		});
    }

	handleChange(event) {
		this.setState({
			editorData: event.target.value
		});
	}

	openModal() {
		this.setState({modalIsOpen: true});
	}

	closeModal() {
		this.setState({modalIsOpen: false});
	}

	handleSubmit(event) {
		this.api.post(this.state.currentFolder+'/'+event.currentTarget.elements.namedItem("entryName").value, this.state.editorData);
	    event.preventDefault();
	}

	render() {
		return (<li className="nav-item">
					<a className="nav-link" onClick={this.openModal}>Add File</a>
		        	<Modal isOpen={this.state.modalIsOpen}
						onRequestClose={this.closeModal}
				        contentLabel="Add/Edit Modal">

				        <b>Hello World</b><p>{this.state.currentEntry} in {this.state.currentFolder}</p>
				        <form onSubmit={this.handleSubmit}>
					        <input type="text" name="entryName"/>
					        <textarea className="editor" value={this.state.editorData} onChange={this.handleChange}></textarea>
							<input type="submit"/>
				        </form>
				        <button onClick={this.closeModal}>close</button>

					</Modal>
		      	</li>)
	}
}
