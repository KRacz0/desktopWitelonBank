
#  WitelonBank (aplikacja desktopowa)

## Wprowadzenie
**WitelonBank** – aplikacja bankowa napisanej w języku Java z wykorzystaniem JavaFX. Projekt umożliwia obsługę klienta oraz panel administratora.

## Wykorzystane technologie
- **Java 21** – główny język programowania
- **JavaFX 21** – biblioteka do tworzenia interfejsu graficznego
- **Maven** – system zarządzania zależnościami (zawiera wtyczkę `javafx-maven-plugin`)
- **JSON** do wymiany danych z API (`org.json` i `Jackson`)

## Uruchomienie lokalne
1. Zainstaluj JDK 21 oraz Maven.
2. W katalogu projektu uruchom:
   ```bash
   mvn clean javafx:run
   ```
   Komenda wykorzystuje `javafx-maven-plugin` (główna klasa: `com.kracz0.desktopwitelonbank.App`).
3. Alternatywnie możesz uruchomić wygenerowany plik JAR `out/artifacts/desktopWitelonBank_jar/desktopWitelonBank.jar`, ale wymaga to właściwej konfiguracji modułów JavaFX.

## Struktura projektu
```
com.kracz0.desktopwitelonbank
├── App.java                – punkt startowy aplikacji
├── Config                  – konfiguracja adresów API
├── Controllers             – kontrolery widoków (Client/Admin)
├── Models                  – modele danych i singleton `Model`
├── Services                – logika komunikacji z API
├── Utils                   – pomocnicze klasy HTTP
├── Views                   – `ViewFactory` do ładowania widoków FXML
└── resources               – pliki FXML i arkusze CSS
```

## Najważniejsze klasy
### `App`
Klasa główna aplikacji, rozszerza `javafx.application.Application`. W metodzie `start()` wywołuje `ViewFactory.showLoginWindow()`.

### `Model`
Singleton przechowujący stan aplikacji (zalogowany użytkownik, dostęp do `ViewFactory`). Oferuje m.in.:
- `getInstance()` – dostęp do instancji
- `getViewFactory()` – fabryka widoków
- `setLoggedUser(User user)` / `getLoggedUser()` – ustawienie i pobranie bieżącego użytkownika
- `logout()` – wylogowanie i wyczyszczenie tokenu

### `ViewFactory`
Odpowiada za wczytywanie i przechowywanie widoków z plików FXML. Udostępnia metody takie jak:
- `showLoginWindow()` – otwarcie okna logowania
- `showClientWindow()` – uruchomienie panelu klienta lub administratora
- `showTwoFactorModal(email, stage)` – weryfikacja dwuskładnikowa
- `getDashboardView()`, `getTransactionsView()` itd. – zwracają załadowane panele klienta

### Kontrolery klienta (`Controllers.Client`)
- **`ClientController`** – zarządza głównym układem okna klienta i zmienia centralną zawartość w zależności od wyboru menu.
- **`DashboardController`** – wczytuje podstawowe informacje o koncie, karty płatnicze, kryptowaluty i ostatnie transakcje. Zawiera metody takie jak `refreshDashboardData()` czy `loadTransactions()`.
- **`TransactionsController`** – obsługa historii i tworzenia przelewów.
- **`AddressBookController`** – zarządza listą zapisanych odbiorców (dodawanie, edycja, usuwanie).
- **`CryptoController`** – umożliwia zakup i sprzedaż kryptowalut z użyciem `CryptoService`.
- **`ClientMenuController`** – obsługuje przyciski na pasku bocznym.
- **Modale** (np. `TwoFactorController`) – dodatkowe okna dialogowe (weryfikacja 2FA).

### Kontrolery administratora (`Controllers.Admin`)
- **`AdminController`** – panel administracyjny z listą kont i statystykami systemowymi; umożliwia generowanie raportów PDF.
- **`AdminAccountDetailsController`** – szczegóły wybranego konta (blokowanie, zmiana limitu, podgląd transakcji).

### Modele danych (`Models` i `Models.DTO`)
- `User`, `Card`, `Recipient` – podstawowe encje klienta
- DTO do komunikacji z API: `Account`, `Transfer`, `CryptoWallet`, `AdminStats`, `StandingOrder`, `AccountAdmin`

