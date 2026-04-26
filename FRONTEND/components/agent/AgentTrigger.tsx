"use client";

import { useState } from "react";
import { Bot, Sparkles } from "lucide-react";
import { cn } from "@/lib/utils";
import { AgentSidebar } from "./AgentSidebar";

export function AgentTrigger() {
  const [open, setOpen] = useState(false);

  return (
    <>
      {/* Floating trigger button */}
      <button
        onClick={() => setOpen(true)}
        className={cn(
          "fixed bottom-6 right-6 z-40",
          "flex h-14 w-14 items-center justify-center",
          "rounded-2xl shadow-lg",
          "bg-sidebar text-sidebar-foreground",
          "border border-sidebar-border",
          "transition-all duration-200",
          "hover:scale-105 hover:shadow-xl hover:border-sidebar-primary/50",
          "group",
          open && "opacity-0 pointer-events-none"
        )}
        title="Ouvrir l'assistant RAG"
        aria-label="Ouvrir l'assistant RAG"
      >
        {/* Pulse ring */}
        <span className="absolute inset-0 rounded-2xl animate-ping opacity-20 bg-sidebar-primary" />
        <Bot className="h-6 w-6 text-sidebar-primary transition-transform group-hover:scale-110" />
        <Sparkles className="absolute -right-1 -top-1 h-3.5 w-3.5 text-sidebar-primary" />
      </button>

      <AgentSidebar open={open} onClose={() => setOpen(false)} />
    </>
  );
}
