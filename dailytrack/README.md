# DailyTrack — Track what you do. See how far you've come.
Run: set DB_USER / DB_PASSWORD (see application-example.properties), then `mvn spring-boot:run` and open http://localhost:8080
Users create all trackers themselves; nothing is seeded. All queries are scoped to the signed-in user (404 on foreign ids).
