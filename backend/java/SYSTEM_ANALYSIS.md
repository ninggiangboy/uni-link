# UniLink — System Analysis

## Overview

**UniLink** is a full-featured social networking platform built on Clean Architecture principles with a polyglot persistence model: Spring Data JDBC + PostgreSQL for relational aggregates, and Spring Data Neo4j + Neo4j for relationship-heavy graph traversals. The system provides a complete set of modern social network capabilities: threaded posts, real-time messaging with end-to-end encryption, community groups, multi-channel notifications, hashtag analytics, and fine-grained access control.

### High-Level Architecture

```
backend/java/core/
├── domain/     # Pure domain layer — models, repository interfaces, enums, domain errors
└── database/   # Infrastructure layer — JDBC + Neo4j repository implementations
```

---

## Modules

---

### 1. Identity (`iam_*`)

> Account management, authentication, and session lifecycle.

**Tables:**

| Table | Purpose |
|-------|---------|
| `iam_accounts` | Core account record — email, phone, password hash, status, verification flags, 2FA |
| `iam_account_credentials` | OAuth / social login provider credentials |
| `iam_account_devices` | Registered devices (fingerprint, push token, platform) |
| `iam_account_sessions` | Active sessions per device (access + refresh tokens) |
| `iam_account_otps` | One-time passwords with TTL, channel, and attempt tracking |
| `iam_account_login_histories` | Immutable login audit trail (IP, device, geo, timestamp) |

**Domain Models:**

| Model | Role |
|-------|------|
| `Account` | Aggregate root. Lifecycle: `PENDING → ACTIVE`, supports `SUSPENDED`, `BANNED`, `DEACTIVATED` |
| `AccountCredential` | OAuth provider credentials linked to an account |
| `AccountDevice` | Device info for session isolation and push notifications |
| `AccountSession` | Token pair (access + refresh) scoped per device |
| `AccountOtp` | Time-limited OTP for email/phone verification and 2FA |
| `AccountLoginHistory` | Append-only record of each successful/failed login |

**Key Enums:**
- `AccountStatus` — `PENDING`, `ACTIVE`, `SUSPENDED`, `BANNED`, `DEACTIVATED`
- `AuthProvider` — OAuth provider identifiers
- `DeviceType` — Device categories
- `OtpChannel` — `EMAIL`, `SMS`
- `OtpPurpose` — `VERIFY_EMAIL`, `PASSWORD_RESET`, `TWO_FACTOR`

---

### 2. Profile (`prf_*`)

> Public user persona, social graph, and personal settings.

**Tables:**

| Table | Purpose |
|-------|---------|
| `prf_profiles` | Public persona — username, display name, bio, visibility, verification status, E2E public key |
| `prf_profile_stats` | Denormalized counters: followers, following, threads, likes |
| `prf_profile_settings` | Notification and interaction preferences |
| `prf_profile_activities` | Last active timestamps |
| `prf_profile_links` | External links (personal site, social accounts) |
| `prf_profile_medias` | Avatar and banner media gallery |
| `prf_profile_metadata` | Custom key-value metadata |
| `prf_profile_usernames` | Full history of username changes |
| *(Neo4j graph)* `(:Profile)-[:FOLLOWS]->(:Profile)` | Directed follow relationships |
| *(Neo4j graph)* `(:Profile)-[:BLOCKS]->(:Profile)` | Block relationships (also removes existing follow edges in both directions) |
| *(Neo4j graph)* `(:Profile)-[:MUTES]->(:Profile)` | Mute relationships |
| *(Neo4j graph)* `(:Profile)-[:FOLLOWS_TAG]->(:Hashtag)` | Hashtag subscription relationships |

**Domain Models:**

| Model | Role |
|-------|------|
| `Profile` | Aggregate root. Separated from `Account` to decouple identity from public persona |
| `ProfileStats` | Cached counters for followers, following, threads, likes |
| `ProfileSetting` | Per-user preference flags for notification and interaction behavior |
| `ProfileActivity` | Last seen / active timestamps |
| `ProfileMedia` | Avatar and banner images (type-discriminated) |
| `ProfileLink` | Personal / social external links |
| `ProfileMetadata` | Flexible key-value extension point |
| `ProfileUsername` | Append-only history of username changes |
| `ProfileRelationshipRepository` | Graph-oriented relationship contract (`follow`, `block`, `mute`, `follow hashtag`, traversal queries) backed by Neo4j (`ProfileRelationshipNeo4jRepository`) |

**Profile Relationship Storage Split:**
- Profile master data (`prf_profiles`, settings, metadata, media, username history) remains in PostgreSQL.
- Relationship edges (follow, block, mute, follow hashtag) are modeled as Neo4j relationships for traversal performance.
- A shared query executor (`Neo4jQueryExecutor`) runs parameterized Cypher and maps scalar/list results.

**Key Enums:**
- `ProfileVisibility` — `PUBLIC`, `PRIVATE`, `HIDDEN`
- `ProfileMediaType` — `AVATAR`, `BANNER`

---

### 3. Thread (`thr_*`)

> Social post authoring, engagement, and moderation — the primary content unit of the platform.

**Tables:**

