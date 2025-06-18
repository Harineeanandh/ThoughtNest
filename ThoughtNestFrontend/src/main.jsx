// Import core React library
import React from 'react';

// Import the ReactDOM client to handle rendering to the DOM
import ReactDOM from 'react-dom/client';

// Import the main App component which contains the entire app structure and routing
import App from './App';

// React 18+ root rendering API
// Creates a root-level React rendering context inside the <div id="root"></div> in index.html
ReactDOM.createRoot(document.getElementById('root')).render(
  // StrictMode is a tool for highlighting potential problems in development
  // It doesn’t affect the production build
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
