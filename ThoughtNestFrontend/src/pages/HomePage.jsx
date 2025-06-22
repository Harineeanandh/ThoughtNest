// Author: Harinee Anandh
import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/index.css"; // Global stylesheet for layout and styling

export default function HomePage() {
  // Controls visibility of the mobile dropdown menu
  const [dropdownVisible, setDropdownVisible] = useState(false);

  // Tracks user login status
  const [loggedIn, setLoggedIn] = useState(false);

  // Hook used to programmatically navigate between routes
  const navigate = useNavigate();

  // Ref pointing to the article carousel container, used for horizontal scrolling
  const carouselRef = useRef(null);



  // On component mount, check if a user is logged in by reading from localStorage
  useEffect(() => {
    setLoggedIn(localStorage.getItem("loggedIn") === "true");
  }, []);

  
  // Toggle visibility of the dropdown menu
  const toggleDropdown = () => {
    setDropdownVisible(!dropdownVisible);
  };

  // Handle user logout by clearing relevant items from localStorage
  const handleLogout = () => {
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("username");
    localStorage.removeItem("token");
    localStorage.removeItem("visitedBefore");
    setLoggedIn(false); // Reset local state
    navigate("/"); // Redirect to homepage
  };

  // Handles click on any article card
  const handleArticleClick = (e, articlePath) => {
    e.preventDefault(); // Prevent default anchor navigation

    if (loggedIn) {
      // If logged in, navigate directly to article
      navigate(articlePath);
    } else {
      // If not logged in, determine if user is visiting for the first time
      const visitedBefore = localStorage.getItem("visitedBefore") === "true";

      if (!visitedBefore) {
        // First-time visitor: redirect to signup with article path saved
        localStorage.setItem("visitedBefore", "true");
        navigate(`/signup?redirect=${encodeURIComponent(articlePath)}`);
      } else {
        // Returning visitor: redirect to login instead
        navigate(`/login?redirect=${encodeURIComponent(articlePath)}`);
      }
    }
  };

  // Scrolls the carousel left or right when arrow buttons are clicked
  const scrollCarousel = (direction) => {
    if (carouselRef.current) {
      const scrollAmount = 300; // Number of pixels to scroll
      carouselRef.current.scrollBy({
        left: direction === "left" ? -scrollAmount : scrollAmount,
        behavior: "smooth", // Smooth animation effect
      });
    }
  };
 
  return (
    <>
      {/* Top navigation header */}
      <header className="navbar">
        <h1 className="logo">ThoughtNest</h1>
      </header>

      {/* Main welcome message section */}
      <main className="blog-container">
      <div className="intro-zoom-wrapper">
        <section
  className="intro-section"
>
        <div className="intro">
            <h1>Hi there! Welcome to ThoughtNest!</h1>
            <p>
              A cozy space for thoughts, stories, and sparks of inspiration.
              <br /><br />
              This platform lets you explore a growing collection of personal
              articles, from quiet musings to insightful reflections. If you
              feel inspired, log in to share your own voice and join the
              conversation.
              <br /><br />
              Write, read, and connect with authenticity!
            </p>
          </div>
        </section>
      </div>
      </main>

      {/* Horizontally scrollable article carousel */}
      <div className="carousel-wrapper">
        {/* Left scroll arrow */}
        <button className="arrow left" onClick={() => scrollCarousel("left")}>&lt;</button>

        {/* Carousel container with all article cards */}
        <div
          className="carousel"
          ref={carouselRef}
          style={{
            display: "flex",
            overflowX: "auto",
            scrollBehavior: "smooth"
          }}
        >
          {/* Each card links to a specific article. Access is gated based on login status */}
          <a
            href="/articles/article1"
            onClick={(e) => handleArticleClick(e, "/articles/article1")}
            className="blog-link"
          >
            <article className="blog-card">
              <img src="assets/meat.svg" alt="Eating habits" />
              <span className="tag">Lifestyle</span>
              <h3 className="title">Guilty eating meat?!</h3>
              <p className="date">March 20, 2024</p>
            </article>
          </a>

          <a
            href="/articles/article2"
            onClick={(e) => handleArticleClick(e, "/articles/article2")}
            className="blog-link"
          >
            <article className="blog-card">
              <img src="assets/ego.jpg" alt="Ego??" />
              <span className="tag travel">Philosophy</span>
              <h3 className="title">Is ego a human thing or do animals have it too?</h3>
              <p className="date">March 18, 2024</p>
            </article>
          </a>

          <a
            href="/articles/article3"
            onClick={(e) => handleArticleClick(e, "/articles/article3")}
            className="blog-link"
          >
            <article className="blog-card">
              <img src="assets/TV.svg" alt="Wondered about this??" />
              <span className="tag travel">Tech</span>
              <h3 className="title">What OS does a TV have?</h3>
              <p className="date">March 12, 2024</p>
            </article>
          </a>

          <a
            href="/articles/article4"
            onClick={(e) => handleArticleClick(e, "/articles/article4")}
            className="blog-link"
          >
            <article className="blog-card">
              <img src="assets/dreaming.svg" alt="Dreams??" />
              <span className="tag">Psychology</span>
              <h3 className="title">Nightmares are good??!</h3>
              <p className="date">March 12, 2024</p>
            </article>
          </a>

          <a
            href="/articles/article5"
            onClick={(e) => handleArticleClick(e, "/articles/article5")}
            className="blog-link"
          >
            <article className="blog-card">
              <img src="assets/universe.svg" alt="Universe" />
              <span className="tag">Science</span>
              <h3 className="title">Is the universe determinitistic?</h3>
              <p className="date">March 8, 2024</p>
            </article>
          </a>

          <a
            href="/articles/article6"
            onClick={(e) => handleArticleClick(e, "/articles/article6")}
            className="blog-link"
          >
            <article className="blog-card">
              <img src="assets/bd.svg" alt="Kindness?" />
              <span className="tag travel">Psychology</span>
              <h3 className="title">Benefit of Doubt: Against intuition?</h3>
              <p className="date">March 5, 2024</p>
            </article>
          </a>
        </div>

        {/* Right scroll arrow */}
        <button className="arrow right" onClick={() => scrollCarousel("right")}>&gt;</button>
      </div>

      {/* Responsive dropdown menu (hamburger) for navigation */}
      <div className="menu-container">
        {/* Hamburger icon for toggling the dropdown menu */}
        <div
          className="hamburger"
          onClick={toggleDropdown}
          style={{ cursor: "pointer" }}
        >
          &#9776;
        </div>

        {/* Dropdown menu: visibility controlled by dropdownVisible state */}
        <div className={`dropdown ${dropdownVisible ? "" : "hidden"}`}>
          {loggedIn ? (
            <>
              <Link to="/account">My account</Link>
              <Link to="/dashboard">Explore ThoughtNest</Link>
              <Link to="/contact">Contact Us</Link>
              <button onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/signup">Signup / Login</Link>
              <Link to="/contact">Contact</Link>
              <Link to="/dashboard">Explore ThoughtNest</Link>
            </>
          )}
        </div>
      </div>
    </>
  );
}
