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
      attempts: 0,
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
    let curComponent = this;
    fetch( "http://localhost:2007/attendance/getRecords/" + this.state.room, {
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      }
    }).then(function(response) {
      return response.json();
    }).then(function(data) {
      console.log(data);
      if (data.status) {
        curComponent.setState({
          isLoggedIn : true,
          studentsList: data.students
          }
        );
      } else {
        curComponent.setState({
          attempts: 1
        })
      }
    });
    // this.forceUpdate();
    event.preventDefault();
  }

  render() {
    console.log(this.state);
    if (this.state.isLoggedIn) {
      // get missing students from backend
      return (
        <div>
        <br/>
        <span>Logged in students:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
          <span>    </span>
          <table>

          </table>
          <span>Missing Students:</span>
        </div>
      );
    } else {
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
}

export default Login;