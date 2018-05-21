import React, { Component } from 'react';
import DirEntry from './DirEntry';
var axios = require('axios');

export default class DirColumn extends Component {

	constructor(props) {
	    super(props);
	    this.state = {
	    	activeEntry: null,
			entries: []
	    }
	    this.handleEntryDirSelected = this.handleEntryDirSelected.bind(this);
	    this.api = axios.create({
		  baseURL: '/api/dir/',
		  timeout: 10000,
		  headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json',
		  }
		});
	}

	handleEntryDirSelected(dirEntry) {
		if (dirEntry.props.container) {
			this.props.onSelect(this, dirEntry.props.fqn);
		}
		this.setState({
			activeEntry: dirEntry.props.fqn
		})
	}

	componentDidMount() {
		var self = this;
		this.api.get(this.props.fqn).then(function (response) {	    	
			self.setState({
	    		entries: response.data
	    	});
	    });
	}

	render() {
		return (
			<div className="dir-col">
				<ul>
				{this.state.entries.map((item, index) => <DirEntry key={item.fqn} active={item.fqn===this.state.activeEntry} name={item.name} fqn={item.fqn} container={item.container} onEntrySelect={this.handleEntryDirSelected}/>)}
				</ul>
			</div>
		)
	}
}
