'use client';

import { useRouter } from 'next/navigation';
import React from "react";


export default function Login() {
  // state value + state setter -> redraw ui
  const router = useRouter();
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [message, setMessage] = React.useState('');

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault(); // prevent legacy form submit 
    setMessage('');
    const userDto = {
      userName: email,
      password: password,
    };
    console.log(userDto, '<- Login');
    //http call to hw2 back end
    const httpSettings = {
      method: 'POST',
      body: JSON.stringify(userDto),
    };
    fetch('/api/login', httpSettings) // /createUser
      .then(httpRes => httpRes.json())
      .then(javaApiRes => { // RestApiAppResponse.java
        if (javaApiRes.status) {
          debugger;
          router.push('/home');
        } else {
          setMessage(javaApiRes.message);
        }
      })
      .catch(() => setMessage('Failed to Log in'));
  }

  function handleSignUp(event: React.FormEvent) {
    event.preventDefault(); // prevent legacy form submit 
    setMessage('');
    const userDto = {
      userName: email,
      password: password,
    };
    console.log(userDto, '<- Sign Up');

    //http call to hw2 back end
    const httpSettings = {
      method: 'POST',
      body: JSON.stringify(userDto),
    };
    fetch('/api/createUser', httpSettings) // /createUser
      .then(httpRes => httpRes.json())
      .then(javaApiRes => { // RestApiAppResponse.java
        if (javaApiRes.status) {
          setMessage('User created successfully');
        } else {
          setMessage(javaApiRes.message);
        }
      })
      .catch(() => setMessage('Failed to create user'));
  }

  return (
    <div>
      {message}
      <h1>Login</h1>
      <form>
        <label>
          Email:
          <input type="email" name="email" value={email} onChange={event => setEmail(event.target.value)} />
        </label>
        <label>
          Password:
          <input type="password" name="password" value={password} onChange={event => setPassword(event.target.value)} />
        </label>
        <button onClick={handleSubmit} disabled={email.length === 0 || password.length === 0} type="submit">Login</button>
        <button onClick={handleSignUp} disabled={email.length === 0 || password.length === 0} type="submit">Sign up</button>
      </form>
    </div>
  );
}
