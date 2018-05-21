import React, { Component } from 'react';
import EventBus from './EventBus';

export default class DirEntry extends Component {

	constructor(props) {
	    super(props);
	    this.handleEntryClick = this.handleEntryClick.bind(this);
	}

	handleEntryClick(e) {
		this.props.onEntrySelect(this);
		const evType = this.props.container ? 'folderSelected' : 'entrySelected';
		EventBus.emit(evType, this.props.fqn);
	}

	render() {
		const icon = this.props.container ? "folder-open" : "file-empty";
		var ctnClass = this.props.container ? "container" : "";
		if (this.props.active) {
			ctnClass += " active";
		}
		return (
			<React.Fragment>
				<li data-fqn={this.props.fqn} onClick={this.handleEntryClick} className={ctnClass}>
					<div>
						<img src={"icons/"+icon+".png"} alt="Icon"/> {this.props.name}
					</div>
				</li>
			</React.Fragment>
		)
	}
}

