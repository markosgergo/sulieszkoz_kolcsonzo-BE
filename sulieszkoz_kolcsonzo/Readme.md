# Sulieszkoz Kolcsonzo – Backend

Iskolai eszközkölcsönző rendszer REST API backendje. Spring Boot alapú, JWT autentikációval, MySQL adatbázissal.

---

## Technológiák

- Java 21
- Spring Boot 3.4
- Spring Security (JWT, BCrypt)
- Spring Data JPA (Hibernate)
- MySQL 8.0
- Lombok
- ZXing (QR kód generálás)
- Docker Compose (fejlesztői adatbázis)

---

## Első indítás

### 1. Előfeltételek

- [Java 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (az adatbázishoz)

### 2. Projekt klónozása

```bash
git clone https://github.com/markosgergo/sulieszkoz_kolcsonzo-BE.git
cd sulieszkoz_kolcsonzo-BE
```

### 3. Környezeti változók beállítása

Hozz létre egy `.env` fájlt a projekt gyökerében (ahol a `pom.xml` is van) az `.env.example` alapján:

```bash
cp .env.example .env
```

Majd töltsd ki a saját értékeiddel:

```
DB_PASSWORD=valaszd_meg_a_jelszot
JWT_SECRET=minimum_32_karakteres_titkos_kulcs
```

> A `.env` fájlt **soha ne commitold** be a repóba – a `.gitignore` már tartalmazza.

### 4. Adatbázis indítása Dockerrel

```bash
docker-compose up -d
```

Ez automatikusan elindítja a MySQL 8.0-s konténert és létrehozza a `mydb` adatbázist.
Az adatok a `mysql_data` Docker volume-ban tárolódnak, tehát újraindítás után sem vesznek el.

Az adatbázis leállításához:

```bash
docker-compose down
```

### 5. Alkalmazás indítása

IntelliJ IDEA-ban:
1. Nyisd meg a projektet
2. A **Run/Debug Configurations** ablakban az **Environment variables** mezőbe add meg:
   ```
   JWT_SECRET=az_env_fajlban_levo_ertek;DB_PASSWORD=az_env_fajlban_levo_ertek
   ```
3. Indítsd el a `SulieszkozKolcsonzoApplication` osztályt

Vagy parancssorból:

```bash
export JWT_SECRET=titkos_kulcs
export DB_PASSWORD=jelszó
mvn spring-boot:run
```

Az alkalmazás alapból a `http://localhost:8080` címen érhető el.

---

## API végpontok

### Autentikáció
| Metódus | Végpont | Leírás | Jogosultság |
|--------|---------|--------|-------------|
| POST | `/api/auth/login` | Bejelentkezés | Mindenki |
| POST | `/api/auth/logout` | Kijelentkezés | Bejelentkezett |
| GET | `/api/auth/me` | Saját adatok lekérése | Bejelentkezett |

### Felhasználók
| Metódus | Végpont | Leírás | Jogosultság |
|--------|---------|--------|-------------|
| GET | `/api/felhasznalok` | Összes felhasználó | Bejelentkezett |
| GET | `/api/felhasznalok/{id}` | Egy felhasználó | Bejelentkezett |
| GET | `/api/felhasznalok/kereses?nev=` | Keresés név alapján | Bejelentkezett |
| POST | `/api/felhasznalok` | Regisztráció | Mindenki |
| DELETE | `/api/felhasznalok/{id}` | Törlés | Bejelentkezett |

### Eszközök
| Metódus | Végpont | Leírás | Jogosultság |
|--------|---------|--------|-------------|
| GET | `/api/eszkozok` | Összes eszköz | Bejelentkezett |
| GET | `/api/eszkozok/{id}` | Egy eszköz | Bejelentkezett |
| GET | `/api/eszkozok/szabad` | Szabad eszközök | Bejelentkezett |
| GET | `/api/eszkozok/kereses?nev=` | Keresés név alapján | Bejelentkezett |
| GET | `/api/eszkozok/{id}/qrcode` | QR kód generálás | Bejelentkezett |
| POST | `/api/eszkozok` | Új eszköz | Bejelentkezett |
| PUT | `/api/eszkozok/{id}` | Eszköz módosítása | Bejelentkezett |
| DELETE | `/api/eszkozok/{id}` | Eszköz törlése | Bejelentkezett |

### Kölcsönzések
| Metódus | Végpont | Leírás | Jogosultság |
|--------|---------|--------|-------------|
| GET | `/api/kolcsonzesek` | Összes kölcsönzés | ADMIN, ALKALMAZOTT |
| GET | `/api/kolcsonzesek/sajat` | Saját kölcsönzések | Bejelentkezett |
| GET | `/api/kolcsonzesek/kesesben` | Késedelmes kölcsönzések | ADMIN, ALKALMAZOTT |
| POST | `/api/kolcsonzesek` | Új kölcsönzés | Bejelentkezett |
| PUT | `/api/kolcsonzesek/{id}/visszavetel` | Visszavétel | ADMIN, ALKALMAZOTT |

---

## Szerepkörök

| Szerepkör | Leírás |
|-----------|--------|
| `FELHASZNALO` | Diák – csak saját kölcsönzéseit látja |
| `ALKALMAZOTT` | Kezelheti a kölcsönzéseket és látja a késésben lévőket |
| `ADMIN` | Teljes hozzáférés |

---

## Tesztek futtatása

```bash
mvn test
```

---

## Projektstruktúra

```
src/main/java/.../
├── config/          # CORS és Security konfiguráció
├── controller/      # REST végpontok
├── dto/             # Adatátviteli objektumok
├── enums/           # Szerepkör és státusz enumok
├── exception/       # Saját kivételosztályok és globális kezelő
├── mapper/          # Entitás <-> DTO konverterek
├── model/           # JPA entitások
├── repository/      # Adatbázis műveletek
├── security/        # JWT filter és utility
└── service/         # Üzleti logika
```