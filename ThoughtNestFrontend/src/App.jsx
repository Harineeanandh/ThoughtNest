// Core React library import
import React from "react";

// React Router components for navigation
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

// Toast container for displaying toast notifications
import { ToastContainer } from "react-toastify";

// Main pages
import HomePage from "./pages/HomePage";
import Article1 from "./pages/articles/Article1";
import Article2 from "./pages/articles/Article2";
import Article3 from "./pages/articles/Article3";
import Article4 from "./pages/articles/Article4";
import Article5 from "./pages/articles/Article5";
import Article6 from "./pages/articles/Article6";
import Signup from "./pages/Signup";
import LoginPage from "./pages/LoginPage";
import Contact from "./pages/ContactPage";
import DashboardPage from "./pages/DashboardPage";
import EditorPage from "./pages/EditorPage";
import ArticleViewPage from "./pages/ArticleViewPage";
import MyAccountPage from "./pages/MyAccountPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";

// Component to protect routes requiring login
import PrivateRoute from "./components/PrivateRoute";

function App() {
  return (
    <>
      {/* ToastContainer renders system-wide toast notifications.
          Placing it outside Router ensures it's always mounted immediately */}
      <ToastContainer
        position="top-right"         // Position on screen
        autoClose={3000}             // Auto-close toast after 3 seconds
        hideProgressBar={false}      // Show progress bar
        closeOnClick                 // Close on click
        pauseOnHover                 // Pause auto-close on hover
        draggable                    // Allow manual dragging
        theme="colored"              // Colored toast theme
      />

      <Router>
        <Routes>
          {/* Public route: Home page */}
          <Route path="/" element={<HomePage />} />

          {/* Static article pages (can be updated to dynamic in future) */}
          <Route path="/articles/article1" element={<Article1 />} />
          <Route path="/articles/article2" element={<Article2 />} />
          <Route path="/articles/article3" element={<Article3 />} />
          <Route path="/articles/article4" element={<Article4 />} />
          <Route path="/articles/article5" element={<Article5 />} />
          <Route path="/articles/article6" element={<Article6 />} />

          {/* Authentication and user-related routes */}
          <Route path="/signup" element={<Signup />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/account" element={<MyAccountPage />} />

          {/* Contact form page */}
          <Route path="/contact" element={<Contact />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />

          {/* Article editor (create/edit page) */}
          <Route path="/editor" element={<EditorPage />} />

          {/* View an article dynamically using its ID */}
          <Route path="/article/:id" element={<ArticleViewPage />} />
          <Route path="/article-view/:id" element={<ArticleViewPage />} />

          {/* Protected route: dashboard only visible to authenticated users */}
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <DashboardPage />
              </PrivateRoute>
            }
          />
        </Routes>
      </Router>
    </>
  );
}

export default App;
