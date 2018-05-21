import React, { Component } from 'react';
import AddFile from './AddFile';
import AddFolder from './AddFolder';
import DeleteEntry from './DeleteEntry';
import DeleteFolder from './DeleteFolder';

export default class NavBar extends Component {

	render() {
		return (<nav className="navbar navbar-expand-lg navbar-dark bg-dark">
  					<a className="navbar-brand" href="/">younic</a>

  					<div className="navbar-collapse">
	    				<ul className="navbar-nav mr-auto">
							<AddFolder />
	    					<AddFile />
	    					<DeleteEntry />
	    					<DeleteFolder />
						</ul>
					</div>
				</nav>)
	}
}
