import { HealthCheck } from './components/HealthCheck';
import './styles/App.css';

/**
 * Componente principal de la aplicación
 * Muestra el comparador de precios de farmacias
 */
function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>🩺 Farmacompara</h1>
        <p className="app-subtitle">Comparador de precios de farmacias en Villahermosa</p>
      </header>

      <main className="app-main">
        <HealthCheck />
      </main>

      <footer className="app-footer">
        <p>Proyecto de ejemplo - Fase 1: Cimientos</p>
      </footer>
    </div>
  );
}

export default App;