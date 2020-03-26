import React, {useState, useContext} from "react";
import {useGrpcRequest} from "./useGrpcHook";
import {ClientContext} from "./index";
const {RecordsRequest, RecordsResponse} = require("./AttendancePodsInterface_pb");
const {StudentRecordsServiceClient} = require("./AttendancePodsInterface_grpc_web_pb");


/**
 * Notes:
 *  - Password as of right now has no value. Need to implement on server-side.
 *  - handleLogin() sets the state.
 *  const {EchoRequest, EchoResponse} = require('./echo_pb.js');
 const {EchoServiceClient} = require('./echo_grpc_web_pb.js');

 var echoService = new EchoServiceClient('http://localhost:8080');

 var request = new EchoRequest();
 request.setMessage('Hello World!');

 echoService.echo(request, {}, function(err, response) {
  // ...
});
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


    event.preventDefault();
  }

  render() {
    const [input, setInput] = useState(this.state.room);
    const client = useContext(ClientContext);

    const newRecordsRequest = async ({room}) => {
      const request = new RecordsRequest();
      request.setRoom(room);
      return await client.getPeriodRecords(request, {});
    };

    const [data, error, loading, refetch] = useGrpcRequest(newRecordsRequest, {room: this.state.room}, []);
    const handleSubmit = () => refetch({room: this.state.room});
    if (error) {
      console.error(error);
      return (
        <div>
          {loading ? <div> Retrying...</div> : <div>Error: {error.message} </div>}
        </div>
      );
    } else {
      console.log(data);
    }

    return (
      <div>
        <p> AYYYY YYFYFYFY</p>
        <form onSubmit={handleSubmit}>
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
      </div>
    );
  }
}

export default Login;