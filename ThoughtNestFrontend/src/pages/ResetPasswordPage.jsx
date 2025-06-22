// Author: Harinee Anandh
import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../styles/resetpassword.css";
import 'react-toastify/dist/ReactToastify.css';
import { toast } from "react-toastify";
import api from "../scripts/apiInstance"; // Using shared axios instance

export default function ResetPasswordPage() {
  const location = useLocation();
  const navigate = useNavigate();

  const [newPassword, setNewPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // Extract token from query string
  const token = new URLSearchParams(location.search).get("token");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await api.post("/auth/reset-password", {
        token,
        newPassword,
      });

      toast.success("Password reset successful! Please login.");
      setTimeout(() => navigate("/login"), 1500);
    } catch (error) {
      let message = "Password reset failed. Please try again.";

      if (error.response?.data?.message) {
        message = error.response.data.message;
      }

      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <header className="navbar center-logo-only">
        <h1 className="logo">ThoughtNest</h1>
      </header>

      <main className="simple-centered-form">
        <h2>Reset Password</h2>

        <form onSubmit={handleSubmit}>
          <div className="password-wrapper">
            <input
              type={showPassword ? "text" : "password"}
              className="password-input"
              placeholder="Enter new password"
              required
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />
            <img
              src={showPassword ? "/assets/eye-closed.png" : "/assets/eye-open.png"}
              alt="Toggle visibility"
              className="password-toggle"
              onClick={() => setShowPassword((prev) => !prev)}
            />
          </div>

          <button type="submit" disabled={isLoading}>
            {isLoading ? "Resetting..." : "Reset Password"}
          </button>
        </form>
      </main>
    </>
  );
}
