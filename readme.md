Techstack:
- React
- TypeScript
- Java Spring Boot
- PostgreSQL
- Docker

Idee:
Autobahn Safety Monitor mit Autobahn App API für eine automatische Gefahrenstellenerkennung.

Umgesetzter Minimalumfang:
- Öffentlicher Endpunkt für aktuelle Warnungen, Baustellen und Sperrungen
- Abgesicherter Endpunkt für ein persönliches Dashboard
- Persistenzschicht mit PostgreSQL
- Externe Service-Anbindung an die Autobahn App API mit Cache-Fallback
- Demo-Account und markierte Demo-Daten

Planung:
- Heatmap für Unfälle und Gefahren
- Risiko-Score für Routen
- Persönliches Dashboard

Wie starten:
- `docker compose up --build`
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

Demo-Zugang:
- Benutzername: `demo_driver`
- Passwort: `Demo123!`

Vorbereitete, aber bewusst noch nicht ausgearbeitete Qualitätsbausteine:
- Teststruktur für Unit-, Integrations-, e2e-, Security- und Architekturtests
- Doku-Struktur für arc42, ADRs und ReadTheDocs
- Einfache GitHub Actions Pipeline als Startpunkt

Maintainer:
Louisa Böhme
Zlata Polovka
Michael Fischermann
