import React from 'react'
import {NavLink} from "react-router-dom";

const home = () => {
  return (
    <div>
      <span>msjhs attendance </span>
      <NavLink to="/">Home</NavLink>
      <span> </span>
      <NavLink to="/login">Login</NavLink>
      <span> </span>
      <NavLink to="/register">Register</NavLink>
    </div>
  );
};

export default home;