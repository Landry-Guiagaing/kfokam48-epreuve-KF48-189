export async function call(baseUrl, method, path, body) {
  try {
    const res = await fetch(baseUrl.replace(/\/$/, '') + path, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined
    });
    const text = await res.text();
    let data;
    try { data = JSON.parse(text); } catch { data = text; }
    if (!res.ok) return { ok: false, data: { status: res.status, error: data } };
    return { ok: true, data };
  } catch (e) {
    return { ok: false, data: { error: String(e) } };
  }
}
