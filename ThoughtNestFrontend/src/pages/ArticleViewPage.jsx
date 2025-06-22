// ArticleViewPage.jsx — Fully Commented
// Author: Harinee Anandh

import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

// Toast safeguard utility to ensure ToastContainer exists before showing toasts
const safeToast = (message, type = "info") => {
  const toastFn = toast[type] || toast;
  const containerExists = document.querySelector(".Toastify__toast-container");

  if (containerExists) {
    toastFn(message);
  } else {
    setTimeout(() => {
      if (document.querySelector(".Toastify__toast-container")) {
        toastFn(message);
      } else {
        console.warn("ToastContainer not mounted. Skipping toast:", message);
      }
    }, 500);
  }
};

// Read backend API base URL from Vite environment variable
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function ArticleViewPage() {
  const { id } = useParams(); // Extract article ID from URL
  const [article, setArticle] = useState(null); // State to hold article data
  const authToken = localStorage.getItem("token");

  console.log("ArticleViewPage loaded");
  console.log("Article ID from route param:", id);
  console.log("Auth token present:", !!authToken);

  useEffect(() => {
    document.body.classList.add("portrait-only");
    return () => document.body.classList.remove("portrait-only");
  }, []);

  // Fetch the article on mount
  useEffect(() => {
    async function fetchArticle() {
      const requestUrl = `${API_BASE_URL}/articles/${id}`;
      console.log("Fetching article ID:", id);
      console.log("Full API URL:", requestUrl);

      try {
        const res = await fetch(requestUrl, {
          headers: {
            Authorization: `Bearer ${authToken}`,
          },
        });

        console.log("Raw fetch response object:", res);
        console.log("Fetch status code:", res.status);

        const contentType = res.headers.get("content-type");
        console.log("Response content-type:", contentType);

        if (res.ok) {
          const json = await res.json();
          console.log("Parsed JSON response:", json);

          if (!json.data) {
            console.warn("JSON received but data field is missing.");
            safeToast("No article found in view response.", "warning");
            return;
          }

          setArticle(json.data);
        } else {
          let errorText = "";

          try {
            const errorJson = await res.json();
            console.error("Server returned error JSON:", errorJson);
            errorText = errorJson.message || JSON.stringify(errorJson);
          } catch (parseErr) {
            const fallbackText = await res.text();
            console.error("Failed to parse JSON. Raw response text:", fallbackText);
            errorText = fallbackText;
          }

          safeToast(`Failed to load article: ${res.status} - ${errorText}`, "error");
        }
      } catch (err) {
        console.error("Network or fetch error:", err);
        safeToast("Error fetching article", "error");
      }
    }

    fetchArticle();
  }, [id, authToken]);

  if (!article) return <div>Loading article...</div>;

  // Construct full image URL for logging
  const imageUrl = article.image;

  console.log("Final image URL to load:", imageUrl);

  return (
    <div className="article-container">
      <Link to="/" className="logo">ThoughtNest</Link>

      {/* Image with error fallback and logging */}
      <img
        src={imageUrl}
        alt="Article"
        className="body-image"
        onError={(e) => {
          console.error("Image failed to load:", imageUrl);
          e.target.src = "/assets/placeholder.png";
          safeToast("Image could not be loaded. Showing placeholder.", "warning");
        }}
      />

      <h1 className="article-title">{article.title}</h1>

      <p className="date">
        {new Date(article.date).toLocaleDateString()}
      </p>

      <div
        className="article-content"
        dangerouslySetInnerHTML={{ __html: article.content }}
      />

      <p><strong>Author:</strong> {article.authorUsername || "Unknown"}</p>
    </div>
  );
}