| Table | Purpose |
|-------|---------|
| `thr_threads` | Core post record with reply/quote/repost chain references, scheduling, visibility, and engagement counts |
| `thr_thread_medias` | Ordered media attachments (image, video, gif) |
| `thr_thread_mentions` | @mention links to profiles within thread content |
| `thr_thread_hashtags` | Hashtag occurrences with position in raw content |
| `thr_thread_polls` | Poll configuration (title, expiry, allow multiple votes) |
| `thr_thread_poll_options` | Poll options with cached vote counts |
| `thr_thread_poll_votes` | Individual vote records per user per option |
| `thr_thread_link_previews` | Unfurled URL metadata (title, description, image, URL) |
| `thr_thread_likes` | Like records (profile → thread) |
| `thr_thread_reposts` | Repost records |
| `thr_thread_bookmarks` | Bookmark records |
| `thr_thread_moderations` | Content reports and moderation decisions |

**Domain Models:**

| Model | Role |
|-------|------|
| `Thread` | Aggregate root. Supports original posts, replies, reposts, and quotes via `parentThreadId`, `rootThreadId`, `quoteThreadId` |
| `ThreadMention` | A profile mentioned in a thread |
| `ThreadHashtag` | A hashtag occurrence with character offset for rendering |
| `ThreadMedia` | An ordered media attachment |
| `ThreadPoll` | Poll configuration owned by a thread |
| `ThreadPollOption` | Individual poll choice with cached vote count |
| `ThreadPollVote` | A user's vote for a specific poll option |
| `ThreadLinkPreview` | Unfurled metadata for the first URL in the thread |
| `ThreadLike` | A like interaction record |
| `ThreadRepost` | A repost interaction record |
| `ThreadBookmark` | A bookmark interaction record |
| `ThreadModeration` | A content report with status and resolution |

**Key Enums:**
- `ThreadStatus` — `DRAFT`, `PUBLISHED`, `ARCHIVED`, `DELETED`
- `ThreadVisibility` — `PUBLIC`, `FOLLOWERS_ONLY`, `PRIVATE`
- `ReplyRestriction` — `EVERYONE`, `FOLLOWERS_ONLY`, `MENTIONED_ONLY`, `NOBODY`
- `MediaType` — Image, Video, Gif

**Engagement Counters** (denormalized on `thr_threads`):
- `likeCount`, `replyCount`, `repostCount`, `quoteCount`, `bookmarkCount`, `viewCount`

---

### 4. Chat (`chat_*`)

> Direct and group messaging with end-to-end encryption.

**Tables:**

| Table | Purpose |
|-------|---------|
| `chat_conversations` | Conversation record (DIRECT or GROUP) with last-message pointer |
| `chat_conversation_participants` | Membership with role, joined/left timestamps |
| `chat_conversation_blocks` | Conversation-level blocks (prevents message delivery) |
| `chat_conversation_read_states` | Per-user read watermark (last read message ID) |
| `chat_messages` | Message content — encrypted text + AES-GCM IV, edit/delete support |
| `chat_message_medias` | File and media attachments |
| `chat_message_reactions` | Emoji reactions per user per message |
| `chat_message_reports` | Abuse reports for moderation |
| `chat_actor_inboxes` | Per-user inbox index with unread count per conversation |
| `chat_message_keys` | Per-recipient encrypted symmetric key for group E2E messages |

**Domain Models:**

| Model | Role |
|-------|------|
| `Conversation` | Aggregate root. Type: `DIRECT` (1:1) or `GROUP` (multi-participant) |
| `ConversationParticipant` | A member of a conversation with assigned role |
| `ConversationReadState` | Tracks the last message a user has read in a conversation |
| `ConversationBlock` | Prevents a user from receiving messages in a conversation |
| `Message` | Aggregate root. Stores encrypted content and AES-GCM IV |
| `MessageMedia` | An attachment associated with a message |
| `MessageReaction` | An emoji reaction by a user on a message |
| `MessageKey` | An encrypted copy of the message's symmetric key for one recipient |
| `ActorInbox` | Lightweight inbox record per user per conversation (unread count, last message) |
| `MessageReport` | A moderation report for a message |

**Key Enums:**
- `ConversationType` — `DIRECT`, `GROUP`
- `ConversationRole` — `MEMBER`, `ADMIN`
- `MessageType` — Text, Media, System

**E2E Encryption Model:**
```
Sender                     Message Record           Per-Recipient Key
──────                     ──────────────           ─────────────────
X25519 key pair            content (AES-GCM)        chat_message_keys
  │                        iv (Base64)                encrypted_key
  │                                                   recipient_profile_id
  └─ encrypts session key ──────────────────────────►
```

---

### 5. Notification (`ntf_*`)

> Multi-channel notification system with actor grouping and delivery tracking.

**Tables:**

| Table | Purpose |
|-------|---------|
| `ntf_notifications` | Notification record with grouping key and actor count |
| `ntf_notification_actors` | Individual actors that triggered this notification |
| `ntf_notification_objects` | Referenced entities associated with the notification |
| `ntf_notification_deliveries` | Delivery status per channel (in-app, email, push) |
| `ntf_notification_settings` | Per-user channel preferences per notification type |
| `ntf_notification_counters` | Unread notification count per user |

**Domain Models:**

