// Author: Harinee Anandh
import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import "../styles/signup.css";
import 'react-toastify/dist/ReactToastify.css';
import { toast } from 'react-toastify';
import { signup } from "../scripts/api";

// Signup component allows users to create a new account on ThoughtNest
export default function Signup() {
  const navigate = useNavigate(); // Used for redirecting after signup
  const location = useLocation(); // Access current URL location

  // State variables to hold user input
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false); // Toggles password visibility
  const [isLoading, setIsLoading] = useState(false); // Indicates if signup is in progress

  // Get the redirect URL from query params (if user came from a protected route)
  const params = new URLSearchParams(location.search);
  const redirectTo = params.get("redirect");

  // Displays a toast message with error styling
  const showToastError = (message) => {
    toast.error(message);
  };

  console.log("Using correct Signup.jsx");

  // Handles the signup form submission
  const handleSignup = async (e) => {
    e.preventDefault(); // Prevent default form submit behavior
    setIsLoading(true); // Show loading state

    try {
      // Call the signup API with the provided credentials
      const data = await signup({ username, email, password });

      // Store authentication info in localStorage for session persistence
      localStorage.setItem("loggedIn", "true");
      localStorage.setItem("username", data.username || "DemoUser");
      localStorage.setItem("token", data.token || "DemoToken");
      localStorage.setItem("visitedBefore", "true");

      // Redirect to intended page or dashboard after short delay
      setTimeout(() => {
        if (redirectTo) {
          navigate(redirectTo);
        } else {
          navigate("/dashboard");
        }
      }, 1000);
    } catch (error) {
      // Default fallback message
      let message = "Signup failed. Please try again.";

      // Handle various error cases from backend or network
      if (error?.response) {
        const status = error.response.status;
        if (status === 400) {
          message = "Please check your inputs. All fields are required.";
        } else if (status === 409) {
          message = "An account with this email already exists.";
        } else if (status >= 500) {
          message = "Server error. Please try again later.";
        } else if (error.response.data?.message) {
          message = error.response.data.message;
        }
      } else if (error?.request) {
        message = "Network error. Please check your internet connection.";
      }

      // Show error to user via toast
      showToastError(message);
      console.log("Full error object", error);
    } finally {
      setIsLoading(false); // Reset loading state
    }
  };

  return (
    <>
      {/* Top navigation bar */}
      <header className="navbar">
        <Link to="/" className="logo">ThoughtNest</Link>
      </header>

      {/* Quick contact link at top-right */}
      <div className="top-right">
        <a href="/contact" className="top-right-link">Contact Us!</a>
      </div>

      {/* Main content area: Signup form layout */}
      <main className="signup-wrapper">
        {/* Left section with illustration */}
        <div className="signup-left">
          <img src="/assets/blogging.svg" alt="Signup illustration" loading="lazy"/>
        </div>

        {/* Right section with form */}
        <div className="signup-right">
          <h2>Create an account</h2>
          <form id="signupForm" onSubmit={handleSignup}>
            {/* Username field */}
            <input
              type="text"
              placeholder="Username*"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />

            {/* Email field */}
            <input
              type="email"
              placeholder="Email*"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />

            {/* Password field with show/hide toggle */}
            <div className="password-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                className="password-input"
                placeholder="Password*"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <img
                src={showPassword ? "/assets/eye-closed.png" : "/assets/eye-open.png"}
                alt="Toggle visibility"
                className="password-toggle"
                onClick={() => setShowPassword((prev) => !prev)}
              />
            </div>

            {/* Submit button with loading state */}
            <button type="submit" disabled={isLoading}>
              {isLoading ? "Creating Account..." : "Create Account"}
            </button>

            {/* Redirect to login page if already a user */}
            <p className="login-redirect">
              Existing User?{" "}
              <Link to={`/login${redirectTo ? `?redirect=${encodeURIComponent(redirectTo)}` : ""}`}>
                Login here
              </Link>.
            </p>
          </form>
        </div>
      </main>
    </>
  );
}
