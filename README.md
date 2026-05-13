# Ristkülik-Nullmäng (Tic-Tac-Toe) – JavaFX

## Kompileerimine ja käivitamine

### Eeldused
- Java 17+ (nt OpenJDK 17)
- JavaFX SDK (https://gluonhq.com/products/javafx/)

### 1. Lae alla JavaFX SDK
Lae alla javafx-sdk ja paki see lahti, nt kausta `C:/javafx-sdk` (Windows)
või `/usr/local/javafx-sdk` (Linux/Mac).

### 2. Kompileeri
Windowsis (käivita `src` kaustas):
```
javac --module-path "C:/javafx-sdk/lib" --add-modules javafx.controls -d out src/*.java
```

Linuxis/Macis:
```
javac --module-path /usr/local/javafx-sdk/lib --add-modules javafx.controls -d out src/*.java
```

### 3. Käivita
```
java --module-path "C:/javafx-sdk/lib" --add-modules javafx.controls -cp out Main
```

---

## Failid
- `game_log.txt` – iga mängu käigud ja tulemus (luuakse automaatselt)
- `scores.txt`   – mängijate võitude arv (luuakse automaatselt)

## Juhised
- Klõpsa lahtril, et teha käik
- **Ctrl+Z** – võta viimane käik tagasi
- Nupp **↩ Võta tagasi** – sama mis Ctrl+Z
- Nupp **🔄 Uus mäng** – alustab uut mängu (skoorid jäävad alles)
- Nupp **📋 Vaata logi** – kuvab eelmiste mängude logi