| Model | Role |
|-------|------|
| `Notification` | Aggregate root. Stores recipient, type, entity reference, read state |
| `NotificationActor` | One of potentially many actors that triggered the notification (e.g., 5 users liked) |
| `NotificationObject` | The entity the notification is about (thread, profile, group, etc.) |
| `NotificationDelivery` | Tracks delivery status per channel per notification |
| `NotificationSetting` | User's opt-in/opt-out preference per notification type per channel |
| `NotificationCounter` | Cached unread count per user |

**Notification Types:**
- `LIKE` — Someone liked your thread
- `REPLY` — Someone replied to your thread
- `MENTION` — Someone mentioned you in a thread
- `FOLLOW` — Someone followed you
- `REPOST` — Someone reposted your thread
- `QUOTE` — Someone quoted your thread
- `THREAD_RECOMMENDATION` — Algorithmic recommendation
- `SYSTEM` — Platform-level messages

**Delivery Channels:**
- `IN_APP` — In-application notification center
- `EMAIL` — Email notification
- `PUSH_NOTIFICATION` — Mobile / desktop push

---

### 6. Hashtag (`ht_*`)

> Hashtag lifecycle, usage analytics, trending computation, and moderation.

**Tables:**

| Table | Purpose |
|-------|---------|
| `ht_hashtags` | Canonical hashtag record (normalized form, original form) |
| `ht_hashtag_stats` | Aggregated use count and thread count |
| `ht_hashtag_metadata` | Category, language, description, curated flag |
| `ht_hashtag_moderations` | Moderation status and reason |
| `ht_hashtag_relations` | Co-occurrence strength between hashtag pairs |
| `ht_hashtag_usage_hourly` | Time-series usage counts per hour for trending |

**Domain Models:**

| Model | Role |
|-------|------|
| `Hashtag` | Aggregate root. Stores normalized and display forms |
| `HashtagStats` | Cached aggregate usage and thread count |
| `HashtagMetadata` | Descriptive metadata: category, language, description |
| `HashtagModeration` | Moderation flag with status and reason |
| `HashtagRelation` | Co-occurrence relationship with strength score |
| `HashtagUsageHourly` | Hour-bucket usage count for trend computation |

**Key Enums:**
- `HashtagModerationStatus` — Active, Restricted, Banned

---

### 7. Group (`grp_*`)

> Community spaces with role hierarchy, bitmask permissions, content moderation, and invitation/approval flows.

**Tables:**

| Table | Purpose |
|-------|---------|
| `grp_groups` | Community record — name, slug, description, avatar, visibility, approval required |
| `grp_group_stats` | Denormalized member count and thread count |
| `grp_group_members` | Membership record with role and fine-grained permissions bitmask |
| `grp_group_rules` | Ordered list of community rules |
| `grp_group_invitations` | Invitation tokens with expiry and usage tracking |
| `grp_group_member_requests` | Join requests for private groups with approval status |
| `grp_group_threads` | Threads posted inside a group (with pinning support) |
| `grp_group_moderation_actions` | Audit log of bans, mutes, warnings, and removals |

**Domain Models:**

| Model | Role |
|-------|------|
| `Group` | Aggregate root. Visibility: `PUBLIC`, `PRIVATE`. Status: `ACTIVE`, `SUSPENDED`, `DELETED` |
| `GroupStats` | Cached member and thread counts |
| `GroupMember` | Membership with role and optional `permissions` bitmask override |
| `GroupRule` | A numbered community rule with title and body |
| `GroupInvitation` | A token-based invitation with max uses and expiry |
| `GroupMemberRequest` | A pending join request for a private group |
| `GroupThread` | A thread-to-group association, optionally pinned |
| `GroupModerationAction` | An immutable moderation event (ban, mute, warning, removal) |

**Role Hierarchy:**

```
OWNER > ADMIN > MODERATOR > MEMBER
```

**`GroupAdminPermission` Bitmask (12 flags):**

| Flag | Description |
|------|-------------|
| `APPROVE_POSTS` | Approve pending posts before publication |
| `REMOVE_POSTS` | Remove published posts |
| `PIN_POSTS` | Pin or unpin threads |
| `HANDLE_REPORTS` | Review and resolve content reports |
| `APPROVE_MEMBERS` | Approve join requests |
| `REMOVE_MEMBERS` | Remove members from the group |
| `BAN_MEMBERS` | Issue bans |
| `MUTE_MEMBERS` | Temporarily mute members |
| `MANAGE_INVITATIONS` | Create and revoke invitation links |
| `MANAGE_RULES` | Add, edit, or remove group rules |
| `MANAGE_GROUP_INFO` | Edit group name, description, avatar, banner |
| `MANAGE_ADMIN_PERMISSIONS` | Grant or revoke admin permissions for other moderators |

**Key Enums:**
- `GroupVisibility` — `PUBLIC`, `PRIVATE`
- `GroupStatus` — `ACTIVE`, `SUSPENDED`, `DELETED`
- `GroupMemberRole` — `OWNER`, `ADMIN`, `MODERATOR`, `MEMBER`
- `GroupMemberStatus` — `ACTIVE`, `MUTED`, `BANNED`
- `GroupInvitationStatus` — `PENDING`, `ACCEPTED`, `REVOKED`, `EXPIRED`
- `GroupMemberRequestStatus` — `PENDING`, `APPROVED`, `REJECTED`
- `GroupModerationActionType` — `BAN`, `MUTE`, `WARNING`, `REMOVE`

