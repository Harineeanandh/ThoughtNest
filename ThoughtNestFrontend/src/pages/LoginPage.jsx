import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import "../styles/login.css";
import { login } from "../scripts/api";
import { saveAuth } from "../scripts/auth";
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import apiInstance from "../scripts/apiInstance"; // Axios instance with baseURL & auth

// Login component handles both login and forgot password flows
export default function Login() {
  const navigate = useNavigate(); // For redirection
  const location = useLocation(); // To read query params for redirection

  const [isLoading, setIsLoading] = useState(false); // For login button loading state
  const [isForgotLoading, setIsForgotLoading] = useState(false); // For forgot password button loading

  // Input state for login form
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false); // Toggle password visibility

  // Forgot password state
  const [forgotMode, setForgotMode] = useState(false); // Controls view toggle
  const [forgotEmail, setForgotEmail] = useState(""); // Email to send reset to
  const [resetStatus, setResetStatus] = useState(null); // Success/failure message

  // Determine where to redirect after successful login
  const params = new URLSearchParams(location.search);
  const redirectTo = params.get("redirect") || "/dashboard";
  console.log("Redirecting to:", redirectTo);

  // Handle login form submission
  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    // Clear any existing auth state
    delete axios.defaults.headers.common["Authorization"];
    localStorage.removeItem("token");
    sessionStorage.clear();

    try {
      // Send login request
      const data = await login({ identifier: email, password });
      const token = data.token;

      // Decode token to extract user info
      const decoded = jwtDecode(token);
      const extractedEmail = decoded.sub;

      // Store authentication data
      saveAuth(token, extractedEmail);
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

      // Delay navigation to simulate loading
      setTimeout(() => navigate(redirectTo), 1000);
    } catch (error) {
      // Default error message
      let message = "Login failed. Please try again.";

      // Customize error messages based on response
      if (error?.response) {
        if (error.response.status === 401) {
          message = "Invalid email or password. Please try again.";
        } else if (error.response.status >= 500) {
          message = "Server error. Please try again later.";
        } else if (error.response.data?.message) {
          message = error.response.data.message;
        }
      } else if (error?.request) {
        message = "Network error. Please check your internet connection.";
      }

      toast.error(message);
      console.error("Login error", error);
    } finally {
      setIsLoading(false);
    }
  };

  // Handle forgot password form submission
  const handleForgotSubmit = async (e) => {
  e.preventDefault();
  setResetStatus(null);
  setIsForgotLoading(true);

  try {
    // Use apiInstance instead of fetch for consistent base URL and headers
    const res = await apiInstance.post("/auth/forgot-password", {
      email: forgotEmail,
    });

    if (res.status === 200) {
      toast.success("Password reset email sent!");
      setResetStatus("Success! Check your email for reset instructions.");
    } else {
      const message = res.data?.message || "Please try again.";
      toast.error("Failed: " + message);
      setResetStatus(`Failed: ${message}`);
    }
  } catch (error) {
    toast.error("Error sending reset request. Please try again.");
    setResetStatus("Error sending reset request. Please try again.");
    console.error("Forgot password error", error);
  } finally {
    setIsForgotLoading(false);
  }
};

  return (
    <div className="login-wrapper">
      {/* Left side of login page */}
      <div className="login-left">
        {/* Logo linking to homepage */}
        <Link to="/" className="logo">ThoughtNest</Link>

        <div className="login-content">
          <h2>Login to explore ThoughtNest!</h2>

          {/* Login Form */}
          {!forgotMode ? (
            <>
              <form id="loginForm" onSubmit={handleLogin}>
                <label htmlFor="email">Email address</label>
                <input
                  type="email"
                  placeholder="Enter your email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />

                <label htmlFor="password">Password</label>
                <div className="password-wrapper">
                  <input
                    type={showPassword ? "text" : "password"}
                    className="password-input"
                    placeholder="Enter your password"
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

                {/* Login button */}
                <button type="submit" disabled={isLoading}>
                  {isLoading ? "Logging in..." : "Login"}
                </button>
              </form>

              {/* Footer with forgot and signup links */}
              <div style={{ marginTop: "1rem" }}></div>
              <div className="form-footer">
                <span className="forgot-password" onClick={() => setForgotMode(true)}>
                  Forgot Password?
                </span>
                <span className="divider"></span>
                <span className="signup-redirect">
                  New here?{" "}
                  <Link to={`/signup${redirectTo ? `?redirect=${encodeURIComponent(redirectTo)}` : ""}`}>
                    Create Account
                  </Link>
                </span>
              </div>
            </>
          ) : (
            <>
              {/* Forgot Password Form */}
              <form id="forgotPasswordForm" onSubmit={handleForgotSubmit}>
                <label htmlFor="forgotEmail">Enter your email to reset password</label>
                <input
                  type="email"
                  placeholder="Your email"
                  required
                  value={forgotEmail}
                  onChange={(e) => setForgotEmail(e.target.value)}
                />
                <button type="submit" disabled={isForgotLoading}>
                  {isForgotLoading ? "Sending..." : "Send Reset Link"}
                </button>
              </form>

              {/* Reset success or failure message */}
              {resetStatus && (
                <p
                  className="reset-status"
                  style={{
                    marginTop: "10px",
                    color: resetStatus.startsWith("Success") ? "green" : "red",
                  }}
                >
                  {resetStatus}
                </p>
              )}

              {/* Back to login toggle */}
              <p
                className="back-to-login"
                style={{ cursor: "pointer", color: "#6a5acd", marginTop: "10px" }}
                onClick={() => {
                  setForgotMode(false);
                  setResetStatus(null);
                  setForgotEmail("");
                }}
              >
                Back to Login
              </p>
            </>
          )}
        </div>
      </div>

      {/* Right section with illustration */}
      <div className="login-right">
        <img src="/assets/girl-working.png" alt="Login illustration" />
      </div>

      {/* Decorative floating bird */}
      <img src="/assets/bird.png" alt="Bird" className="bird-floating" />

      {/* Toast messages container */}
      <ToastContainer position="bottom-right" autoClose={3000} hideProgressBar theme="colored" />
    </div>
  );
}