### Serwisy (`Services`)
Zawierają logikę komunikacji z serwerem i przetwarzania odpowiedzi:
- `AuthService` – logowanie i weryfikacja 2FA (`login()`, `verify2FA()`)
- `DashboardService` – pobieranie kont, przelewów oraz zleceń stałych
- `TransactionsService` – wysyłanie przelewu (`sendTransfer`)
- `AddressBookService` – operacje CRUD na odbiorcach
- `CryptoService` – obsługa portfela kryptowalut i aktualnych kursów
- `AdminService` – funkcje panelu administratora (pobieranie kont, blokowanie, raporty)

### Utils
- **`ApiClient`** – konfiguracja `HttpClient` oraz metoda `authorizedRequest()` dodająca nagłówek `Authorization`
- **`HttpUtil`** – prostszy builder zapytań HTTP używany m.in. przy logowaniu

### Config
- `ApiConfig` – stałe z adresami endpointów API, np. `LOGIN`, `PRZELEWY`, `ADMIN_KONTA`

## Zasoby
Pliki FXML oraz style CSS znajdują się w `src/main/resources`. Kluczowe widoki to `Login.fxml`, `Client.fxml`, `Admin.fxml` oraz podwidoki w katalogach `Client` i `Admin`.

## Komunikacja z API
Aplikacja korzysta z REST API pod adresem bazowym zdefiniowanym w `ApiConfig.BASE_URL` (`https://witelonapi.host358482.xce.pl/api`). Do autoryzacji używany jest token JWT zwracany po poprawnym logowaniu.

## Funkcjonalności 

### Funkcjonalności dla użytkownika
- logowanie i weryfikacja dwuskładnikowa
- podgląd salda i historii transakcji
- wykonywanie przelewów oraz zarządzanie listą odbiorców
- podgląd kart płatniczych i płatności cyklicznych
- inwestowanie środków w kryptowaluty
- eksport historii operacji do pliku