---

## Architecture

### Clean Architecture Layers

```
┌──────────────────────────────────────────────────────────┐
│                        domain/                            │
│                                                           │
│   ┌─────────────┐   ┌───────────────┐   ┌────────────┐  │
│   │   Models    │   │  Repository   │   │   Enums    │  │
│   │  (entities) │   │  (interfaces) │   │  & Errors  │  │
│   └─────────────┘   └───────────────┘   └────────────┘  │
│                                                           │
│                   Zero framework dependencies             │
└──────────────────────────────────────────────────────────┘
                        ↑ depends on ↑
┌──────────────────────────────────────────────────────────┐
│                       database/                           │
│                                                           │
│   ┌─────────────┐   ┌───────────────┐   ┌────────────┐  │
│   │    JDBC     │   │    Mapper     │   │ Repository │  │
│   │  Entities   │   │  toDomain()   │   │    Impl    │  │
│   │             │   │  toJdbc()     │   │            │  │
│   └─────────────┘   └───────────────┘   └────────────┘  │
│            +                                              │
│   ┌─────────────┐   ┌───────────────┐   ┌────────────┐  │
│   │   Cypher    │   │ QueryExecutor │   │ Repository │  │
│   │   Queries   │   │  (Neo4jClient)│   │   (Graph)  │  │
│   └─────────────┘   └───────────────┘   └────────────┘  │
│                                                           │
│         Spring Data JDBC + PostgreSQL + Neo4j            │
└──────────────────────────────────────────────────────────┘
```

### Design Patterns

| Pattern | Description |
|---------|-------------|
| **Aggregate Root** | Each domain has a single entry-point entity (`Account`, `Profile`, `Thread`, `Conversation`, `Notification`, `Hashtag`, `Group`) |
| **Repository Pattern** | Interfaces defined in `domain/`, implemented in `database/` — domain has no knowledge of JDBC |
| **Mapper Pattern** | `JdbcMapper<Domain, Entity>` provides explicit bidirectional `toDomain()` and `toJdbc()` conversions |
| **Graph Repository Pattern** | Traversal-heavy contracts (e.g. profile relationships) use Cypher + Neo4j repositories instead of relational join tables |
| **Polyglot Persistence** | Relational aggregates stay in PostgreSQL; social graph edges are stored in Neo4j for efficient relationship queries |
| **Soft Delete** | `SoftDeletableJdbcEntity` records `deleted_at` instead of physically removing rows |
| **Audit Trail** | Every entity carries `created_by`, `created_at`, `updated_by`, `updated_at` |
| **Bitmask Permissions** | `GroupAdminPermission` encodes 12 permission flags into a single `BIGINT` column |
| **Inbox Pattern** | `chat_actor_inboxes` maintains a per-user lightweight index for efficient inbox queries |
| **Denormalized Counters** | Stats tables (`ProfileStats`, `HashtagStats`, `GroupStats`) cache aggregate counts for read performance |

### E2E Encryption Architecture

```
┌──────────────┐     ┌──────────────────┐     ┌───────────────────────┐
│ prf_profiles │     │  chat_messages   │     │  chat_message_keys    │
│              │     │                  │     │                       │
│ public_key   │     │ content (AES enc)│     │ recipient_profile_id  │
│ (X25519 ECDH)│     │ iv (AES-GCM)     │     │ encrypted_key         │
└──────────────┘     └──────────────────┘     └───────────────────────┘
      │                                                  ▲
      │   Sender generates session key                   │
      └─ encrypts session key with each recipient's ─────┘
         X25519 public key (ECDH key exchange)
```

- Each profile holds an **X25519 ECDH public key**.
- Each message stores an **AES-GCM IV** alongside the encrypted ciphertext.
- For group messages, `chat_message_keys` holds one encrypted copy of the symmetric key per recipient.

---

## Database Schema Summary

```
PostgreSQL (relational):
iam_*    Identity          6 tables
prf_*    Profile           8 tables
thr_*    Thread           12 tables
chat_*   Chat             10 tables
ntf_*    Notification      6 tables
ht_*     Hashtag           6 tables
grp_*    Group             8 tables
                          ─────────
Subtotal                 56 tables

Neo4j (graph):
Profile-Profile edges    FOLLOWS, BLOCKS, MUTES
Profile-Hashtag edges    FOLLOWS_TAG
```

Migrations are managed by **Liquibase** (`db.changelog-master.yaml`) with full rollback support.

---

## Functional Requirements

### FR-01 — Identity & Authentication

#### FR-01.1 Account Registration
- [ ] **FR-01.1.1** A new user must be able to register an account using an email address and password.
- [ ] **FR-01.1.2** A new user must be able to register using a phone number and password.
- [ ] **FR-01.1.3** A new user must be able to register via OAuth (social login providers: Google, GitHub, Facebook, etc.).
- [ ] **FR-01.1.4** On registration, a new account is created in `PENDING` status until the primary contact (email or phone) is verified.
- [ ] **FR-01.1.5** The system must store a hashed representation of the password; plaintext must never be persisted.

