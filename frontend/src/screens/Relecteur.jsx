import { useState } from 'react';
import { call } from '../api.js';

export default function Relecteur({ baseUrl, onResult }) {
  const [form, setForm] = useState({ exerciceId: '', note: '', commentaire: '' });

  const submit = async (e) => {
    e.preventDefault();
    const { data } = await call(baseUrl, 'POST', `/api/lectures/${Number(form.exerciceId)}`, {
      note: Number(form.note),
      commentaire: form.commentaire || null
    });
    onResult(data);
  };

  return (
    <div>
      <h2>Rendre une relecture</h2>
      <form onSubmit={submit}>
        <input
          placeholder="Exercice ID"
          type="number"
          value={form.exerciceId}
          onChange={(e) => setForm({ ...form, exerciceId: e.target.value })}
          required
        />
        <input
          placeholder="Note (0-20)"
          type="number"
          min="0"
          max="20"
          value={form.note}
          onChange={(e) => setForm({ ...form, note: e.target.value })}
          required
        />
        <input
          placeholder="Commentaire (optionnel)"
          value={form.commentaire}
          onChange={(e) => setForm({ ...form, commentaire: e.target.value })}
        />
        <button type="submit">Envoyer</button>
      </form>
    </div>
  );
}
