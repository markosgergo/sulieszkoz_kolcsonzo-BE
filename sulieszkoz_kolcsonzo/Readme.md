# SuliEszköz Kölcsönző – Backend

Iskolai IKT eszközök nyilvántartására és kölcsönzésére szolgáló rendszer REST API backendje. A projekt támogatja a foglalási folyamat adminisztrátori jóváhagyását és a QR-kód alapú gyors visszavételt.

---

## 🛠️ Technológiák

| Technológia | Verzió |
|---|---|
| Java (Amazon Corretto / Eclipse Temurin) | 21 |
| Spring Boot | 3.4.0 |
| Spring Security (JJWT, BCrypt) | JJWT 0.11.5 |
| Spring Data JPA (Hibernate) | Hibernate 6.6 |
| MySQL | 8.0 |
| Lombok | – |
| ZXing (QR kód generálás) | – |
| Docker & Docker Compose | Multi-stage build |

---

## 🚀 Első indítás

### Előfeltételek

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (ajánlott – Java és Maven telepítése **nem szükséges**)
- Git

### 1. Projekt klónozása

```bash
git clone https://github.com/markosgergo/sulieszkoz_kolcsonzo-BE.git
cd sulieszkoz_kolcsonzo-BE
```

### 2. Környezeti változók beállítása

Hozz létre egy `.env` fájlt a projekt gyökerében (ahol a `pom.xml` is található):

```env
DB_URL=jdbc:mysql://sulieszkoz_db:3306/sulieszkoz_kolcsonzo
DB_USER=root
DB_PASSWORD=titkos_adatbazis_jelszo
JWT_SECRET=minimum_32_karakteres_nagyon_titkos_kulcs
```

### 3. Indítás Dockerrel *(ajánlott)*

A projekt multi-stage Dockerfile-t használ, így a Docker egy tiszta környezetben lefordítja a forráskódot, majd elindítja a szervert és az adatbázist.

```bash
# Indítás (fordítással együtt)
docker-compose up --build -d
```

Az alkalmazás a **http://localhost:8080** címen lesz elérhető.

```bash
# Leállítás
docker-compose down
```

---

## 📡 API Végpontok

### Autentikáció

| Metódus | Végpont | Leírás | Jogosultság |
|---|---|---|---|
| `POST` | `/api/auth/login` | Bejelentkezés és JWT token igénylése | Public |
| `POST` | `/api/auth/register` | Új felhasználó regisztrációja | Public |

### Felhasználók

| Metódus | Végpont | Leírás | Jogosultság |
|---|---|---|---|
| `GET` | `/api/felhasznalok` | Összes felhasználó listázása | ADMIN, ALKALMAZOTT |
| `GET` | `/api/felhasznalok/me` | Saját profiladatok lekérése | Bejelentkezett |
| `PUT` | `/api/felhasznalok/{id}/szerepkor` | Felhasználó szerepkörének módosítása | ADMIN |
| `PUT` | `/api/felhasznalok/me/jelszo` | Saját jelszó megváltoztatása | Bejelentkezett |

### Eszközök

| Metódus | Végpont | Leírás | Jogosultság |
|---|---|---|---|
| `GET` | `/api/eszkozok` | Összes eszköz listázása | Bejelentkezett |
| `GET` | `/api/eszkozok/{id}` | Egy eszköz részletes adatai | Bejelentkezett |
| `GET` | `/api/eszkozok/{id}/qr` | Eszköz QR kódjának lekérése (Base64) | Bejelentkezett |
| `POST` | `/api/eszkozok` | Új eszköz felvétele | ADMIN, ALKALMAZOTT |
| `PUT` | `/api/eszkozok/{id}` | Eszköz adatainak módosítása | ADMIN, ALKALMAZOTT |
| `DELETE` | `/api/eszkozok/{id}` | Eszköz törlése (logikai törlés) | ADMIN, ALKALMAZOTT |

### Kölcsönzések

| Metódus | Végpont | Leírás | Jogosultság |
|---|---|---|---|
| `GET` | `/api/kolcsonzesek` | Az összes kölcsönzés listázása | ADMIN, ALKALMAZOTT |
| `GET` | `/api/kolcsonzesek/sajat` | Bejelentkezett felhasználó kölcsönzései | Bejelentkezett |
| `GET` | `/api/kolcsonzesek/kiadasra-var` | Jóváhagyásra váró kérelmek listája | ADMIN, ALKALMAZOTT |
| `GET` | `/api/kolcsonzesek/kesesben` | Lejárt határidejű kölcsönzések | ADMIN, ALKALMAZOTT |
| `POST` | `/api/kolcsonzesek` | Új foglalási kérelem leadása | USER (Tanuló) |
| `PUT` | `/api/kolcsonzesek/{id}/elfogadas` | Foglalás jóváhagyása (kiadás) | ADMIN, ALKALMAZOTT |
| `DELETE` | `/api/kolcsonzesek/{id}/elutasitas` | Foglalás elutasítása | ADMIN, ALKALMAZOTT |
| `PUT` | `/api/kolcsonzesek/{id}/visszavetel` | Visszavétel rögzítése (ID alapján) | ADMIN, ALKALMAZOTT |
| `PUT` | `/api/kolcsonzesek/visszavetel/eszkoz/{id}` | QR alapú visszavétel (Eszköz ID alapján) | ADMIN, ALKALMAZOTT |

---

## 🔐 Szerepkörök és Jogosultságok

| Szerepkör | Leírás |
|---|---|
| `USER` | Tanuló vagy tanár. Eszközöket böngészhet és foglalhat le. |
| `ALKALMAZOTT` | Kezelő személyzet. Jóváhagyja a kiadást, rögzíti a visszavételt (QR-ral is). |
| `ADMIN` | Rendszergazda. Teljes hozzáférés, felhasználói szerepkörök kezelése. |

---

## 📁 Projektstruktúra

```
src/main/java/com/kolcsonzo/suli/sulieszkoz_kolcsonzo/
├── config/         # Security (JWT) és CORS konfigurációk
├── controller/     # REST API végpontok (Controller réteg)
├── dto/            # Adatátviteli objektumok (Request/Response)
├── enums/          # Státusz és Szerepkör típusok
├── exception/      # Egyedi hibakezelők (Business/EntityNotFound)
├── mapper/         # Entity <-> DTO konverziók
├── model/          # JPA Entitások (Adatbázis modellek)
├── repository/     # Spring Data JPA interfészek
├── security/       # JWT Filter és Token Utility osztályok
└── service/        # Üzleti logika megvalósítása
```

---

## 🧪 Tesztelés

A backend egységtesztjei (JUnit 5 + Mockito) a következő paranccsal futtathatók:

```bash
mvn test
```

Az API manuális teszteléséhez a projekthez mellékelve van egy **Postman Collection**, amely tartalmazza az összes végpontot és az automatikus JWT token kezelést.