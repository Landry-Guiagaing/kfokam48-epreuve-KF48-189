import { useState } from 'react';
import { call } from '../api.js';

export default function Etudiant({ baseUrl, onResult }) {
  const [presence, setPresence] = useState({ code: '', etudiantId: '' });
  const [exercice, setExercice] = useState({ sessionId: '', etudiantId: '', lien: '' });
  const [consultId, setConsultId] = useState('');

  const submitPresence = async (e) => {
    e.preventDefault();
    const { data } = await call(baseUrl, 'POST', '/api/presences', {
      code: presence.code,
      etudiantId: Number(presence.etudiantId)
    });
    onResult(data);
  };

  const submitExercice = async (e) => {
    e.preventDefault();
    const { data } = await call(baseUrl, 'POST', '/api/exercices', {
      sessionId: Number(exercice.sessionId),
      etudiantId: Number(exercice.etudiantId),
      lien: exercice.lien
    });
    onResult(data);
  };

  const submitConsult = async (e) => {
    e.preventDefault();
    const { data } = await call(baseUrl, 'GET', `/api/exercices/${Number(consultId)}`);
    onResult(data);
  };

  return (
    <div>
      <h2>Marquer ma présence</h2>
      <form onSubmit={submitPresence}>
        <input
          placeholder="Code de session"
          value={presence.code}
          onChange={(e) => setPresence({ ...presence, code: e.target.value })}
          required
        />
        <input
          placeholder="Mon étudiant ID"
          type="number"
          value={presence.etudiantId}
          onChange={(e) => setPresence({ ...presence, etudiantId: e.target.value })}
          required
        />
        <button type="submit">Marquer</button>
      </form>

      <h2>Déposer un exercice</h2>
      <form onSubmit={submitExercice}>
        <input
          placeholder="Session ID"
          type="number"
          value={exercice.sessionId}
          onChange={(e) => setExercice({ ...exercice, sessionId: e.target.value })}
          required
        />
        <input
          placeholder="Mon étudiant ID"
          type="number"
          value={exercice.etudiantId}
          onChange={(e) => setExercice({ ...exercice, etudiantId: e.target.value })}
          required
        />
        <input
          placeholder="Lien"
          value={exercice.lien}
          onChange={(e) => setExercice({ ...exercice, lien: e.target.value })}
          required
        />
        <button type="submit">Déposer</button>
      </form>

      <h2>Consulter mon exercice</h2>
      <form onSubmit={submitConsult}>
        <input
          placeholder="Exercice ID"
          type="number"
          value={consultId}
          onChange={(e) => setConsultId(e.target.value)}
          required
        />
        <button type="submit">Consulter</button>
      </form>
    </div>
  );
}
