// EditorPage.jsx — Fully Commented
// Author: Harinee Anandh

import React, { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams, useLocation } from "react-router-dom";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import "../styles/article.css";
import apiInstance from "../scripts/apiInstance"; // Axios instance with baseURL & auth
import { ToastContainer } from "react-toastify";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const safeToast = (message, type = "info") => {
  console.log(`Attempting to show toast: "${message}" of type: "${type}"`);
  setTimeout(() => {
    const container = document.querySelector(".Toastify__toast-container");
    if (container) {
      console.log("ToastContainer is mounted. Showing toast.");
      toast[type](message);
    } else {
      console.warn("ToastContainer not mounted yet. Skipping toast:", message);
    }
  }, 250);
};


// Main editor page component for creating or editing articles
export default function EditorPage() {
  const location = useLocation(); // React Router hook to access current location object
  console.log("EditorPage loaded, location:", location);

  const navigate = useNavigate(); // React Router hook for programmatic navigation
  const [searchParams] = useSearchParams(); // Extract search parameters from URL
  const mode = searchParams.get("mode") || "create"; // Determine whether to create or edit
  const articleId = searchParams.get("id"); // Article ID for edit mode

  // State for form fields
  const [title, setTitle] = useState("");
  const [date, setDate] = useState("");
  const [content, setContent] = useState("");
  const [imageFile, setImageFile] = useState(null); // Image file object
  const [imagePreviewUrl, setImagePreviewUrl] = useState("/assets/placeholder.png");

  const [loading, setLoading] = useState(mode === "edit"); // Show loader if editing
  const [submitting, setSubmitting] = useState(false); // Disable button during submission

  const authToken = localStorage.getItem("token"); // Retrieve token from localStorage

  // Decode JWT token to extract username or subject
  function getUsernameFromToken(token) {
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.sub || payload.username || null;
    } catch (e) {
      console.error("Failed to decode token:", e);
      return null;
    }
  }

  const loggedInUser = getUsernameFromToken(authToken);

  // Format a date string (from DB) into YYYY-MM-DD format for input[type="date"]
  function formatDateForInput(dateString) {
    if (!dateString) return "";
    const d = new Date(dateString);
    if (isNaN(d)) return "";
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  // Fetch article data if in edit mode
  useEffect(() => {
  if (mode === "edit" && articleId) {
    async function fetchArticle() {
      try {
        console.log("Fetching article with ID:", articleId);
        const res = await apiInstance.get(`/articles/${articleId}`);

        console.log("Raw API response:", res.data);
        const article = res.data.data;

        console.log("Fetched article:", article);
        console.log("Fetched image path:", article.image);

        setTitle(article.title || "");
        setContent(article.content || "");
        setDate(formatDateForInput(article.date));

        // Set image preview
        if (article.image) {
          setImagePreviewUrl(article.image);
          console.log("imagePreviewUrl set to:", article.image);
        } else {
          setImagePreviewUrl("/assets/placeholder.png");
          console.warn("No image found in article. Using placeholder.");
        }
      } catch (err) {
        console.error("Error loading article", err);
        safeToast("Failed to load article.", "error");
      } finally {
        setLoading(false);
      }
    }

    fetchArticle();
  } else {
    setLoading(false);
  }
}, [mode, articleId, loggedInUser]);


  // Handle image file selection and preview rendering
  function onImageChange(e) {
    const file = e.target.files[0];
    setImageFile(file);
    setImagePreviewUrl(file ? URL.createObjectURL(file) : "/assets/placeholder.png");
  }

  // Handle form submission to create or update an article
  async function submitArticle() {
    if (!title || !date || !content) {
      safeToast("Please fill in all fields before saving.", "error");
      return;
    }

    setSubmitting(true);
    let imageUrl = imagePreviewUrl;

    // Upload image if provided
    if (imageFile) {
      const formData = new FormData();
      formData.append("image", imageFile);

      try {
        const uploadRes = await apiInstance.post("/articles/upload-image", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        imageUrl = uploadRes.data.data;
      } catch (error) {
        console.error("Error uploading image", error);
        safeToast("Image upload failed. Please try again.", "error");
        setSubmitting(false);
        return;
      }
    }

    // Construct final article payload
    const articleData = {
      title,
      date: new Date(date).toISOString(),
      content,
      image: imageUrl,
      author: loggedInUser,
    };

    try {
      let response;

      // POST new article
      if (mode === "create") {
        response = await apiInstance.post("/articles", articleData);
      }
      // PUT existing article update
      else if (mode === "edit" && articleId) {
        response = await apiInstance.put(`/articles/${articleId}`, articleData);
      }

      // Handle response
      if (response?.status >= 200 && response?.status < 300) {
        safeToast(`Article ${mode === "create" ? "created" : "updated"} successfully!`, "success");
        navigate("/dashboard");
      } else {
        console.error("Failed to save article:", response);
        safeToast("Failed to save article. Check console.", "error");
      }
    } catch (error) {
      console.error("Error saving article", error);
      safeToast("Error while saving article. Please try again.", "error");
    } finally {
      setSubmitting(false);
    }
  }

  // Show loading state when data is being fetched
  if (loading) {
    return (
      <div className="article-container">
        <p>Loading article data...</p>
      </div>
    );
  }

  // Quill toolbar configuration
  const quillModules = {
    toolbar: [
      [{ header: [1, 2, 3, false] }],
      ["bold", "italic", "underline", "strike"],
      [{ list: "ordered" }, { list: "bullet" }],
      ["blockquote", "link"],
      ["clean"],
    ],
  };

  // Supported Quill content formats
  const quillFormats = [
    "header",
    "bold",
    "italic",
    "underline",
    "strike",
    "list",
    "bullet",
    "blockquote",
    "link",
  ];

  // Final rendered layout of the editor page
  return (
    <div className="article-container">
      <Link to="/" className="logo">
        ThoughtNest
      </Link>

      {/* Image upload input */}
      <input type="file" accept="image/*" onChange={onImageChange} />

      {/* Preview of selected image */}
      <img
        src={imagePreviewUrl}
        className="top-image"
        alt="Preview"
        onError={(e) => {
          e.target.src = "/assets/placeholder.png";
        }}
      />

      {/* Title input field */}
      <input
        type="text"
        placeholder="Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="article-title"
      />

      {/* Date input field */}
      <input
        type="date"
        value={date}
        onChange={(e) => setDate(e.target.value)}
        className="date"
      />

      {/* Quill rich text editor */}
      <ReactQuill
        value={content}
        onChange={setContent}
        modules={quillModules}
        formats={quillFormats}
        placeholder="Write your article here..."
        className="article-editor"
        theme="snow"
      />

      {/* Submit button */}
      <button onClick={submitArticle} disabled={submitting}>
        {submitting
          ? "Saving..."
          : `Save & ${mode === "create" ? "Create" : "Update"} Article`}
      </button>

      {/* Toast notification container */}
      <ToastContainer position="top-right" autoClose={3000} />
    </div>
  );
}
