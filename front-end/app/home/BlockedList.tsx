import React from "react";

interface BlockDto {
  blocker?: string;
  blocked?: string;
}

interface Props {
  currentUser?: string;
}

export default function BlockedList({ currentUser }: Props) {
  const [blocked, setBlocked] = React.useState<BlockDto[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [target, setTarget] = React.useState("");
  const [message, setMessage] = React.useState<{ text: string; type: "success" | "error" } | null>(null);

  const fetchBlocked = React.useCallback(() => {
    if (!currentUser) return;
    setLoading(true);
    fetch("/api/getBlockedUsers")
      .then((res) => res.json())
      .then((data) => setBlocked(data?.data || []))
      .catch((err) => console.error("Failed to fetch blocked users", err))
      .finally(() => setLoading(false));
  }, [currentUser]);

  React.useEffect(() => {
    if (!currentUser) {
      setBlocked([]);
      return;
    }
    fetchBlocked();
  }, [currentUser, fetchBlocked]);

  const showMessage = (text: string, type: "success" | "error") => {
    setMessage({ text, type });
    setTimeout(() => setMessage(null), 3000);
  };

  const handleBlock = async (e: React.FormEvent) => {
    e.preventDefault();
    const name = target.trim();
    if (!name) return;
    try {
      const res = await fetch("/api/blockUser", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ blocked: name }),
      });
      const data = await res.json();
      if (data.status) {
        showMessage("User blocked", "success");
        setTarget("");
        fetchBlocked();
      } else {
        showMessage(data.message || "Failed to block", "error");
      }
    } catch (err) {
      showMessage("Failed to block", "error");
    }
  };

  const handleUnblock = async (name?: string) => {
    if (!name) return;
    try {
      const res = await fetch("/api/unblockUser", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ blocked: name }),
      });
      const data = await res.json();
      if (data.status) {
        showMessage("User unblocked", "success");
        fetchBlocked();
      } else {
        showMessage(data.message || "Failed to unblock", "error");
      }
    } catch (err) {
      showMessage("Failed to unblock", "error");
    }
  };

  return (
    <div style={{ maxWidth: 720 }}>
      <h2 style={{ margin: "8px 0", fontSize: 18 }}>Blocked Users</h2>

      {message && (
        <div
          style={{
            padding: "10px 14px",
            marginBottom: 12,
            borderRadius: 8,
            background: message.type === "success" ? "#dcfce7" : "#fee2e2",
            color: message.type === "success" ? "#166534" : "#991b1b",
            fontSize: 14,
          }}
        >
          {message.text}
        </div>
      )}

      <form onSubmit={handleBlock} style={{ marginBottom: 12, display: "flex", gap: 8 }}>
        <input
          aria-label="Block user by username"
          placeholder="Enter username to block"
          value={target}
          onChange={(e) => setTarget(e.target.value)}
          style={{
            flex: 1,
            padding: "10px 12px",
            borderRadius: 10,
            border: "1px solid #e6e6e6",
            outline: "none",
            fontSize: 14,
          }}
        />
        <button
          type="submit"
          style={{
            padding: "10px 14px",
            borderRadius: 10,
            border: "none",
            background: "#ef4444",
            color: "white",
            fontWeight: 600,
            cursor: "pointer",
          }}
        >
          Block
        </button>
      </form>

      <div style={{ background: "#ffffff", borderRadius: 12, padding: 12, boxShadow: "0 6px 24px rgba(15, 23, 42, 0.06)" }}>
        {loading ? (
          <div style={{ padding: 20, color: "#666" }}>Loading blocked users…</div>
        ) : blocked.length === 0 ? (
          <div style={{ color: "#666", padding: 12 }}>No blocked users.</div>
        ) : (
          <ul style={{ listStyle: "none", padding: 0, margin: 0, display: "grid", gap: 8 }}>
            {blocked.map((b, i) => (
              <li
                key={b.blocked || i}
                style={{
                  padding: 12,
                  borderRadius: 10,
                  border: "1px solid #f0f2f5",
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
                  <div
                    style={{
                      width: 40,
                      height: 40,
                      borderRadius: 10,
                      background: "#fff1f2",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontWeight: 700,
                      color: "#b91c1c",
                      fontSize: 14,
                    }}
                  >
                    {b.blocked ? b.blocked.slice(0, 2).toUpperCase() : "?"}
                  </div>
                  <span style={{ fontWeight: 600, fontSize: 15 }}>{b.blocked}</span>
                </div>
                <div>
                  <button
                    onClick={() => handleUnblock(b.blocked)}
                    style={{
                      padding: "6px 12px",
                      borderRadius: 6,
                      border: "none",
                      background: "#10b981",
                      color: "white",
                      fontWeight: 600,
                      fontSize: 12,
                      cursor: "pointer",
                    }}
                  >
                    Unblock
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