#### FR-01.2 Email & Phone Verification
- [ ] **FR-01.2.1** The system must send a one-time password (OTP) to the user's email or phone number for verification.
- [ ] **FR-01.2.2** An OTP must have a configurable time-to-live (TTL) after which it expires.
- [ ] **FR-01.2.3** The system must limit the number of OTP verification attempts per token to prevent brute-force attacks.
- [ ] **FR-01.2.4** Upon successful verification, the account status transitions to `ACTIVE` and the corresponding verification flag (`emailVerified` / `phoneVerified`) is set.

#### FR-01.3 Authentication
- [ ] **FR-01.3.1** A user must be able to sign in using email/password or phone/password.
- [ ] **FR-01.3.2** A user must be able to sign in using a registered OAuth provider credential.
- [ ] **FR-01.3.3** On successful authentication, the system must issue an access token and a refresh token scoped to the specific device.
- [ ] **FR-01.3.4** The system must support two-factor authentication (2FA) via OTP before issuing tokens.
- [ ] **FR-01.3.5** Only accounts in `ACTIVE` status are permitted to authenticate; `PENDING`, `SUSPENDED`, `BANNED`, and `DEACTIVATED` accounts must be rejected with appropriate error codes.

#### FR-01.4 Session Management
- [ ] **FR-01.4.1** Each session must be associated with a specific device record (fingerprint, platform, push token).
- [ ] **FR-01.4.2** A user must be able to view all active sessions across their devices.
- [ ] **FR-01.4.3** A user must be able to terminate any individual session.
- [ ] **FR-01.4.4** A user must be able to terminate all sessions simultaneously (global sign-out).
- [ ] **FR-01.4.5** Access tokens must be refreshed using the refresh token without requiring re-authentication.
- [ ] **FR-01.4.6** Refresh tokens must be rotated on each use; an already-used refresh token must be invalidated.

#### FR-01.5 Login History
- [ ] **FR-01.5.1** Every authentication attempt (successful or failed) must be recorded with IP address, device info, geographic location, and timestamp.
- [ ] **FR-01.5.2** The system must retain login history for audit and security review purposes.

#### FR-01.6 Account Status Management
- [ ] **FR-01.6.1** An administrator must be able to suspend or ban an account.
- [ ] **FR-01.6.2** A user must be able to deactivate their own account.
- [ ] **FR-01.6.3** A suspended or banned account must be prevented from authenticating or performing any actions.

---

### FR-02 — User Profile

#### FR-02.1 Profile Information
- [ ] **FR-02.1.1** Each account has exactly one associated public profile.
- [ ] **FR-02.1.2** A user must be able to set and update their display name, bio, website, and location.
- [ ] **FR-02.1.3** A user must be able to upload an avatar image and a banner image.
- [ ] **FR-02.1.4** A user must be able to add, edit, and remove external links (up to a configured maximum).
- [ ] **FR-02.1.5** A user may attach arbitrary key-value metadata to their profile.

#### FR-02.2 Username
- [ ] **FR-02.2.1** A user must be able to choose a unique username at registration.
- [ ] **FR-02.2.2** A user must be able to change their username; the previous username must be recorded in username history.
- [ ] **FR-02.2.3** Released usernames may be reclaimed by other users after a configurable grace period.

#### FR-02.3 Profile Visibility
- [ ] **FR-02.3.1** A user must be able to set their profile visibility to `PUBLIC`, `PRIVATE`, or `HIDDEN`.
- [ ] **FR-02.3.2** A `PUBLIC` profile is visible to all users including unauthenticated visitors.
- [ ] **FR-02.3.3** A `PRIVATE` profile's threads and follower list are only visible to approved followers.
- [ ] **FR-02.3.4** A `HIDDEN` profile does not appear in search results or recommendations.

#### FR-02.4 Follow / Unfollow
- [ ] **FR-02.4.1** A user must be able to follow another user's public profile.
- [ ] **FR-02.4.2** Following a private profile creates a pending follow request until the owner approves it.
- [ ] **FR-02.4.3** A user must be able to unfollow any profile they currently follow.
- [ ] **FR-02.4.4** A profile owner must be able to remove a follower.
- [ ] **FR-02.4.5** Follower and following counts must be maintained in `prf_profile_stats`.

#### FR-02.5 Block
- [ ] **FR-02.5.1** A user must be able to block another user.
- [ ] **FR-02.5.2** A blocked user must not be able to view, interact with, or message the blocking user.
- [ ] **FR-02.5.3** A user must be able to unblock a previously blocked user.
- [ ] **FR-02.5.4** If a block relationship exists, any existing follow relationships between the two parties must be removed.

#### FR-02.6 Mute
- [ ] **FR-02.6.1** A user must be able to mute another user to suppress their content from feeds without notifying them.
- [ ] **FR-02.6.2** A user must be able to unmute a previously muted user.
- [ ] **FR-02.6.3** Muting does not affect the muted user's ability to interact with the muting user.

#### FR-02.7 Profile Statistics
- [ ] **FR-02.7.1** The system must maintain denormalized counts for followers, following, threads, and likes received.
- [ ] **FR-02.7.2** Counts must be incremented and decremented atomically as the corresponding events occur.

#### FR-02.8 E2E Key Management
- [ ] **FR-02.8.1** A user must be able to register their X25519 ECDH public key against their profile.
- [ ] **FR-02.8.2** The public key must be readable by any user who wants to send an encrypted message.

---

