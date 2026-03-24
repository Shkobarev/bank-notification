import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import ClientsPage from './pages/ClientsPage';
import ClientFormPage from './pages/ClientFormPage'
import ClientDetailsPage from "./pages/ClientDetailsPage";

function App() {
  return (
      <BrowserRouter>
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
          <div className="container">
            <span className="navbar-brand">
                Тестовый UI для проверки API
            </span>
            <div className="navbar-nav">
              <Link className="nav-link" to="/clients">Клиенты</Link>
            </div>
          </div>
        </nav>

        <div className="container mt-4">
          <Routes>
              <Route path="/" element={
                  <div className="text-center mt-5">
                      <h1>Добро пожаловать!</h1>
                      <Link to="/clients" className="btn btn-primary btn-lg mt-3">
                          Перейти к клиентам
                      </Link>
                  </div>
              } />
              <Route path="/clients" element={<ClientsPage />} />
              <Route path="/clients/new" element={<ClientFormPage />} />
              <Route path="/clients/:id" element={<ClientDetailsPage />} />
          </Routes>
        </div>
      </BrowserRouter>
  );
}

export default App;
