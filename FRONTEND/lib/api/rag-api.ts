// RAG Agent API client
// Base: http://localhost:8000

const RAG_BASE = "http://localhost:8000";

export interface RagAskResponse {
  question: string;
  answer: string;
  sources: string[];
  success: boolean;
  error?: string;
}

export interface RagDocument {
  type: string;
  institution: string;
  source: string;
}

export interface RagDocumentsResponse {
  success: boolean;
  documents: Record<string, RagDocument>;
  total: number;
}

/** Post a single question to the RAG endpoint */
export async function ragAsk(question: string): Promise<RagAskResponse> {
  const res = await fetch(
    `${RAG_BASE}/api/rag/ask?${new URLSearchParams({ question })}`,
    { method: "POST", signal: AbortSignal.timeout(30_000) }
  );
  if (!res.ok) {
    throw new Error(`RAG API error: ${res.status}`);
  }
  return res.json();
}

/** Get the list of indexed documents */
export async function ragDocuments(): Promise<RagDocumentsResponse> {
  const res = await fetch(`${RAG_BASE}/api/rag/documents`, {
    signal: AbortSignal.timeout(8_000),
  });
  if (!res.ok) throw new Error(`RAG API error: ${res.status}`);
  return res.json();
}

/** Health check */
export async function ragHealth(): Promise<boolean> {
  try {
    const res = await fetch(`${RAG_BASE}/health`, {
      signal: AbortSignal.timeout(3_000),
    });
    return res.ok;
  } catch {
    return false;
  }
}
