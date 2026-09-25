import { useState } from 'react';
import Etudiant from './screens/Etudiant.jsx';
import Relecteur from './screens/Relecteur.jsx';
import Examinateur from './screens/Examinateur.jsx';

const SCREENS = {
  etudiant: { label: 'Étudiant', Component: Etudiant },
  relecteur: { label: 'Relecteur', Component: Relecteur },
  examinateur: { label: 'Examinateur', Component: Examinateur }
};

export default function App() {
  const [baseUrl, setBaseUrl] = useState('http://localhost:8080');
  const [screen, setScreen] = useState('etudiant');
  const [result, setResult] = useState(null);

  const { Component } = SCREENS[screen];

  return (
    <>
      <h1>Kfokam48 — Frontend</h1>

      <div className="row">
        <label htmlFor="baseUrl">API base URL :</label>
        <input id="baseUrl" value={baseUrl} onChange={(e) => setBaseUrl(e.target.value)} />
      </div>

      <div className="tabs">
        {Object.entries(SCREENS).map(([key, { label }]) => (
          <button
            key={key}
            className={'tab-btn' + (screen === key ? ' active' : '')}
            onClick={() => setScreen(key)}
          >
            {label}
          </button>
        ))}
      </div>

      <Component baseUrl={baseUrl} onResult={setResult} />

      <h2>Résultat brut</h2>
      <pre>{result ? JSON.stringify(result, null, 2) : '—'}</pre>
    </>
  );
}
