// Import React and routing utilities
import React from "react";
import { Navigate, useLocation } from "react-router-dom";

// Import authentication check helper
import { isLoggedIn } from "../scripts/auth";

/**
 * PrivateRoute component protects routes that require authentication.
 * If the user is not logged in, it redirects them to the login page.
 * After successful login, they can be redirected back to the originally intended route.
 * 
 * @param {Object} props
 * @param {ReactNode} props.children - The component(s) to render if authenticated
 */
export default function PrivateRoute({ children }) {
  // Get current route location to preserve the redirect target
  const location = useLocation();

  // If user is not authenticated, redirect them to the login page
  if (!isLoggedIn()) {
    return (
      <Navigate
        to={`/login?redirect=${encodeURIComponent(location.pathname)}`} // Append redirect path as query param
        replace // Prevents the previous route from being preserved in history
      />
    );
  }

  // If user is logged in, allow access to the protected content
  return children;
}
