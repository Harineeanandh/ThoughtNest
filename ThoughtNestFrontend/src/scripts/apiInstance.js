// Import Axios library to make HTTP requests
import axios from "axios";

// Use Vite env variable for base URL, fallback to localhost if not set
const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

// Create a reusable Axios instance with a predefined base URL
const apiInstance = axios.create({
  baseURL, // All requests made using this instance will be prefixed with this base URL
});

// Add a request interceptor to automatically attach the auth token to each request
apiInstance.interceptors.request.use(
  (config) => {
    // Get the token from localStorage
    const token = localStorage.getItem("token");

    // If token exists, include it in the Authorization header of the request
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    // Return the modified request config
    return config;
  },
  // If there's an error in setting up the request, reject it
  (error) => Promise.reject(error)
);

// Export the configured instance for use in API calls
export default apiInstance;
