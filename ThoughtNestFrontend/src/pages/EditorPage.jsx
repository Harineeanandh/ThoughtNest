// EditorPage.jsx 
// Author: Harinee Anandh

import React, { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams, useLocation } from "react-router-dom";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import "../styles/article.css";
import apiInstance from "../scripts/apiInstance"; // Axios instance with baseURL & auth
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const safeToast = (message, type = "info") => {
  console.log(`Showing toast: "${message}" of type: "${type}"`);
  toast[type](message);
};

export default function EditorPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const mode = searchParams.get("mode") || "create";
  const articleId = searchParams.get("id");

  const [title, setTitle] = useState("");
  const [date, setDate] = useState("");
  const [content, setContent] = useState("");
  const [imageFile, setImageFile] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState("/assets/placeholder.png");
  const [loading, setLoading] = useState(mode === "edit");
  const [submitting, setSubmitting] = useState(false);

  const authToken = localStorage.getItem("token");

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

  function formatDateForInput(dateString) {
    if (!dateString) return "";
    const d = new Date(dateString);
    if (isNaN(d)) return "";
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  }

  useEffect(() => {
    document.body.classList.add("portrait-only");
    return () => document.body.classList.remove("portrait-only");
  }, []);

  useEffect(() => {
    if (mode === "edit" && articleId) {
      async function fetchArticle() {
        try {
          const res = await apiInstance.get(`/articles/${articleId}`);
          const article = res.data.data;

          setTitle(article.title || "");
          setContent(article.content || "");
          setDate(formatDateForInput(article.date));

          if (article.image) {
            setImagePreviewUrl(article.image);
          } else {
            setImagePreviewUrl("/assets/placeholder.png");
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

  function onImageChange(e) {
    const file = e.target.files[0];
    setImageFile(file);
    setImagePreviewUrl(file ? URL.createObjectURL(file) : "/assets/placeholder.png");
  }

  async function submitArticle() {
    if (!title || !date || !content) {
      safeToast("Please fill in all fields before saving.", "error");
      return;
    }

    setSubmitting(true);
    let imageUrl = imagePreviewUrl;

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
        safeToast("Image upload failed. Please try again with a file size less than 200kb", "error");
        setSubmitting(false);
        return;
      }
    } else if (imageUrl && !imageUrl.startsWith("http")) {
      imageUrl = null;
    }

    const articleData = {
      title,
      date: new Date(date).toISOString(),
      content,
      image: imageUrl,
      author: loggedInUser,
    };

    try {
      let response;
      if (mode === "create") {
        response = await apiInstance.post("/articles", articleData);
      } else if (mode === "edit" && articleId) {
        response = await apiInstance.put(`/articles/${articleId}`, articleData);
      }

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

  if (loading) {
    return (
      <div className="article-container">
        <p>Loading article data...</p>
      </div>
    );
  }

  const quillModules = {
    toolbar: [
      [{ header: [1, 2, 3, false] }],
      ["bold", "italic", "underline", "strike"],
      [{ list: "ordered" }, { list: "bullet" }],
      ["blockquote", "link"],
      ["clean"],
    ],
  };

  const quillFormats = [
    "header", "bold", "italic", "underline", "strike",
    "list", "bullet", "blockquote", "link",
  ];

  return (
    <div className="editor-wrapper">
      <div className="article-container">
        <Link to="/" className="logo">ThoughtNest</Link>

        <input type="file" accept="image/*" onChange={onImageChange} />
        <img
          src={imagePreviewUrl}
          className="top-image"
          alt="Preview"
          onError={(e) => {
            console.error("Broken image URL:", e.target.src);
            e.target.src = "/assets/placeholder.png";
          }}
        />

        <input
          type="text"
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="article-title"
        />

        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          className="date"
        />

        <ReactQuill
          value={content}
          onChange={setContent}
          modules={quillModules}
          formats={quillFormats}
          placeholder="Write your article here..."
          className="article-editor"
          theme="snow"
        />

        <button onClick={submitArticle} disabled={submitting}>
          {submitting
            ? "Saving..."
            : `Save & ${mode === "create" ? "Create" : "Update"} Article`}
        </button>
      </div>
    </div>
  );
}
