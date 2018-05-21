import React, { Component } from 'react';
import DirList from './DirList';
import NavBar from './NavBar';
import TextEditor from './TextEditor';


class App extends Component {
  render() {
    return (
      <div className="App">
        <NavBar />
        <DirList />

        <TextEditor />
      </div>
    );
  }
}

export default App;
