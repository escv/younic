import React, { Component } from 'react';
import DirColumn from './DirColumn';

export default class DirList extends Component {

	constructor(props) {
	    super(props);
	    this.state = {
	    	columns: ['/']
	    };
	    this.handleSelect = this.handleSelect.bind(this);
	}

	handleSelect(dirCol, fqn) {
		this.setState((prevState, props) => {
			var newCols = prevState.columns.slice(0, prevState.columns.indexOf(dirCol.props.fqn)+1);
			return {
				columns: newCols.concat([fqn])
			}
		})
	}

	render() {
		return (
			<section className="container-fluid">
				<div className="row-fluid dir-list">
					{this.state.columns.map((item, index) => <DirColumn key={item} fqn={item} onSelect={this.handleSelect}/>)}
				</div>
			</section>
		)
	}
}
