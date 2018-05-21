import React, { Component } from 'react';
import EventBus from './EventBus';
var axios = require('axios');

export default class TextEditor extends Component {

	constructor(props) {
	    super(props);
	    this.state = {
	    	editorData: '',
	    	entryFQN: ''
	    }
	    this.handleChange = this.handleChange.bind(this);
	    this.handleSubmit = this.handleSubmit.bind(this);

	    this.api = axios.create({
		  baseURL: 'http://localhost:8282/api/txt/',
		  timeout: 10000,
		  headers: {
		    'Accept': 'text/plain',
		    'Content-Type': 'text/plain',
		  }
		});
	    
	    this.textWhitelist=['.txt','.html','.xml','.csv','.js','.css','.yaml'];
	}

	componentWillMount() {
		const self = this;
    	EventBus.addListener('entrySelected', (entryFQN) => {
    		if (self.isTxt(entryFQN)) {
	    		self.api.get(entryFQN).then(function (response) {
	    			self.setState({
						editorData: response.data,
						entryFQN: entryFQN
					})
	    		});
	    	} else {
	    		self.setState({
					editorData: '',
					entryFQN: ''
				})
	    	}
		});
    }

    isTxt(fqn) {
    	for (var i=0; i<this.textWhitelist.length; i++) {
    		if (fqn.endsWith(this.textWhitelist[i])) {
    			return true;
    		}
    	}
    	return false;
    }

	handleChange(event) {
		this.setState({
			editorData: event.target.value
		});
	}

	handleSubmit(event) {
		this.api.post(this.state.entryFQN, this.state.editorData);
		event.preventDefault();
	}

	render() {
		return (<section className="container-fluid">
					<div className="row-fluid">
						<form onSubmit={this.handleSubmit}>
							<textarea className="editor" value={this.state.editorData} onChange={this.handleChange}></textarea>
							<input type="submit"/>
						</form>
					</div>
				</section>)
	}
}
