import React, { useState } from "react";
import { Link } from "react-router-dom";
import "../styles/contact.css";
import axios from "axios";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

// Safe toast wrapper to handle toast calls gracefully
const safeToast = (message, type = "info") => {
  setTimeout(() => {
    if (typeof toast[type] === "function") {
      toast[type](message);
    } else {
      console.warn("Invalid toast type or ToastContainer not mounted.");
    }
  }, 0);
};

// Use Vite env variable for backend API base URL, fallback to localhost
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function ContactPage() {
  // Local state for form fields and submission status
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  // Handle contact form submission
  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent page reload
    setIsLoading(true); // Disable button and show loading state

    try {
      const response = await axios.post(`${API_BASE_URL}/api/contact`, {
        name,
        email,
        message,
      });

      // Show success message and clear form
      if (response.status === 200) {
        safeToast(`Thank you, ${name}! Your message has been sent.`, "success");
        setName("");
        setEmail("");
        setMessage("");
      }
    } catch (err) {
      // Log and show error message
      console.error("Error sending message:", err);
      const msg = err.response?.data?.error || "Something went wrong. Please try again.";
      safeToast(msg, "error");
    } finally {
      setIsLoading(false); // Re-enable button
    }
  };

  return (
    <>
      {/* Header with logo navigation */}
      <header className="header">
        <Link to="/" className="logo">
          ThoughtNest
        </Link>
      </header>

      {/* Contact section layout */}
      <main className="contact-section">
        {/* Left side: Illustration */}
        <div className="contact-image">
          <img src="/assets/teamup.png" alt="Team collaboration illustration" />
        </div>

        {/* Right side: Contact form */}
        <div className="contact-form">
          <h2>Wanna reach out?</h2>
          <form onSubmit={handleSubmit}>
            {/* Name input */}
            <input
              type="text"
              placeholder="Name*"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
            />

            {/* Email input */}
            <input
              type="email"
              placeholder="Email*"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />

            {/* Message textarea */}
            <textarea
              placeholder="Message*"
              required
              value={message}
              onChange={(e) => setMessage(e.target.value)}
            />

            {/* reCAPTCHA legal notice */}
            <p className="captcha-note">
              This site is protected by reCAPTCHA and the Google{" "}
              <a href="#">Privacy Policy</a> and{" "}
              <a href="#">Terms of Service</a> apply.
            </p>

            {/* Submit button */}
            <button type="submit" disabled={isLoading}>
              {isLoading ? "Sending..." : "Send Message"}
            </button>
          </form>
        </div>
      </main>
    </>
  );
}
