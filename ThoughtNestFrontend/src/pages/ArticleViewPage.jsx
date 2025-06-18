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

  console.log("🟢 ArticleViewPage loaded");
  console.log("🔑 Article ID from route param:", id);
  console.log("🔐 Auth token present:", !!authToken);

  // Fetch the article on mount
  useEffect(() => {
    async function fetchArticle() {
      console.log("📡 Fetching article ID:", id);

      try {
        const res = await fetch(`${API_BASE_URL}/articles/${id}`, {
          headers: {
            Authorization: `Bearer ${authToken}`,
          },
        });

        console.log("🌐 Fetch status code:", res.status);

        if (res.ok) {
          const json = await res.json();
          console.log("📥 Article fetch response:", json);

          if (!json.data) {
            safeToast("No article found in view response.", "warning");
            return;
          }

          setArticle(json.data);
        } else {
          safeToast("Failed to load article", "error");
        }
      } catch (err) {
        console.error("❌ Error fetching article:", err);
        safeToast("Error fetching article", "error");
      }
    }

    fetchArticle();
  }, [id, authToken]);

  if (!article) return <div>Loading article...</div>;

  // Construct full image URL for logging
  const imageUrl = article.image?.startsWith("http")
    ? article.image
    : `${API_BASE_URL}${article.image}`;

  console.log("🖼️ Final image URL to load:", imageUrl);

  return (
    <div className="article-container">
      <Link to="/" className="logo">ThoughtNest</Link>

      {/* Image with error fallback and logging */}
      <img
        src={imageUrl}
        alt="Article"
        className="body-image"
        onError={(e) => {
          console.error("🚫 Image failed to load:", imageUrl);
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
