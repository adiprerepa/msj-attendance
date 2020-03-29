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
      presentStudentsList: [],
      absentStudentsList: [],
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
          presentStudentsList: data.presentStudents,
          absentStudentsList: data.missingStudents,
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

  renderPresentTableData() {
    return this.state.presentStudentsList.map((student, index) => {
      const { id, name } = student; //destructuring
      return (
        <tr key={id}>
          <td>{id}</td>
          <td>{name}</td>
        </tr>
      )
    })
  }

  renderAbsentTableData() {
    return this.state.absentStudentsList.map((student, index) => {
      const { id, name} = student;
      return (
        <tr key={id}>
          <td>{id}</td>
          <td>{name}</td>
        </tr>
      )
    })
  }

  render() {
    if (this.state.isLoggedIn) {
      // get missing students from backend
      return (
        <div>
          <div>
            <h1 id='title'>Present Students</h1>
            <table id='students'>
              <tbody>
              {this.renderPresentTableData()}
              </tbody>
            </table>
          </div>
          <div>
            <h1 id ='title'>Absent Students</h1>
            <table id='students'>
              <tbody>
              {this.renderAbsentTableData()}
              </tbody>
            </table>
          </div>
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