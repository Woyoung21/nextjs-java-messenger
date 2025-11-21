import React from "react";

interface ConversationDto {
  uniqueId: string;
  fromId: string;
  toId: string;
  conversationId: string;
  messageCount?: string; // may come as a string from the API
  lastMessage?: string;
  unreadCount?: number;
}

interface Props {
  currentUser?: string;
  onOpenChat: (username: string) => void;
}

export default function ChatList({ currentUser, onOpenChat }: Props) {
  const [threads, setThreads] = React.useState<ConversationDto[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);
  const [newUser, setNewUser] = React.useState("");

  React.useEffect(() => {
    if (!currentUser) {
      setThreads([]);
      return;
    }

    setLoading(true);
    setError(null);

    fetch(`/api/getConversations`)
      .then((res) => res.json())
      .then((data) => {
        const raw = data?.data || [];
        const mapped: ConversationDto[] = raw.map((c: any) => ({
          uniqueId: c.uniqueId ?? c.conversationId ?? c.id,
          fromId: c.fromId,
          toId: c.toId,
          conversationId: c.conversationId,
          messageCount: c.messageCount,
          lastMessage: c.lastMessage,
          unreadCount: c.unreadCount ?? (c.messageCount ? parseInt(c.messageCount, 10) : undefined),
        }));
        setThreads(mapped);
      })
      .catch((err) => {
        console.error(err);
        setError("Failed to load threads.");
      })
      .finally(() => setLoading(false));
  }, [currentUser]);

  const handleOpen = (username: string) => {
    if (!username) return;
    onOpenChat(username);
  };

  const handleSubmitNew = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = newUser.trim();
    if (!trimmed) return;
    onOpenChat(trimmed);
    setNewUser("");
  };

  const getInitials = (name?: string) => {
    if (!name) return "?";
    const parts = name.split(/\s+/);
    const initials = parts.length === 1 ? parts[0].slice(0, 2) : (parts[0][0] + parts[1][0]);
    return initials.toUpperCase();
  };

  return (
    <div style={{ marginTop: 20, maxWidth: 720 }}>
      <h2 style={{ margin: "8px 0", fontSize: 18 }}>Chats</h2>

      <form onSubmit={handleSubmitNew} style={{ marginBottom: 12, display: "flex", gap: 8 }}>
        <input
          aria-label="Start chat with username"
          placeholder="Type a username to start a new chat"
          value={newUser}
          onChange={(e) => setNewUser(e.target.value)}
          style={{
            flex: 1,
            padding: '10px 12px',
            borderRadius: 10,
            border: '1px solid #e6e6e6',
            boxShadow: 'inset 0 1px 4px rgba(16,24,40,0.04)',
            outline: 'none',
            fontSize: 14,
          }}
        />
        <button
          type="submit"
          style={{
            padding: '10px 14px',
            borderRadius: 10,
            border: 'none',
            background: 'linear-gradient(180deg, #3b82f6, #2563eb)',
            color: 'white',
            fontWeight: 600,
            boxShadow: '0 6px 18px rgba(37,99,235,0.15)',
            cursor: 'pointer',
          }}
        >
          Open
        </button>
      </form>

      <div
        style={{
          background: '#ffffff',
          borderRadius: 12,
          padding: 12,
          boxShadow: '0 6px 24px rgba(15, 23, 42, 0.06)',
        }}
      >
        {loading ? (
          <div style={{ padding: 20, color: '#666' }}>Loading threads…</div>
        ) : error ? (
          <div style={{ color: "red", padding: 12 }}>{error}</div>
        ) : threads.length === 0 ? (
          <div style={{ color: "#666", padding: 12 }}>No recent chats.</div>
        ) : (
          <ul style={{ listStyle: "none", padding: 0, margin: 0, display: "grid", gap: 10 }}>
            {threads.map((t) => {
              const withUser = currentUser && t.fromId === currentUser ? t.toId : t.fromId;
              const unread = t.unreadCount;

              return (
                <li
                  key={t.uniqueId}
                  onClick={() => handleOpen(withUser)}
                  style={{
                    padding: 12,
                    borderRadius: 10,
                    border: "1px solid #f0f2f5",
                    cursor: "pointer",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    transition: 'transform 120ms ease, box-shadow 120ms ease',
                    boxShadow: '0 2px 6px rgba(16,24,40,0.04)',
                  }}
                  onMouseEnter={(e) => {
                    (e.currentTarget as HTMLElement).style.transform = 'translateY(-4px)';
                    (e.currentTarget as HTMLElement).style.boxShadow = '0 10px 30px rgba(16,24,40,0.08)';
                  }}
                  onMouseLeave={(e) => {
                    (e.currentTarget as HTMLElement).style.transform = 'translateY(0)';
                    (e.currentTarget as HTMLElement).style.boxShadow = '0 2px 6px rgba(16,24,40,0.04)';
                  }}
                >
                  <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                    <div
                      style={{
                        width: 44,
                        height: 44,
                        borderRadius: 10,
                        background: '#eef2ff',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 700,
                        color: '#3730a3',
                        fontSize: 14,
                        boxShadow: 'inset 0 -6px 16px rgba(99,102,241,0.06)',
                      }}
                    >
                      {getInitials(withUser)}
                    </div>

                    <div>
                      <div style={{ fontWeight: 700, fontSize: 15 }}>{withUser}</div>
                      {t.lastMessage && (
                        <div style={{ color: "#666", fontSize: 13, marginTop: 4, maxWidth: 420, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {t.lastMessage}
                        </div>
                      )}
                    </div>
                  </div>

                  {typeof unread === 'number' && unread > 0 ? (
                    <div
                      style={{
                        background: '#ef4444',
                        color: 'white',
                        padding: '6px 10px',
                        borderRadius: 9999,
                        fontSize: 12,
                        minWidth: 30,
                        textAlign: 'center',
                        fontWeight: 700,
                        boxShadow: '0 6px 12px rgba(239,68,68,0.12)',
                      }}
                    >
                      {unread}
                    </div>
                  ) : null}
                </li>
              );
            })}
          </ul>
        )}
      </div>
    </div>
  );
}
