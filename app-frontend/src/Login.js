import React, {useState, useContext} from "react";
import {useGrpcRequest} from './useGrpcHook'

const {RecordsRequest, RecordsResponse} = require('./AttendancePodsInterface_pb.js');
const {StudentRecordsServiceClient} = require('./AttendancePodsInterface_grpc_web_pb.js');

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
    var studentRecordsService = new StudentRecordsServiceClient('http://localhost:2005', null, null);
    var request = new RecordsRequest();
    request.setRoom(this.state.room);
    studentRecordsService.getPeriodRecords(request, {}, function(err, response) {
      console.log(response);
      if (err) {
        console.error(err)
      }
      if (response.getStatus()) {
        this.setState({
          isLoggedIn: true,
          studentsList: response.getStudentsList()
        });
        console.log(true);
        console.log(response.getStudentsList())
        // authentication succeeded - lead to next page
      } else {
        this.setState({
          isLoggedIn: false,
        });
        console.log("shit" + false)
        // show "authentication failed"
      }
    });
    alert('lmao -> ' + this.state.room + " " + this.state.password);
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