### FR-03 — Thread (Social Posts)

#### FR-03.1 Authoring
- [ ] **FR-03.1.1** A user must be able to create a new thread post with text content.
- [ ] **FR-03.1.2** A user must be able to attach one or more media files (images, videos, GIFs) to a thread in a defined order.
- [ ] **FR-03.1.3** A user must be able to schedule a thread for future publication using `scheduledPublishAt`.
- [ ] **FR-03.1.4** A user must be able to save a thread as a draft before publishing.
- [ ] **FR-03.1.5** A user must be able to edit the content of a published thread; edits must be flagged (`isEdited`, `editedAt`).
- [ ] **FR-03.1.6** A user must be able to delete their own thread (soft delete).

#### FR-03.2 Thread Types
- [ ] **FR-03.2.1** A thread can be an original post, a reply to another thread (`parentThreadId`, `rootThreadId`), a repost (boost), or a quote of another thread (`quoteThreadId`).
- [ ] **FR-03.2.2** Reply chains must be navigable from root thread to leaf reply using `rootThreadId`.

#### FR-03.3 Mentions
- [ ] **FR-03.3.1** The system must detect `@username` patterns in thread content and create `ThreadMention` records.
- [ ] **FR-03.3.2** Mentioned users must receive a `MENTION` notification.

#### FR-03.4 Hashtags
- [ ] **FR-03.4.1** The system must detect `#hashtag` patterns in thread content and create `ThreadHashtag` records with the character offset.
- [ ] **FR-03.4.2** New hashtags must be created in the hashtag registry; existing ones must have their stats incremented.

#### FR-03.5 Poll
- [ ] **FR-03.5.1** A user must be able to attach a poll to a thread with 2–4 options and an expiry time.
- [ ] **FR-03.5.2** A user must be able to vote on an active poll (one vote per option unless multiple votes are allowed).
- [ ] **FR-03.5.3** Once a poll expires, no further votes are accepted.
- [ ] **FR-03.5.4** Vote counts per option must be maintained in real time.

#### FR-03.6 Link Preview
- [ ] **FR-03.6.1** When a thread contains a URL, the system must asynchronously fetch and store the link preview metadata (title, description, image, canonical URL).

#### FR-03.7 Engagement
- [ ] **FR-03.7.1** A user must be able to like and un-like a thread; the `likeCount` on the thread must be updated atomically.
- [ ] **FR-03.7.2** A user must be able to repost and un-repost a thread; `repostCount` must be updated.
- [ ] **FR-03.7.3** A user must be able to bookmark and un-bookmark a thread for personal saved-content access.
- [ ] **FR-03.7.4** A thread author may choose to hide the like count from other viewers (`isHideLikeCount`).

#### FR-03.8 Visibility & Access Control
- [ ] **FR-03.8.1** A thread may be set to `PUBLIC`, `FOLLOWERS_ONLY`, or `PRIVATE`.
- [ ] **FR-03.8.2** A thread author must be able to restrict who can reply: `EVERYONE`, `FOLLOWERS_ONLY`, `MENTIONED_ONLY`, or `NOBODY`.

#### FR-03.9 Moderation
- [ ] **FR-03.9.1** Any user must be able to report a thread for policy violations.
- [ ] **FR-03.9.2** A moderation record must capture the reporter, reason, and resolution status.

---

### FR-04 — Chat & Messaging

#### FR-04.1 Direct Messaging
- [ ] **FR-04.1.1** A user must be able to start a direct (1:1) conversation with another user.
- [ ] **FR-04.1.2** Only one direct conversation may exist between any two users.
- [ ] **FR-04.1.3** A user must not be able to start a conversation with a user who has blocked them.

#### FR-04.2 Group Messaging
- [ ] **FR-04.2.1** A user must be able to create a group conversation with multiple participants.
- [ ] **FR-04.2.2** Group conversations support roles: `ADMIN` and `MEMBER`.
- [ ] **FR-04.2.3** An admin must be able to add or remove participants.
- [ ] **FR-04.2.4** A participant must be able to leave a group conversation.

#### FR-04.3 Message Sending
- [ ] **FR-04.3.1** A user must be able to send text messages in a conversation.
- [ ] **FR-04.3.2** A user must be able to attach media files to a message.
- [ ] **FR-04.3.3** Messages must be end-to-end encrypted: content is stored as AES-GCM ciphertext with the IV stored alongside it.

#### FR-04.4 Message Management
- [ ] **FR-04.4.1** A sender must be able to edit a sent message; the edit must be tracked (`editedAt`).
- [ ] **FR-04.4.2** A sender must be able to delete a sent message (soft delete, `deletedAt`).

#### FR-04.5 Reactions
- [ ] **FR-04.5.1** A user must be able to add an emoji reaction to any message.
- [ ] **FR-04.5.2** A user must be able to remove their own reaction.
- [ ] **FR-04.5.3** Each user may place at most one reaction of each emoji type per message.

#### FR-04.6 Read Receipts
- [ ] **FR-04.6.1** The system must track the last message each participant has read (`ConversationReadState`).
- [ ] **FR-04.6.2** Unread message counts per conversation must be maintained in `chat_actor_inboxes`.

#### FR-04.7 Conversation Block
- [ ] **FR-04.7.1** A user must be able to block a conversation, preventing further incoming messages in that thread.

