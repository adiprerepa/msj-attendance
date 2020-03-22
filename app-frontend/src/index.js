import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import {StudentRecordsServicePromiseClient} from "./AttendancePodsInterface_grpc_web_pb"

export const ClientContext = React.createContext();

const client = new StudentRecordsServicePromiseClient('http://localhost:8080', null, null);


ReactDOM.render(
  <ClientContext.Provider value={client}>
    <App />
  </ClientContext.Provider>,
  document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
