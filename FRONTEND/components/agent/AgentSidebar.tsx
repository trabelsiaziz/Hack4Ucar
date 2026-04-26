"use client";

import { useState, useRef, useEffect, useCallback } from "react";
import {
  Bot,
  X,
  Send,
  Loader2,
  FileText,
  ChevronDown,
  ChevronUp,
  Sparkles,
  AlertCircle,
  RefreshCw,
  Wifi,
  WifiOff,
  MessageSquare,
  Trash2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import { ragAsk, ragDocuments, ragHealth } from "@/lib/api/rag-api";
import type { RagAskResponse, RagDocument } from "@/lib/api/rag-api";

// ── Types ─────────────────────────────────────────────────────────────────────

interface Message {
  id: string;
  role: "user" | "assistant" | "error";
  content: string;
  sources?: string[];
  timestamp: Date;
}

const SUGGESTED_QUESTIONS = [
  "Quel est le taux d'absentéisme par département pour IHEC?",
  "Combien d'enseignants à Marketing?",
  "Quelles sont les notes d'EYA JABER?",
  "Résume les données ISSTE",
  "Quel est le taux de réussite académique?",
];

// ── Source chip ───────────────────────────────────────────────────────────────

function SourceChip({ source }: { source: string }) {
  const label = source.split("/").pop() ?? source;
  return (
    <span className="inline-flex items-center gap-1 rounded-full border border-sidebar-border bg-sidebar-accent/60 px-2 py-0.5 text-[10px] text-sidebar-foreground/70">
      <FileText className="h-2.5 w-2.5" />
      {label}
    </span>
  );
}

// ── Message bubble ────────────────────────────────────────────────────────────

function MessageBubble({ msg }: { msg: Message }) {
  const [sourcesOpen, setSourcesOpen] = useState(false);
  const isUser = msg.role === "user";
  const isError = msg.role === "error";

  return (
    <div className={cn("flex flex-col gap-1", isUser && "items-end")}>
      <div
        className={cn(
          "max-w-[88%] rounded-2xl px-3.5 py-2.5 text-sm leading-relaxed",
          isUser &&
            "rounded-tr-sm bg-sidebar-primary text-sidebar-primary-foreground",
          !isUser &&
            !isError &&
            "rounded-tl-sm bg-sidebar-accent/70 text-sidebar-foreground",
          isError &&
            "rounded-tl-sm border border-red-500/30 bg-red-500/10 text-red-400"
        )}
      >
        {isError && (
          <div className="mb-1 flex items-center gap-1.5 font-medium">
            <AlertCircle className="h-3.5 w-3.5" />
            <span className="text-xs">Erreur</span>
          </div>
        )}
        <span className="whitespace-pre-wrap">{msg.content}</span>
      </div>

      {/* Sources */}
      {msg.sources && msg.sources.length > 0 && (
        <div className="max-w-[88%] space-y-1">
          <button
            onClick={() => setSourcesOpen((o) => !o)}
            className="flex items-center gap-1 text-[10px] text-sidebar-foreground/50 transition-colors hover:text-sidebar-foreground/80"
          >
            <FileText className="h-2.5 w-2.5" />
            {msg.sources.length} source{msg.sources.length > 1 ? "s" : ""}
            {sourcesOpen ? (
              <ChevronUp className="h-2.5 w-2.5" />
            ) : (
              <ChevronDown className="h-2.5 w-2.5" />
            )}
          </button>
          {sourcesOpen && (
            <div className="flex flex-wrap gap-1">
              {msg.sources.map((s) => (
                <SourceChip key={s} source={s} />
              ))}
            </div>
          )}
        </div>
      )}

      <span className="text-[10px] text-sidebar-foreground/30">
        {msg.timestamp.toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        })}
      </span>
    </div>
  );
}

// ── Documents panel ───────────────────────────────────────────────────────────

function DocumentsPanel({
  docs,
}: {
  docs: Record<string, RagDocument> | null;
}) {
  if (!docs) return null;
  const entries = Object.entries(docs);
  if (entries.length === 0) return null;

  return (
    <div className="space-y-1.5 rounded-xl border border-sidebar-border bg-sidebar-accent/30 p-3">
      <p className="text-[10px] font-semibold uppercase tracking-wide text-sidebar-foreground/50">
        Documents indexés
      </p>
      {entries.map(([, doc]) => (
        <div
          key={doc.source}
          className="flex items-center gap-2 text-xs text-sidebar-foreground/70"
        >
          <FileText className="h-3 w-3 shrink-0 text-sidebar-primary" />
          <span className="truncate">{doc.source}</span>
          <Badge
            variant="outline"
            className="ml-auto shrink-0 border-sidebar-border text-[9px] text-sidebar-foreground/50"
          >
            {doc.type}
          </Badge>
        </div>
      ))}
    </div>
  );
}

// ── Main Agent Sidebar ────────────────────────────────────────────────────────

interface AgentSidebarProps {
  open: boolean;
  onClose: () => void;
}