![0608(1)](https://github.com/user-attachments/assets/b28e8d8e-ff76-4d78-bf92-30d93068495e)


### Funkcjonalności dla administratora
- logowanie do panelu administracyjnego
- zarządzanie kontami użytkowników (blokowanie, zmiana limitów)
- podgląd i filtrowanie transakcji w systemie
- generowanie raportów
- monitorowanie działania systemu (statystyki)

![Admin](https://github.com/user-attachments/assets/8cec08ba-78d8-4942-ba32-e509f2eb9bba)

## Testy jednostkowe
Kod testów znajduje się w katalogu `src/test/java/com/kracz0/desktopwitelonbank` i korzysta z biblioteki **JUnit 5**. Do symulacji zapytań HTTP używany jest **MockWebServer**, a w zależności testów dostępny jest również **Mockito**. Do tworzenia i parsowania obiektów JSON wykorzystywany jest pakiet `org.json` oraz `Jackson`.

### Podział testów
**Models**
- `CardTest` – sprawdza poprawność konstrukcji i dostęp do pól modelu `Card`.
- `ModelTest` – weryfikuje działanie singeltona `Model` (logowanie, token, flaga modalu).
- `RecipientTest` – testuje settery i gettery klasy `Recipient`.
- `UserTest` – testuje settery i gettery klasy `User`.
- `DTO/AccountAdminTest` – poprawność danych administratora rachunku.
- `DTO/AccountTest` – weryfikuje pola podstawowego konta.
- `DTO/AdminStatsTest` – testuje model statystyk administratora.
- `DTO/CryptoWalletTest` – przy użyciu refleksji sprawdza pola portfela kryptowalut.
- `DTO/StandingOrderTest` – pełny zestaw pól zlecenia stałego.
- `DTO/TransferTest` – rozróżnia przelew przychodzący i wychodzący.

**Services**
- `AddressBookServiceTest` – z użyciem `MockWebServer` testuje operacje CRUD na odbiorcach.
- `CryptoServiceTest` – symuluje zakup i sprzedaż kryptowalut.
- `StandingOrderMainServiceTest` – weryfikuje tworzenie, aktualizację i usuwanie zleceń stałych.
- `TransactionsServiceTest` – sprawdza walidację danych przy wysyłaniu przelewu.

**Utils**
- `ApiClientTest` – upewnia się, że `authorizedRequest` i `basicRequest` nadają właściwe nagłówki.

### Używane biblioteki testowe
- **JUnit Jupiter** – podstawowy framework uruchamiający testy.
- **Mockito** – pozwala na tworzenie atrap i weryfikację interakcji (w projekcie dostępny do rozbudowy testów).
- **MockWebServer** – serwer HTTP w pamięci do testowania klas `Service`.
- **org.json/Jackson** – biblioteki do budowania i odczytu danych JSON w testach.

Aby uruchomić wszystkie testy, w katalogu projektu wykonaj:
```bash
mvn test
```

Aby uruchomić testy, należy upewnić się, że w pliku `pom.xml` znajdują się następujące zależności:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
    <version>4.12.0</version>
    <scope>test</scope>
</dependency>
```

## Lista funkcjonalności
| OPZ    | Funkcjonalność                                                                                                          | API | web | mobile | desktop |
|--------|-------------------------------------------------------------------------------------------------------------------------|-----|-----|--------|---------|
| WBK-01 | Administrator może logować się do systemu.                                                                              | ✓    |     |        |<code style="color : green">✓</code>       |
| WBK-02 | Administrator może zarządzać kontami użytkowników (blokowanie, odblokowywanie, limity).                                 | ✓   |     |        | <code style="color : green">✓</code>         |
| WBK-03 | Administrator może monitorować transakcje w systemie.                                                                   | ✓   |     |        | <code style="color : green">✓</code>        |
| WBK-04 | Administrator może generować raporty finansowe.                                                                         | ✓   |     |        | <code style="color : green">✓</code>       |
| WBK-05 | Administrator widzi statystyki systemowe.                                                                               | ✓   |     |        | <code style="color : green">✓</code>      |
| WBK-06 | Użytkownik może inwestować środki (cryptowaluty).                                                                       | ✓   |     |        | <code style="color : green">✓</code>      |
| WBK-07 | Użytkownik może logować się do swojego konta.                                                                           | ✓   | ✓   | ✓      |  <code style="color : green">✓</code>      |
| WBK-8 | Użytkownik może zresetować swoje hasło.                                                                                  | ✓   | ✓   | ✓      |         |
| WBK-9 | Użytkownik może sprawdzać saldo konta.                                                                                   | ✓   | ✓   | ✓      | <code style="color : green">✓</code>      |
| WBK-10 | Użytkownik może przeglądać historię transakcji.                                                                         | ✓   | ✓   | ✓      |<code style="color : green">✓</code>      |
| WBK-11 | Użytkownik może wykonywać przelewy.                                                                                     | ✓   | ✓   | ✓      |<code style="color : green">✓</code>         |
| WBK-12 | Użytkownik może zarządzać swoimi kartami płatniczymi (blokowanie, zmiana limitów).                                      | ✓   | ✓   | ✓      |         |
| WBK-13 | Użytkownik może skonfigurować płatności cykliczne.                                                                      | ✓   | ✓   | ✓      |<code style="color : green">✓</code>         |
| WBK-14 | Użytkownik otrzymuje powiadomienia o transakcjach na e-mail/push.                                                       | ✓   | ✓   | ✓      |         |
| WBK-15 | Użytkownik może dodać odbiorców do listy zapisanych przelewów.                                                          | ✓   | ✓   | ✓      | <code style="color : green">✓</code>       |
| WBK-16 | Użytkownik może eksportować historię transakcji.                                                                        | ✓   | ✓   | ✓      | <code style="color : green">✓</code>     |
| WBK-17 | Użytkownik może zamknąć konto.                                                                                          | ✓   | ✓   |        |         |
| WBK-18 | Użytkownik może się wylogować.                                                                                          | ✓   | ✓   | ✓      | <code style="color : green">✓</code>       |
| WBK-19 | System obsługuje język polski, angielski i niemiecki.                                                                   | ✓   | ✓   | ✓      | ✓       |

---