#### FR-04.8 Moderation
- [ ] **FR-04.8.1** A user must be able to report a message for policy violations.

#### FR-04.9 E2E Key Exchange
- [ ] **FR-04.9.1** For group conversations, when a message is sent, the system must create one `MessageKey` record per recipient containing the message's symmetric key encrypted with that recipient's X25519 public key.
- [ ] **FR-04.9.2** A recipient must be able to fetch their own `MessageKey` to decrypt a message.

---

### FR-05 — Notification

#### FR-05.1 Notification Generation
- [ ] **FR-05.1.1** The system must generate a notification for the following events: like, reply, mention, follow, repost, quote, thread recommendation, system message.
- [ ] **FR-05.1.2** Multiple actors performing the same action on the same entity within a time window must be grouped into a single notification using a `groupKey`.
- [ ] **FR-05.1.3** The `actorCount` and `lastActorProfileId` fields must reflect the latest grouped state.

#### FR-05.2 Notification Delivery
- [ ] **FR-05.2.1** The system must attempt delivery via all channels the user has enabled: `IN_APP`, `EMAIL`, `PUSH_NOTIFICATION`.
- [ ] **FR-05.2.2** Delivery status per channel must be tracked (`NotificationDelivery`): `PENDING`, `SENT`, `DELIVERED`, `FAILED`.
- [ ] **FR-05.2.3** Failed deliveries must be retryable.

#### FR-05.3 Notification Preferences
- [ ] **FR-05.3.1** A user must be able to enable or disable each notification type per delivery channel independently.
- [ ] **FR-05.3.2** Preference changes must take effect for all subsequent notifications.

#### FR-05.4 Notification Management
- [ ] **FR-05.4.1** A user must be able to list their notifications.
- [ ] **FR-05.4.2** A user must be able to mark individual notifications as read.
- [ ] **FR-05.4.3** A user must be able to mark all notifications as read at once.
- [ ] **FR-05.4.4** The unread notification count in `ntf_notification_counters` must be updated on read.

---

### FR-06 — Hashtag & Trending

#### FR-06.1 Hashtag Lifecycle
- [ ] **FR-06.1.1** A hashtag must be created automatically the first time it appears in a thread.
- [ ] **FR-06.1.2** The system must store both the normalized form (lowercase, no special characters) and the original display form.
- [ ] **FR-06.1.3** Usage statistics (`HashtagStats`) must be updated each time a hashtag is referenced in a new thread.

#### FR-06.2 Trending
- [ ] **FR-06.2.1** Hourly usage counts must be recorded in `ht_hashtag_usage_hourly` for trend computation.
- [ ] **FR-06.2.2** The system must be able to compute trending hashtags over configurable time windows (e.g., last 1h, 6h, 24h).

#### FR-06.3 Related Hashtags
- [ ] **FR-06.3.1** When two hashtags appear in the same thread, a co-occurrence relationship (`HashtagRelation`) must be incremented.
- [ ] **FR-06.3.2** The system must expose related hashtags ordered by co-occurrence strength.

#### FR-06.4 Hashtag Discovery
- [ ] **FR-06.4.1** A user must be able to search for hashtags by name.
- [ ] **FR-06.4.2** Hashtags may carry metadata (category, language, description) for enriched discovery.
- [ ] **FR-06.4.3** A user must be able to follow a hashtag to receive its content in their feed.

#### FR-06.5 Hashtag Moderation
- [ ] **FR-06.5.1** An administrator must be able to set a hashtag's moderation status (e.g., restricted, banned).
- [ ] **FR-06.5.2** Banned hashtags must be suppressed from search results and trending lists.

---

### FR-07 — Group (Community)

#### FR-07.1 Group Creation & Configuration
- [ ] **FR-07.1.1** A user must be able to create a group with a unique slug, name, description, avatar, and banner.
- [ ] **FR-07.1.2** A group must be set to `PUBLIC` or `PRIVATE` visibility at creation.
- [ ] **FR-07.1.3** The creator automatically becomes the `OWNER` of the group.
- [ ] **FR-07.1.4** A group owner must be able to update the group's name, description, avatar, banner, and visibility.
- [ ] **FR-07.1.5** A group owner must be able to delete (soft-delete) the group.

#### FR-07.2 Membership
- [ ] **FR-07.2.1** Any user may join a `PUBLIC` group without approval.
- [ ] **FR-07.2.2** Joining a `PRIVATE` group creates a `GroupMemberRequest` that must be approved by an authorized member.
- [ ] **FR-07.2.3** An authorized member must be able to approve or reject join requests.
- [ ] **FR-07.2.4** A member must be able to leave a group.
- [ ] **FR-07.2.5** An authorized member must be able to remove another member.
- [ ] **FR-07.2.6** The `grp_group_stats.member_count` must be kept in sync with membership changes.

#### FR-07.3 Invitation System
- [ ] **FR-07.3.1** An authorized member must be able to generate an invitation token with a configurable expiry and maximum usage count.
- [ ] **FR-07.3.2** A user who receives a valid token must be able to join the group directly, bypassing the approval queue for private groups.
- [ ] **FR-07.3.3** An authorized member must be able to revoke an invitation token.
- [ ] **FR-07.3.4** Expired or fully-used tokens must be automatically invalidated.

