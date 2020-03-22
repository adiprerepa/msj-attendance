import React from 'react';
import './App.css';
import Home from "./Home"
import Login from "./Login"
import Register from "./Register"
import {
  BrowserRouter as Router,
  Route,
} from "react-router-dom";
function App() {
  return (
    <body>
    <Router>
      <div>
        <Route path="/" component={Home}/>
        <Route path="/login" component={Login}/>
        <Route path="/register" component={Register}/>
      </div>
    </Router>
    </body>
  );
}

export default App;
