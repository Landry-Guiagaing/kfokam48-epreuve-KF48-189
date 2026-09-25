import { useState } from 'react';
import { call } from '../api.js';

export default function Examinateur({ baseUrl, onResult }) {
  const [session, setSession] = useState({ titre: '', promotionId: '' });
  const [clotureId, setClotureId] = useState('');
  const [promotionId, setPromotionId] = useState('');
  const [tableau, setTableau] = useState(null);

  const submitSession = async (e) => {
    e.preventDefault();
    const { data } = await call(baseUrl, 'POST', '/api/sessions', {
      titre: session.titre,
      promotionId: Number(session.promotionId)
    });
    onResult(data);
  };

  const submitCloture = async (e) => {
    e.preventDefault();
    const { data } = await call(baseUrl, 'POST', `/api/sessions/${Number(clotureId)}/cloture`);
    onResult(data);
  };

  const submitTableau = async (e) => {
    e.preventDefault();
    const { ok, data } = await call(baseUrl, 'GET', `/api/tableau?promotionId=${Number(promotionId)}`);
    onResult(data);
    setTableau(ok && Array.isArray(data) ? data : null);
  };

  return (
    <div>
      <h2>Ouvrir une session</h2>
      <form onSubmit={submitSession}>
        <input
          placeholder="Titre"
          value={session.titre}
          onChange={(e) => setSession({ ...session, titre: e.target.value })}
          required
        />
        <input
          placeholder="Promotion ID"
          type="number"
          value={session.promotionId}
          onChange={(e) => setSession({ ...session, promotionId: e.target.value })}
          required
        />
        <button type="submit">Ouvrir</button>
      </form>

      <h2>Clôturer une session</h2>
      <form onSubmit={submitCloture}>
        <input
          placeholder="Session ID"
          type="number"
          value={clotureId}
          onChange={(e) => setClotureId(e.target.value)}
          required
        />
        <button type="submit">Clôturer</button>
      </form>

      <h2>Tableau d'une promotion</h2>
      <form onSubmit={submitTableau}>
        <input
          placeholder="Promotion ID"
          type="number"
          value={promotionId}
          onChange={(e) => setPromotionId(e.target.value)}
          required
        />
        <button type="submit">Afficher</button>
      </form>

      {tableau && (
        <table>
          <thead>
            <tr>
              <th>Étudiant</th>
              <th>Nom</th>
              <th>Présences</th>
              <th>Exercices</th>
              <th>Moyenne</th>
              <th>Relectures en attente</th>
            </tr>
          </thead>
          <tbody>
            {tableau.map((l) => (
              <tr key={l.etudiantId}>
                <td>{l.etudiantId}</td>
                <td>{l.nom ?? ''}</td>
                <td>{l.presences}</td>
                <td>{l.exercicesDeposes}</td>
                <td>{l.moyenne ?? ''}</td>
                <td>{l.relecturesEnAttente}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