export function AgentSidebar({ open, onClose }: AgentSidebarProps) {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "welcome",
      role: "assistant",
      content:
        "Bonjour ! Je suis votre assistant RAG. Posez-moi des questions sur les données de vos institutions (IHEC, ISSTE, EYA JABER).",
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isOnline, setIsOnline] = useState<boolean | null>(null);
  const [docs, setDocs] = useState<Record<string, RagDocument> | null>(null);
  const [showDocs, setShowDocs] = useState(false);

  const scrollRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const bottomRef = useRef<HTMLDivElement>(null);

  // ── Health check ────────────────────────────────────────────────────────────
  const checkHealth = useCallback(async () => {
    const ok = await ragHealth();
    setIsOnline(ok);
    if (ok) {
      try {
        const d = await ragDocuments();
        if (d.success) setDocs(d.documents);
      } catch {
        // docs fetch optional
      }
    }
  }, []);

  useEffect(() => {
    if (open) {
      checkHealth();
      inputRef.current?.focus();
    }
  }, [open, checkHealth]);

  // ── Auto-scroll ──────────────────────────────────────────────────────────────
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isLoading]);

  // ── Send message ─────────────────────────────────────────────────────────────
  const send = useCallback(
    async (question: string) => {
      const q = question.trim();
      if (!q || isLoading) return;

      const userMsg: Message = {
        id: `u-${Date.now()}`,
        role: "user",
        content: q,
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, userMsg]);
      setInput("");
      setIsLoading(true);

      try {
        const res: RagAskResponse = await ragAsk(q);
        const botMsg: Message = {
          id: `b-${Date.now()}`,
          role: res.success ? "assistant" : "error",
          content: res.success
            ? res.answer
            : res.error ?? "Une erreur s'est produite.",
          sources: res.sources ?? [],
          timestamp: new Date(),
        };
        setMessages((prev) => [...prev, botMsg]);
      } catch (err) {
        const errMsg: Message = {
          id: `e-${Date.now()}`,
          role: "error",
          content:
            "Impossible de joindre l'agent RAG. Vérifiez que le serveur est démarré sur le port 8000.",
          timestamp: new Date(),
        };
        setMessages((prev) => [...prev, errMsg]);
        setIsOnline(false);
      } finally {
        setIsLoading(false);
      }
    },
    [isLoading]
  );

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      send(input);
    }
  };

  const clearChat = () => {
    setMessages([
      {
        id: "welcome",
        role: "assistant",
        content:
          "Bonjour ! Je suis votre assistant RAG. Posez-moi des questions sur les données de vos institutions.",
        timestamp: new Date(),
      },
    ]);
  };

  // ── Render ──────────────────────────────────────────────────────────────────

  if (!open) return null;

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 z-40 bg-black/20 backdrop-blur-[2px] lg:hidden"
        onClick={onClose}
      />

      {/* Sidebar panel */}
      <aside
        className={cn(
          "fixed right-0 top-0 z-50 flex h-full w-[380px] max-w-[95vw] flex-col",
          "bg-sidebar text-sidebar-foreground",
          "border-l border-sidebar-border shadow-2xl",
          "transition-transform duration-300",
          open ? "translate-x-0" : "translate-x-full"
        )}
      >
        {/* ── Header ─────────────────────────────────────────────────────── */}
        <div className="flex items-center gap-3 border-b border-sidebar-border px-4 py-3.5">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-sidebar-primary/20">
            <Bot className="h-5 w-5 text-sidebar-primary" />
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2">
              <p className="font-semibold text-sm">Agent RAG</p>
              <Sparkles className="h-3 w-3 text-sidebar-primary" />
            </div>
            <div className="flex items-center gap-1.5 mt-0.5">
              {isOnline === null ? (
                <Loader2 className="h-2.5 w-2.5 animate-spin text-sidebar-foreground/40" />
              ) : isOnline ? (
                <>
                  <Wifi className="h-2.5 w-2.5 text-emerald-400" />
                  <span className="text-[10px] text-emerald-400">Connecté</span>
                </>
              ) : (
                <>
                  <WifiOff className="h-2.5 w-2.5 text-red-400" />
                  <span className="text-[10px] text-red-400">Hors-ligne</span>
                </>
              )}
            </div>
          </div>
          <div className="flex items-center gap-1">
            <Button
              variant="ghost"
              size="icon"
              className="h-7 w-7 text-sidebar-foreground/50 hover:text-sidebar-foreground hover:bg-sidebar-accent"
              onClick={checkHealth}
              title="Reconnecter"
            >
              <RefreshCw className="h-3.5 w-3.5" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-7 w-7 text-sidebar-foreground/50 hover:text-sidebar-foreground hover:bg-sidebar-accent"
              onClick={clearChat}
              title="Effacer la conversation"
            >
              <Trash2 className="h-3.5 w-3.5" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-7 w-7 text-sidebar-foreground/50 hover:text-sidebar-foreground hover:bg-sidebar-accent"
              onClick={onClose}
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        </div>

        {/* ── Documents toggle ────────────────────────────────────────────── */}
        {docs && (
          <button
            onClick={() => setShowDocs((s) => !s)}
            className="flex items-center justify-between border-b border-sidebar-border px-4 py-2 text-xs text-sidebar-foreground/60 transition-colors hover:bg-sidebar-accent/30 hover:text-sidebar-foreground"
          >
            <div className="flex items-center gap-1.5">
              <FileText className="h-3 w-3" />
              <span>{Object.keys(docs).length} document{Object.keys(docs).length > 1 ? "s" : ""} indexé{Object.keys(docs).length > 1 ? "s" : ""}</span>
            </div>
            {showDocs ? (
              <ChevronUp className="h-3 w-3" />
            ) : (
              <ChevronDown className="h-3 w-3" />
            )}
          </button>
        )}
        {showDocs && (
          <div className="border-b border-sidebar-border px-4 py-3">
            <DocumentsPanel docs={docs} />
          </div>
        )}

        {/* ── Messages ────────────────────────────────────────────────────── */}
        <ScrollArea className="flex-1 px-4" ref={scrollRef}>
          <div className="space-y-4 py-4">
            {messages.map((msg) => (
              <MessageBubble key={msg.id} msg={msg} />
            ))}

            {/* Loading indicator */}
            {isLoading && (
              <div className="flex items-start gap-2">
                <div className="flex h-7 w-7 items-center justify-center rounded-full bg-sidebar-primary/20">
                  <Bot className="h-3.5 w-3.5 text-sidebar-primary" />
                </div>
                <div className="rounded-2xl rounded-tl-sm bg-sidebar-accent/70 px-3.5 py-2.5">
                  <div className="flex items-center gap-1.5">
                    <span className="h-1.5 w-1.5 animate-bounce rounded-full bg-sidebar-primary [animation-delay:-0.3s]" />
                    <span className="h-1.5 w-1.5 animate-bounce rounded-full bg-sidebar-primary [animation-delay:-0.15s]" />
                    <span className="h-1.5 w-1.5 animate-bounce rounded-full bg-sidebar-primary" />
                  </div>
                </div>
              </div>
            )}
            <div ref={bottomRef} />
          </div>
        </ScrollArea>

        {/* ── Suggestions ─────────────────────────────────────────────────── */}
        {messages.length <= 1 && !isLoading && (
          <div className="border-t border-sidebar-border px-4 py-3">
            <p className="mb-2 text-[10px] font-semibold uppercase tracking-wide text-sidebar-foreground/40">
              Suggestions
            </p>
            <div className="flex flex-col gap-1.5">
              {SUGGESTED_QUESTIONS.slice(0, 3).map((q) => (
                <button
                  key={q}
                  onClick={() => send(q)}
                  className="flex items-center gap-2 rounded-lg border border-sidebar-border bg-sidebar-accent/20 px-3 py-2 text-left text-xs text-sidebar-foreground/70 transition-all hover:border-sidebar-primary/40 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground"
                >
                  <MessageSquare className="h-3 w-3 shrink-0 text-sidebar-primary/60" />
                  <span className="line-clamp-1">{q}</span>
                </button>
              ))}
            </div>
          </div>
        )}

        {/* ── Input ───────────────────────────────────────────────────────── */}
        <div className="border-t border-sidebar-border p-3">
          <div className="flex items-end gap-2 rounded-xl border border-sidebar-border bg-sidebar-accent/30 px-3 py-2 transition-colors focus-within:border-sidebar-primary/50 focus-within:bg-sidebar-accent/50">
            <textarea
              ref={inputRef}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Posez votre question… (↵ pour envoyer)"
              rows={1}
              className="flex-1 resize-none bg-transparent text-sm text-sidebar-foreground placeholder:text-sidebar-foreground/30 focus:outline-none"
              style={{ maxHeight: "120px" }}
              onInput={(e) => {
                const t = e.currentTarget;
                t.style.height = "auto";
                t.style.height = Math.min(t.scrollHeight, 120) + "px";
              }}
            />
            <Button
              size="icon"
              className={cn(
                "h-7 w-7 shrink-0 rounded-lg transition-all",
                input.trim() && !isLoading
                  ? "bg-sidebar-primary text-sidebar-primary-foreground hover:bg-sidebar-primary/80"
                  : "bg-sidebar-accent/50 text-sidebar-foreground/30 cursor-not-allowed"
              )}
              onClick={() => send(input)}
              disabled={!input.trim() || isLoading}
            >
              {isLoading ? (
                <Loader2 className="h-3.5 w-3.5 animate-spin" />
              ) : (
                <Send className="h-3.5 w-3.5" />
              )}
            </Button>
          </div>
          <p className="mt-1.5 text-center text-[10px] text-sidebar-foreground/25">
            Shift+↵ pour une nouvelle ligne
          </p>
        </div>
      </aside>
    </>
  );
}
