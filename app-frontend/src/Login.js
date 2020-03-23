import React, {useState, useContext} from "react";

/**
 * Notes:
 *  - Password as of right now has no value. Need to implement on server-side.
 *  - handleLogin() sets the state.
 */

class Login extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      room: '',
      password: '',
      isLoggedIn: false,
      studentsList: [],
    };
    this.handleRoomChange = this.handleRoomChange.bind(this);
    this.handlePasswordChange = this.handlePasswordChange.bind(this);
    this.handleLogin = this.handleLogin.bind(this);
  }

  handleRoomChange(event) {
    this.setState({room: event.target.value});
  }

  handlePasswordChange(event) {
    this.setState({password: event.target.value});
  }

  handleLogin(event) {
    fetch( "http://localhost:2005/StudentRecordsService/getPeriodRecords", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Request-Headers': '*n'
      },
      body: JSON.stringify({
        room: this.state.room,
      })
    }).then(function(response) {
      console.log(response);
      return response.json();
    }).then(function(data) {
      console.log(data);
      console.log(data.status)
    });
    event.preventDefault();
  }

  render() {
    return (
      <form onSubmit={this.handleLogin}>
        <label>
          Room:
          <input type="text" value={this.state.room} onChange={this.handleRoomChange} />
        </label>
        <br/>
        <label>
          Password:
          <input type="text" value={this.state.password} onChange={this.handlePasswordChange} />
        </label>
        <br/>
        <input type="submit" value="Submit" />
      </form>
    )
  }
}

export default Login;