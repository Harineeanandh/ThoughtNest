// Import the configured Axios instance for making API requests
import API from "./apiInstance";

/**
 * Registers a new user.
 * Sends a POST request to /auth/signup with the provided username, email, and password.
 * 
 * @param {Object} param0 - Object containing username, email, and password
 * @returns {Object} response.data - The response payload from the backend
 * @throws Will throw if the request fails
 */
export async function signup({ username, email, password }) {
  try {
    const response = await API.post("/auth/signup", {
      username,
      email,
      password,
    });
    return response.data;
  } catch (error) {
    throw error; // Propagate the error to be handled in the calling component
  }
}

/**
 * Logs in a user.
 * Sends a POST request to /auth/login with the provided identifier (email/username) and password.
 * 
 * @param {Object} param0 - Object containing identifier and password
 * @returns {Object} response.data - The response payload (usually includes token)
 * @throws Will throw if login fails
 */
export async function login({ identifier, password }) {
  try {
    const response = await API.post("/auth/login", {
      identifier,
      password,
    });
    return response.data;
  } catch (error) {
    throw error; // Let the calling code handle the error
  }
}

/**
 * Logs out the current user.
 * Clears authentication-related data from localStorage.
 */
export function logout() {
  localStorage.removeItem("loggedIn");
  localStorage.removeItem("token");
  localStorage.removeItem("username");
}

/**
 * Checks if the user is currently authenticated.
 * @returns {boolean} True if a token is present in localStorage, false otherwise.
 */
export function isAuthenticated() {
  return localStorage.getItem("token") !== null;
}