#### FR-07.4 Role & Permission Management
- [ ] **FR-07.4.1** The role hierarchy within a group is: `OWNER > ADMIN > MODERATOR > MEMBER`.
- [ ] **FR-07.4.2** The owner must be able to promote or demote any member's role.
- [ ] **FR-07.4.3** Admin and moderator capabilities are governed by the `permissions` bitmask on their `GroupMember` record, using the 12 flags defined in `GroupAdminPermission`.
- [ ] **FR-07.4.4** An authorized member must be able to grant or revoke individual permission flags to/from other admins/moderators.

#### FR-07.5 Community Rules
- [ ] **FR-07.5.1** An authorized member must be able to add, edit, reorder, and delete group rules.
- [ ] **FR-07.5.2** Rules must be displayed to members and prospective members.

#### FR-07.6 Content in Groups
- [ ] **FR-07.6.1** A group member must be able to post a thread into the group.
- [ ] **FR-07.6.2** An authorized member must be able to pin a thread within a group.
- [ ] **FR-07.6.3** An authorized member must be able to remove a thread from the group.
- [ ] **FR-07.6.4** If a group requires post approval (`requiresApproval = true`), threads must enter a pending state before being visible.

#### FR-07.7 Member Moderation
- [ ] **FR-07.7.1** An authorized member must be able to warn, mute (with expiry), or ban another member.
- [ ] **FR-07.7.2** A muted member must be prevented from posting or interacting within the group until the mute expires.
- [ ] **FR-07.7.3** A banned member must be removed from the group and prevented from rejoining.
- [ ] **FR-07.7.4** All moderation actions must be recorded in `grp_group_moderation_actions` as an immutable audit log.

#### FR-07.8 Group Statistics
- [ ] **FR-07.8.1** The system must maintain real-time counts of `member_count` and `thread_count` in `grp_group_stats`.

---

## Non-Functional Requirements

### NFR-01 — Security

- **NFR-01.1** All message content in chat must be end-to-end encrypted using AES-GCM; the server must never store plaintext message content.
- **NFR-01.2** E2E key exchange must use X25519 ECDH; public keys must be stored per profile.
- **NFR-01.3** For group messages, one encrypted key copy per recipient must be stored in `chat_message_keys`.
- **NFR-01.4** Passwords must be stored using a strong, salted hashing algorithm (e.g., bcrypt, argon2).
- **NFR-01.5** OTPs must have a short TTL (e.g., 10 minutes) and a maximum attempt count (e.g., 5) to prevent brute-force.
- **NFR-01.6** Session tokens must be device-scoped; compromise of one device's token must not affect others.
- **NFR-01.7** Role-Based Access Control (RBAC) with bitmask permissions must be enforced for all group operations.
- **NFR-01.8** Block and mute relationships must be enforced at the data layer before content is served.

### NFR-02 — Data Integrity

- **NFR-02.1** Every entity must carry full audit metadata: `created_by`, `created_at`, `updated_by`, `updated_at`.
- **NFR-02.2** Deletions must use soft delete (`deleted_at`); hard deletes are not permitted for auditable entities.
- **NFR-02.3** All database schema changes must be managed as Liquibase migrations with rollback scripts.
- **NFR-02.4** Denormalized counter columns (`likeCount`, `follower_count`, etc.) must be updated atomically to prevent drift.

### NFR-03 — Performance & Scalability

- **NFR-03.1** Frequently read aggregates (profile stats, hashtag stats, group stats) must be maintained as denormalized counters to avoid expensive `COUNT` queries at read time.
- **NFR-03.2** The inbox pattern (`chat_actor_inboxes`) must allow efficient per-user inbox queries without full conversation scans.
- **NFR-03.3** Hourly hashtag usage records (`ht_hashtag_usage_hourly`) must support time-windowed trending queries without full-table aggregation.
- **NFR-03.4** The domain layer must have zero framework dependencies, enabling straightforward extraction into independent services.

### NFR-04 — Maintainability

- **NFR-04.1** Domain models must have no dependency on infrastructure frameworks (Spring, JDBC, etc.).
- **NFR-04.2** All domain-to-entity conversions must go through explicit `JdbcMapper` implementations; no implicit ORM magic.
- **NFR-04.3** Database table names must use a module prefix (`iam_`, `prf_`, `thr_`, `chat_`, `ntf_`, `ht_`, `grp_`) to prevent naming collisions.
- **NFR-04.4** Each aggregate root must have a dedicated repository interface with no cross-aggregate repository dependencies.

### NFR-05 — Extensibility

- **NFR-05.1** The `ProfileMetadata` key-value model must allow per-profile extension without schema migrations.
- **NFR-05.2** The notification type enum must be extendable to accommodate new event types without breaking existing delivery logic.
- **NFR-05.3** The `GroupAdminPermission` bitmask uses a `BIGINT`, supporting up to 63 distinct permission flags before schema change is required.

---

## Module Dependency Graph

```
domain/
  ├── libs:common
  └── libs:utils

database/
  ├── core:domain          ← depends on domain interfaces
  ├── libs:common
  ├── libs:utils
  ├── spring-boot-data-jdbc
  ├── spring-boot-data-neo4j
  ├── postgresql
  ├── neo4j-java-driver
  └── liquibase
```
