# ThoughtNest – Frontend

ThoughtNest is a calm, thoughtful platform designed to let users write, read, and reflect on ideas. This frontend is built using React and is paired with a secure Spring Boot backend. It offers a smooth, focused experience for reading articles, creating posts, and managing personal accounts.


## Project Overview

This repository contains the complete frontend for the ThoughtNest blog application.

It handles:

- Page routing and navigation
- Article viewing and writing
- User authentication and authorization flows
- Account management and contact submissions
- Secure and styled user dashboard
- Responsive design for desktop and mobile

Every component has been thoughtfully structured for clarity, consistency, and usability.


## Technologies Used

- React (with JSX)
- Vite (for fast development and optimized builds)
- React Router DOM (routing)
- Axios (for communicating with the backend)
- React Quill (rich text editor for article creation)
- React Toastify (non-intrusive toast notifications)
- LocalStorage (for storing tokens and user session)
- Pure CSS (custom styling, responsive layouts)

---

## Folder Structure (Summary)

src/
├── components/ // Shared utility components
│ └── PrivateRoute.jsx // Route protection for authenticated views
├── pages/ // Main application pages
│ ├── HomePage.jsx
│ ├── LoginPage.jsx
│ ├── Signup.jsx
│ ├── DashboardPage.jsx
│ ├── EditorPage.jsx
│ ├── ContactPage.jsx
│ ├── MyAccountPage.jsx
│ └── ArticleViewPage.jsx
├── scripts/ // Helper utilities
│ ├── api.js
│ ├── apiInstance.js
│ └── auth.js
├── styles/ // Custom CSS
│ ├── login.css
│ ├── signup.css
│ ├── account.css
│ ├── article.css
│ └── index.css
├── App.jsx // Main app router
├── main.jsx // Entry point for ReactDOM
└── index.html // Static HTML entry point


## Key Features

- Clean and elegant homepage with animated article carousel
- Article creation with rich formatting and image support
- Private dashboard for managing published and unpublished articles
- Toast messages for immediate, consistent user feedback
- Responsive design for both desktop and mobile screens
- Account editing with field-level update and feedback
- Password visibility toggle, loading states, and redirect logic

---

## Authentication Flow

- On signup or login, tokens and user data are stored in localStorage
- All API requests use an Axios interceptor to include the token automatically
- Authenticated routes like the dashboard are protected by `PrivateRoute`
- If an unauthenticated user tries to access a restricted page, they are redirected to the login page with a redirect path preserved

---

## How to Run Locally

1. Make sure you have **Node.js** and **npm** installed.
2. Clone the repository:

   git clone https://github.com/your-username/thoughtnest-frontend.git
   cd thoughtnest-frontend
Install dependencies:


npm install
Start the development server:

npm run dev
Open your browser and visit:

http://localhost:5173
Ensure your backend is running on http://localhost:8080 for full functionality.

Deployment Notes
Update the base URL in apiInstance.js to point to your deployed backend endpoint

Build the project for production using:

npm run build
Deploy the dist/ folder using your preferred static hosting service (e.g., Vercel, Netlify, or Nginx)

## Legal and Ownership Notice

This project is created and owned by **Harinee Anandh**.

All code, structure, styling, and logic belong exclusively to the author.
This work must not be copied, reused, republished, or redistributed under any circumstance.

Any unauthorized use or duplication will be treated as a violation of intellectual property rights and may result in legal consequences.

## Contact
If you wish to reach out regarding this work, collaboration, or clarification, please use the contact form inside the app or reach out via appropriate professional channels.

## Crafted with originality and purpose by **Harinee Anandh**

