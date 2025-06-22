// Author: Harinee Anandh
import React, { useEffect, useState } from "react";
import apiInstance from "../scripts/apiInstance";
import "../styles/account.css";
import { useNavigate, Link } from "react-router-dom";
import { FaRegEdit } from "react-icons/fa";
import { MdOutlineDraw } from "react-icons/md";
import { LuCalendarCheck2 } from "react-icons/lu";
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Util to safely trigger toasts (handles edge cases like invalid toast types)
const safeToast = (message, type = "info") => {
  setTimeout(() => {
    if (typeof toast[type] === "function") {
      toast[type](message);
    } else {
      console.warn("Invalid toast type or ToastContainer not mounted yet.");
    }
  }, 0);
};

export default function MyAccountPage() {
  // Local state for user data and form control
  const [userData, setUserData] = useState(null);
  const [editMode, setEditMode] = useState({ username: false, email: false });
  const [formData, setFormData] = useState({ username: "", email: "" });
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);

  const navigate = useNavigate();

  // Fetch latest user data (used after update)
  const fetchUserData = () => {
    const token = localStorage.getItem("token");

    apiInstance
      .get("/auth/account", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const user = res.data?.data;
        if (user) {
          setUserData(user);
          setFormData({ username: user.username, email: user.email });
        }
      })
      .catch((err) => {
        console.error("Error refetching user data", err);
        safeToast("Failed to refresh account details.", "error");
      });
  };

  // On mount: fetch user account data or redirect if not logged in
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    apiInstance
      .get("/auth/account", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const user = res.data?.data;
        if (user) {
          setUserData(user);
          setFormData({ username: user.username, email: user.email });
        } else {
          console.error("No user data found.");
          safeToast("User data could not be loaded.", "error");
        }
      })
      .catch((err) => {
        console.error("Error fetching user data:", err);
        safeToast("Session expired. Please log in again.", "error");
        setTimeout(() => navigate("/login"), 1500);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [navigate]);

  // Enable editing for selected field
  const handleEdit = (field) => {
    setEditMode((prev) => ({ ...prev, [field]: true }));
  };

  // Controlled input handler for form fields
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Send PATCH request to update user info
  const handleUpdate = () => {
    const token = localStorage.getItem("token");
    setUpdating(true);

    apiInstance
      .patch(
        "/auth/account",
        {
          username: formData.username,
          email: formData.email,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      )
      .then((res) => {
        const updatedUser = res.data?.data;
        const message = res.data?.message || "";

        if (updatedUser) {
          // Special case: email changed → log user out
          if (message.includes("log in again")) {
            safeToast("Email changed! Please log in again.", "info");
            localStorage.removeItem("token");
            setTimeout(() => navigate("/login"), 1500);
            return;
          }

          // Set updated data and reset edit mode
          setUserData(updatedUser);
          setFormData({ username: updatedUser.username, email: updatedUser.email });
          setEditMode({ username: false, email: false });

          // In case username changed on server
          if (formData.username !== userData.username) {
            fetchUserData();
          }

          safeToast("Account updated successfully!", "success");
        } else {
          safeToast("Update succeeded but user data is missing.", "error");
        }
      })
      .catch((err) => {
        console.error("Failed to update account", err);
        const msg = err?.response?.data?.message || err?.message || "Update failed.";
        safeToast(msg, "error");
      })
      .finally(() => {
        setUpdating(false);
      });
  };

  // Show loading or fallback state before user data is ready
  if (loading) return <div className="account-container"><p>Loading...</p></div>;
  if (!userData) return <div className="account-container"><p>User data not available.</p></div>;

  return (
    <>
      {/* Header links */}
      <Link to="/" className="logo">ThoughtNest</Link>
      <div className="top-right">
        <a href="/contact" className="top-right-link">Contact Us!</a>
      </div>

      {/* Main container */}
      <div className="account-container">

        {/* Left: Editable account info */}
        <div className="account-details">
          <h2>My Account</h2>
          <div className="account-form">
            <h3>My Details</h3>

            {/* Username field */}
            <div className="account-field">
              <input
                type="text"
                name="username"
                value={formData.username}
                onChange={handleChange}
                disabled={!editMode.username}
                placeholder="Username"
              />
              <button onClick={() => handleEdit("username")} disabled={editMode.username}>
                <FaRegEdit />
              </button>
            </div>

            {/* Email field */}
            <div className="account-field">
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                disabled={!editMode.email}
                placeholder="Email"
              />
              <button onClick={() => handleEdit("email")} disabled={editMode.email}>
                <FaRegEdit />
              </button>
            </div>

            {/* Save button appears only in edit mode */}
            {(editMode.username || editMode.email) && (
              <button
                className="update-btn"
                onClick={handleUpdate}
                disabled={updating}
              >
                {updating ? "⏳ Updating..." : "SAVE CHANGES"}
              </button>
            )}
          </div>
        </div>

        {/* Right: Blog stats summary */}
        <div className="account-summary">
          <div className="profile-pic">
            <span role="img" aria-label="profile">👩</span>
          </div>

          <h3>My Blog Stats</h3>
          <p><MdOutlineDraw /> <strong>{userData.articleCount}</strong> Articles Created</p>
          <p><LuCalendarCheck2 /> <strong>{userData.publishedCount}</strong> Articles Published</p>
        </div>
      </div>
    </>
  );
}
