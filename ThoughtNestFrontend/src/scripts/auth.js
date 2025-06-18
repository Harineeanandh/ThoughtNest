// Define keys used in localStorage for consistent access
const TOKEN_KEY = "token";
const USERNAME_KEY = "username";
const LOGGED_IN_KEY = "loggedIn";

/**
 * Stores the authentication token and username in localStorage.
 * Also marks the user as logged in.
 * 
 * @param {string} token - JWT or session token
 * @param {string} username - Username of the logged-in user
 */
export function saveAuth(token, username) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USERNAME_KEY, username);
  localStorage.setItem(LOGGED_IN_KEY, "true");
}

/**
 * Clears all authentication-related data from localStorage.
 * Used when user logs out or session expires.
 */
export function logout() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USERNAME_KEY);
  localStorage.removeItem(LOGGED_IN_KEY);
}

/**
 * Checks if the user is marked as logged in.
 * 
 * @returns {boolean} True if user is logged in, false otherwise.
 */
export function isLoggedIn() {
  return localStorage.getItem(LOGGED_IN_KEY) === "true";
}

/**
 * Retrieves the stored authentication token from localStorage.
 * 
 * @returns {string|null} The token if present, otherwise null.
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

/**
 * Retrieves the stored username from localStorage.
 * 
 * @returns {string|null} The username if present, otherwise null.
 */
export function getUsername() {
  return localStorage.getItem(USERNAME_KEY);
}

/**
 * Utility to generate an Authorization header for authenticated requests.
 * 
 * @returns {Object} Object with Authorization header if token exists, otherwise an empty object.
 */
export function getAuthHeaders() {
  const token = getToken();
  return token ? { Authorization: "Bearer " + token } : {};
}
