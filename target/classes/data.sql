-- Commented out initial user inserts as DataInitializer handles them
-- INSERT INTO users (username, password, email, role, enabled) VALUES
-- ('admin', '$2a$10$w4EefUNDHGyCViVqDMuRyeRJz4j0pCNj8.MM96DpffUq58p3AKn4q', 'admin@example.com', 'ADMIN', true)
-- ON CONFLICT (username) DO NOTHING;

-- INSERT INTO users (username, password, email, role, enabled) VALUES
-- ('developer', '$2a$10$pEw/S608qQz.bSNX4j8mIuJ0j.f4y4Q.r6k1N.1h2L/N3f5h.P.4e', 'dev@example.com', 'DEVELOPER', true)
-- ON CONFLICT (username) DO NOTHING;

-- INSERT INTO users (username, password, email, role, enabled) VALUES
--     ('reporter', '$2a$10$d8i7Yt5F2.f/t.3b/Uv6A..6o2b2q.G2b6J.n.o.u.N.t.x.y.e.t', 'reporter@example.com', 'REPORTER', true)
-- ON CONFLICT (username) DO NOTHING;

-- Removed initial bug inserts as they caused timing issues with DataInitializer
-- INSERT INTO bugs (title, description, status, priority, created_at, reporter_id) VALUES
-- ('UI Button Misaligned', 'The submit button on the login form is off-center on mobile devices.', 'NEW', 'MEDIUM', CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'reporter')),
-- ('API Call Fails', 'The GET /api/users endpoint returns 500 error sometimes.', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'developer'));