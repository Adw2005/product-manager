import React, { useEffect, useState } from "react";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Settings from "./pages/Settings";

function App() {
  const [user, setUser] = useState(null);
  const [checking, setChecking] = useState(true);
  const [page, setPage] = useState("dashboard");

  useEffect(() => {
    const token = localStorage.getItem("pm_token");
    const storedUser = localStorage.getItem("pm_user");
    if (token && storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setChecking(false);
  }, []);

  const handleLoginSuccess = (data) => {
    setUser({ username: data.username, role: data.role });
  };

  const handleLogout = () => {
    localStorage.removeItem("pm_token");
    localStorage.removeItem("pm_user");
    setUser(null);
    setPage("dashboard");
  };

  if (checking) return null;

  if (!user) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  if (page === "settings") {
    return <Settings onNavigate={setPage} onLogout={handleLogout} user={user} />;
  }

  return <Dashboard onNavigate={setPage} onLogout={handleLogout} user={user} />;
}

export default App;
