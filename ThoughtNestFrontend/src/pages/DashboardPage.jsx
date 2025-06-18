import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/dashboard.css";
import apiInstance from "../scripts/apiInstance";
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


export default function DashboardPage() {
  const navigate = useNavigate();
  const [articles, setArticles] = useState([]);
  const [publicArticles, setPublicArticles] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [articleToDelete, setArticleToDelete] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);
  const [articlePage, setArticlePage] = useState(1);
  const [publicPage, setPublicPage] = useState(1);
  const ARTICLES_PER_PAGE = 5;

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

  const authToken = localStorage.getItem("token");


  const loggedInUser = getUsernameFromToken(authToken);

  useEffect(() => {
    if (!authToken || !loggedInUser) {
      localStorage.removeItem("token");
      navigate("/login");
      return;
    }

    fetchArticles();
    fetchPublicArticles();
  }, []); 

  function formatDate(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString(undefined, {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  }
  function formatDateTime(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}


  async function fetchArticles() {
    try {
      const res = await apiInstance.get("/articles/my");
      const json = res.data;

      let rawData = Array.isArray(json.data)
        ? json.data
        : json.data?.articles || [];

      const processed = rawData.map((article) => ({
        ...article,
        authorUsername:
          article.authorUsername ||
          (article.author && article.author.username) ||
          "Unknown",
      }));

      setArticles(processed);
    } catch (e) {
      console.error("Error fetching user articles:", e);
      safeToast("Authentication error. Please log in again.", "error");
      if (e.response && (e.response.status === 401 || e.response.status === 403)) {
        safeToast("Authentication error. Please log in again.", "error");
        localStorage.removeItem("token");
        navigate("/login");
      }
    }
  }

  async function fetchPublicArticles() {
    try {
      const response = await apiInstance.get("/articles/public");
      const rawData = Array.isArray(response.data)
        ? response.data
        : Array.isArray(response.data.data)
        ? response.data.data
        : response.data.data?.articles || [];

      const processed = rawData.map((article) => ({
        ...article,
        authorUsername:
          article.authorUsername ||
          (article.author && article.author.username) ||
          "Unknown",
      }));

      setPublicArticles(processed);
    } catch (e) {
      console.error("Error fetching public articles:", e);
      safeToast("Failed to load public articles.", "error");
    }
  }

  const staticArticleRoutes = {
    "guilty eating meat": "/articles/Article1",
    "ego a human thing to have?": "/articles/Article2",
    "what os does a tv have?": "/articles/Article3",
    "nightmares are good?": "/articles/Article4",
    "is the universe detreministic": "/articles/Article5",
    "benefit of doubt": "/articles/Article6",
  };

  const staticArticlesAsObjects = Object.entries(staticArticleRoutes).map(
    ([title, route], index) => ({
      id: `static-${index}`,
      title,
      authorUsername: "ThoughtNest Blog Admin",
      route,
      isStatic: true,
    })
  );

  const combinedPublicArticles = [
    ...publicArticles,
    ...staticArticlesAsObjects.filter(
      (staticArticle) =>
        !publicArticles.some(
          (pub) => pub.id === staticArticle.id || pub.route === staticArticle.route
        )
    ),
  ];

  const filteredArticles = [
    ...articles.filter((article) =>
      article.title.toLowerCase().includes(searchTerm.toLowerCase())
    ),
    ...Object.entries(staticArticleRoutes)
      .filter(([title]) => title.includes(searchTerm.toLowerCase()))
      .map(([title, route]) => ({
        id: route,
        title,
        isStatic: true,
        route,
      })),
  ];

  const handleSearchInputChange = (e) => {
    setSearchTerm(e.target.value);
    setShowSuggestions(true);
    setActiveIndex(-1); // reset index on input
  };

  const handleSearchSuggestionClick = (title) => {
    setSearchTerm(title);
    setShowSuggestions(false);
  };

 const handleSearchButtonClick = () => {
  const matchedRoute = staticArticleRoutes[searchTerm.toLowerCase().trim()];
  if (matchedRoute) {
    navigate(matchedRoute);
  } else {
    safeToast("No exact match found. Please try a different search.", "error");
  }
};

  const handleKeyDown = (e) => {
    if (!showSuggestions || filteredArticles.length === 0) return;

    if (e.key === "ArrowDown") {
      e.preventDefault();
      setActiveIndex((prev) => (prev + 1) % filteredArticles.length);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActiveIndex((prev) =>
        prev <= 0 ? filteredArticles.length - 1 : prev - 1
      );
    } else if (e.key === "Enter" && activeIndex >= 0) {
      e.preventDefault();
      const selected = filteredArticles[activeIndex];
      setSearchTerm(selected.title);
      setShowSuggestions(false);
      const route =
        selected.route || staticArticleRoutes[selected.title.toLowerCase()];
      if (route) navigate(route);
    }
  };

  const handleEdit = (articleId) => {
    navigate(`/editor?mode=edit&id=${articleId}`);
  };

  async function handleDelete(articleId) {
    try {
      setIsLoading(true);
      await apiInstance.delete(`/articles/${articleId}`);
      setArticles((prev) => prev.filter((a) => a.id !== articleId));
setArticleToDelete(null);
safeToast("Article deleted successfully.", "success");

      setArticleToDelete(null);
    } catch (err) {
      console.error("Error deleting article:", err);
      safeToast("Failed to delete article.", "error");
    } finally {
      setIsLoading(false);
    }
  }

  const togglePublish = async (articleId, currentStatus) => {
    try {
      setIsLoading(true);
      await apiInstance.patch(
        `/articles/${articleId}/publish?published=${!currentStatus}`
      );
      await fetchArticles();
      await fetchPublicArticles();
      safeToast(currentStatus ? "Article unpublished." : "Article published.", "info");
    } catch (error) {
      console.error("Failed to toggle publish status:", error);
      safeToast("Failed to change publish status.", "error");
    } finally {
      setIsLoading(false);
    }
  };

  const paginatedArticles = articles.slice(
    (articlePage - 1) * ARTICLES_PER_PAGE,
    articlePage * ARTICLES_PER_PAGE
  );

  const paginatedPublicArticles = combinedPublicArticles.slice(
    (publicPage - 1) * ARTICLES_PER_PAGE,
    publicPage * ARTICLES_PER_PAGE
  );

  return (
    <div className="dashboard-wrapper">
      <header className="dashboard-header">
        <Link to="/" className="logo">
          ThoughtNest
        </Link>
      </header>

      <div className="logout-container">
        <button
          className="logout"
          onClick={() => {
            localStorage.clear();
            navigate("/login");
          }}
        >
          Log Out
        </button>
      </div>

      <main className="dashboard-container">
        <h1 className="dashboard-title">Your Dashboard</h1>

        <div className="top-actions">
          <button
            className="new-article"
            onClick={() => navigate("/editor?mode=create")}
            disabled={isLoading}
          >
            {isLoading ? (
              "Creating..."
            ) : (
              <>
                <span className="plus-icon">+</span> Create New Article
              </>
            )}
          </button>

          <div className="search-bar-container">
            <div className="search-input-wrapper">
              <input
                type="text"
                className="search-box"
                placeholder="Search your articles..."
                value={searchTerm}
                onChange={handleSearchInputChange}
                onKeyDown={handleKeyDown}
                onBlur={() => setTimeout(() => setShowSuggestions(false), 100)}
                onFocus={() => setShowSuggestions(true)}
              />
              {searchTerm && showSuggestions && (
                <ul className="search-dropdown">
                  {filteredArticles.length > 0 ? (
                    filteredArticles.map((article, index) => (
                      <li key={article.id}>
                        <span
                          className={`suggestion-item ${
                            index === activeIndex ? "active-suggestion" : ""
                          }`}
                          onMouseDown={() =>
                            handleSearchSuggestionClick(article.title)
                          }
                        >
                          {article.title}
                        </span>
                      </li>
                    ))
                  ) : (
                    <li className="no-match">No suggested articles</li>
                  )}
                </ul>
              )}
            </div>
            <button className="search-btn" onClick={handleSearchButtonClick}>
              🔍
            </button>
          </div>
        </div>

        <section className="articles-section">
          <h2>Your Articles</h2>
          {articles.length === 0 && !searchTerm ? (
            <div className="empty-state-inline">
              <img
                src="/assets/empty-state.png"
                alt="No articles yet"
                className="empty-img-inline"
              />
              <span className="empty-msg-inline">
                No articles yet! Start by writing one 📝
              </span>
            </div>
          ) : (
            <>
            <div className="dashboard-scroll-wrapper">
              <div className="table-container">
                <table className="articles-table">
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Published</th>
                      <th>Date of Creation</th>
                      <th>Last Modified</th>
                      <th>Author</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {paginatedArticles.map((article) => (
                      <tr key={article.id}>
                        <td>
                          <span
                            className="article-link"
                            style={{ cursor: "pointer", color: "#3b82f6" }}
                            onClick={() => navigate(`/article/${article.id}`)}
                          >
                            {article.title}
                          </span>
                        </td>
                        <td>{article.published ? "Yes" : "No"}</td>
                        <td>{formatDate(article.date)}</td>
                         <td>{formatDateTime(article.lastModifiedDate)}</td>
                        <td>{article.authorUsername}</td>
                        <td className="actions">
                          <div className="action-buttons">
                          <button
                            className="edit-btn"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleEdit(article.id);
                            }}
                            disabled={isLoading}
                          >
                            Edit
                          </button>
                          <button
                            className="delete-btn"
                            onClick={(e) => {
                              e.stopPropagation();
                              setArticleToDelete(article.id);
                            }}
                            disabled={isLoading}
                          >
                            Delete
                          </button>
                          <button
                            className="publish-btn"
                            onClick={() =>
                              togglePublish(article.id, article.published)
                            }
                            disabled={isLoading}
                          >
                            {isLoading
                              ? article.published
                                ? "Unpublishing..."
                                : "Publishing..."
                              : article.published
                              ? "Unpublish"
                              : "Publish"}
                          </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              </div>
              <div className="pagination-controls">
                <button
                  disabled={articlePage === 1}
                  onClick={() => setArticlePage(articlePage - 1)}
                >
                  Previous
                </button>
                <span>Page {articlePage}</span>
                <button
                  disabled={
                    articlePage >= Math.ceil(articles.length / ARTICLES_PER_PAGE)
                  }
                  onClick={() => setArticlePage(articlePage + 1)}
                >
                  Next
                </button>
              </div>
            </>
          )}
        </section>

        <section className="articles-section">
          <h2>Public Articles</h2>
          {combinedPublicArticles.length === 0 ? (
            <p>No public articles available.</p>
          ) : (
            <>
              <div className="dashboard-scroll-wrapper">
              <div className="table-container">
                <table className="articles-table">
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Author</th>
                    </tr>
                  </thead>
                  <tbody>
                    {paginatedPublicArticles.map((article) => (
                      <tr key={article.id}>
                        <td>
                          {article.isStatic ? (
                            <Link to={article.route} className="article-link">
                              {article.title}
                            </Link>
                          ) : (
                            <Link
                              to={`/article/${article.id}`}
                              className="article-link"
                            >
                              {article.title}
                            </Link>
                          )}
                        </td>
                        <td>{article.authorUsername}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              </div>
              <div className="pagination-controls">
                <button
                  disabled={publicPage === 1}
                  onClick={() => setPublicPage(publicPage - 1)}
                >
                  Previous
                </button>
                <span>Page {publicPage}</span>
                <button
                  disabled={
                    publicPage >=
                    Math.ceil(combinedPublicArticles.length / ARTICLES_PER_PAGE)
                  }
                  onClick={() => setPublicPage(publicPage + 1)}
                >
                  Next
                </button>
              </div>
            </>
          )}
        </section>

        {articleToDelete && (
          <div className="modal-overlay">
            <div className="confirmation-modal">
              <p>Are you sure you want to delete this article?</p>
              <div className="modal-actions">
                <button
                  className="confirm-delete"
                  onClick={() => handleDelete(articleToDelete)}
                  disabled={isLoading}
                >
                  {isLoading ? "Deleting..." : "Yes"}
                </button>
                <button
                  className="cancel-delete"
                  onClick={() => setArticleToDelete(null)}
                  disabled={isLoading}
                >
                  No
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
