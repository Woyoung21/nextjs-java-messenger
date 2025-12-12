import React from "react";

interface FriendRequestDto {
  uniqueId: string;
  fromUser: string;
  toUser: string;
  status: string;
}

interface Props {
  currentUser?: string;
}

export default function FriendsList({ currentUser }: Props) {
  const [friends, setFriends] = React.useState<FriendRequestDto[]>([]);
  const [pendingRequests, setPendingRequests] = React.useState<FriendRequestDto[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [newFriend, setNewFriend] = React.useState("");
  const [message, setMessage] = React.useState<{ text: string; type: "success" | "error" } | null>(null);

  const fetchFriends = React.useCallback(() => {
    if (!currentUser) return;

    fetch("/api/getFriends")
      .then((res) => res.json())
      .then((data) => {
        setFriends(data?.data || []);
      })
      .catch((err) => console.error("Failed to fetch friends:", err));
  }, [currentUser]);

  const fetchPendingRequests = React.useCallback(() => {
    if (!currentUser) return;

    fetch("/api/getFriendRequests")
      .then((res) => res.json())
      .then((data) => {
        setPendingRequests(data?.data || []);
      })
      .catch((err) => console.error("Failed to fetch requests:", err));
  }, [currentUser]);

  React.useEffect(() => {
    if (!currentUser) {
      setFriends([]);
      setPendingRequests([]);
      return;
    }
    setLoading(true);
    Promise.all([fetchFriends(), fetchPendingRequests()]).finally(() => setLoading(false));
  }, [currentUser, fetchFriends, fetchPendingRequests]);

  const showMessage = (text: string, type: "success" | "error") => {
    setMessage({ text, type });
    setTimeout(() => setMessage(null), 3000);
  };

  const handleSendRequest = async (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = newFriend.trim();
    if (!trimmed) return;

    try {
      const res = await fetch("/api/sendFriendRequest", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ toUser: trimmed }),
      });
      const data = await res.json();

      if (data.status) {
        showMessage("Friend request sent!", "success");
        setNewFriend("");
      } else {
        showMessage(data.message || "Failed to send request", "error");
      }
    } catch (err) {
      showMessage("Failed to send request", "error");
    }
  };

  const handleRespond = async (requestId: string, accept: boolean) => {
    try {
      const res = await fetch("/api/respondFriendRequest", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ requestId, accept }),
      });
      const data = await res.json();

      if (data.status) {
        showMessage(accept ? "Friend added!" : "Request declined", "success");
        fetchPendingRequests();
        if (accept) fetchFriends();
      } else {
        showMessage(data.message || "Failed to respond", "error");
      }
    } catch (err) {
      showMessage("Failed to respond", "error");
    }
  };

  const getInitials = (name?: string) => {
    if (!name) return "?";
    const parts = name.split(/\s+/);
    return parts.length === 1 ? parts[0].slice(0, 2).toUpperCase() : (parts[0][0] + parts[1][0]).toUpperCase();
  };

  const getFriendUsername = (request: FriendRequestDto) => {
    return request.fromUser === currentUser ? request.toUser : request.fromUser;
  };

  return (
    <div style={{ maxWidth: 720 }}>
      <h2 style={{ margin: "8px 0", fontSize: 18 }}>Friends</h2>

      {/* Message toast */}
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

      {/* Add friend form */}
      <form onSubmit={handleSendRequest} style={{ marginBottom: 12, display: "flex", gap: 8 }}>
        <input
          aria-label="Add friend by username"
          placeholder="Enter username to add friend"
          value={newFriend}
          onChange={(e) => setNewFriend(e.target.value)}
          style={{
            flex: 1,
            padding: "10px 12px",
            borderRadius: 10,
            border: "1px solid #e6e6e6",
            boxShadow: "inset 0 1px 4px rgba(16,24,40,0.04)",
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
            background: "linear-gradient(180deg, #10b981, #059669)",
            color: "white",
            fontWeight: 600,
            boxShadow: "0 6px 18px rgba(5,150,105,0.15)",
            cursor: "pointer",
          }}
        >
          Add Friend
        </button>
      </form>

      {/* Pending requests */}
      {pendingRequests.length > 0 && (
        <div
          style={{
            background: "#fffbeb",
            borderRadius: 12,
            padding: 12,
            marginBottom: 12,
            boxShadow: "0 6px 24px rgba(15, 23, 42, 0.06)",
          }}
        >
          <h3 style={{ margin: "0 0 10px 0", fontSize: 14, color: "#92400e" }}>
            Pending Requests ({pendingRequests.length})
          </h3>
          <ul style={{ listStyle: "none", padding: 0, margin: 0, display: "grid", gap: 8 }}>
            {pendingRequests.map((req) => (
              <li
                key={req.uniqueId}
                style={{
                  padding: 12,
                  borderRadius: 10,
                  background: "white",
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
                  <div
                    style={{
                      width: 36,
                      height: 36,
                      borderRadius: 8,
                      background: "#fef3c7",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontWeight: 700,
                      color: "#92400e",
                      fontSize: 12,
                    }}
                  >
                    {getInitials(req.fromUser)}
                  </div>
                  <span style={{ fontWeight: 600, fontSize: 14 }}>{req.fromUser}</span>
                </div>
                <div style={{ display: "flex", gap: 6 }}>
                  <button
                    onClick={() => handleRespond(req.uniqueId, true)}
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
                    Accept
                  </button>
                  <button
                    onClick={() => handleRespond(req.uniqueId, false)}
                    style={{
                      padding: "6px 12px",
                      borderRadius: 6,
                      border: "1px solid #e5e7eb",
                      background: "white",
                      color: "#6b7280",
                      fontWeight: 600,
                      fontSize: 12,
                      cursor: "pointer",
                    }}
                  >
                    Decline
                  </button>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Friends list */}
      <div
        style={{
          background: "#ffffff",
          borderRadius: 12,
          padding: 12,
          boxShadow: "0 6px 24px rgba(15, 23, 42, 0.06)",
        }}
      >
        {loading ? (
          <div style={{ padding: 20, color: "#666" }}>Loading friends…</div>
        ) : friends.length === 0 ? (
          <div style={{ color: "#666", padding: 12 }}>No friends yet. Add someone above!</div>
        ) : (
          <ul style={{ listStyle: "none", padding: 0, margin: 0, display: "grid", gap: 8 }}>
            {friends.map((friend) => {
              const friendName = getFriendUsername(friend);
              return (
                <li
                  key={friend.uniqueId}
                  style={{
                    padding: 12,
                    borderRadius: 10,
                    border: "1px solid #f0f2f5",
                    display: "flex",
                    alignItems: "center",
                    gap: 12,
                  }}
                >
                  <div
                    style={{
                      width: 40,
                      height: 40,
                      borderRadius: 10,
                      background: "#ecfdf5",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontWeight: 700,
                      color: "#059669",
                      fontSize: 14,
                    }}
                  >
                    {getInitials(friendName)}
                  </div>
                  <span style={{ fontWeight: 600, fontSize: 15 }}>{friendName}</span>
                </li>
              );
            })}
          </ul>
        )}
      </div>
    </div>
  );
}

