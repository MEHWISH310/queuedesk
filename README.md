# QueueDesk

QueueDesk is an internal helpdesk ticketing simulator written in Java. Staff log bugs, feature requests and access requests; the program triages them by priority and age, stores them, and reports on them.

It is the running project of the **Grow before you Groww** pre-internship program, and it grows month by month:

| Month | Focus | What was added to QueueDesk |
|---|---|---|
| 1 | Java fundamentals | Ticket model, inheritance, collections, file I/O, exceptions, daily report |
| 2 | Java deep dive | Stream-based analytics, `Optional`, JSON persistence with Jackson, unit tests |

## Tech stack

- Java 17
- Maven 3.9+
- Jackson 2.21 (`jackson-databind`, `jackson-datatype-jsr310`)
- JUnit 5, Mockito and AssertJ (test scope)

## Project structure

```
queuedesk/
├── pom.xml
├── tickets.txt              legacy pipe-delimited input (one-time migration source)
├── tickets.json             JSON store, created by the migration
├── daily_report.txt         sample output of the end-of-day report
└── src/
    ├── main/java/com/queuedesk/
    │   ├── Ticket.java                    abstract base class
    │   ├── BugTicket.java
    │   ├── FeatureRequestTicket.java
    │   ├── AccessRequestTicket.java
    │   ├── Escalatable.java               interface for escalation
    │   ├── Priority.java  Status.java  Severity.java
    │   ├── Repository.java                generic in-memory store
    │   ├── TicketComparators.java
    │   ├── TriageBoard.java
    │   ├── TicketAnalytics.java           stream-based analytics
    │   ├── TicketFileLoader.java          pipe-delimited loader
    │   ├── ReportGenerator.java
    │   ├── TicketJsonStore.java           JSON save and load
    │   ├── TicketMigration.java           tickets.txt to tickets.json
    │   ├── JsonMapperProvider.java        single shared ObjectMapper
    │   ├── InvalidTicketDataException.java
    │   ├── TicketNotFoundException.java
    │   └── *Demo / *Runner / QueueNumberSimulator   runnable examples
    └── test/java/com/queuedesk/
        ├── TicketAnalyticsTest.java
        ├── RepositoryTest.java
        ├── TicketJsonStoreTest.java
        └── TicketMigrationTest.java
```

## Month 1: Java fundamentals

1. **Queue Number Simulator** (`QueueNumberSimulator`). Reads a starting ticket number and a customer count with `Scanner`, prints "Now serving ticket #N", flags every 5th number as a VIP lane and every 10th as a priority recheck using `%`, and keeps running totals with plain variables.
2. **Ticket class** (`Ticket`). Private fields with getters, setters only where allowed, two constructors (the short one chains to the full one with `this(...)`), a `private static` id counter, `ageInHours()`, and `Ticket.totalCreated()`. `Priority` and `Status` are enums.
3. **Subclasses and a capability.** `Ticket` is abstract with `estimateEffortHours()`. `BugTicket`, `FeatureRequestTicket` and `AccessRequestTicket` extend it. The `Escalatable` interface (with a default `escalationBadge()`) is implemented only by the bug and access tickets. `TriageBoard` loops over the base type and uses `instanceof Escalatable e` to show escalation state.
4. **Repository and triage queue.** `Ticket implements Comparable<Ticket>` (priority descending, then creation time ascending). `TicketComparators` holds comparators built with `Comparator.comparing(...).thenComparing(...)`. `Repository<T extends Ticket>` offers `add`, `all` (unmodifiable), `findById` and `groupByAssignee`. A `PriorityQueue` serves tickets by natural order or by a comparator.
5. **Loading, saving, reporting.** `TicketFileLoader` reads `tickets.txt` with try-with-resources and `BufferedReader`. A bad line raises the checked `InvalidTicketDataException` with the line number. `Repository.findById` raises the unchecked `TicketNotFoundException`. `ReportGenerator` builds an end-of-day summary with `StringBuilder` and writes it with `BufferedWriter`.

## Month 2: Java deep dive

1. **TicketAnalytics.** Stream pipelines for `countByPriority`, `groupByAssignee`, `averageEffortHours` (empty list handled), `totalEffortHours` (with a loop version beside it for comparison), `oldestOpenTicket` and `tagLine` (`flatMap`, `distinct`, `sorted`, `joining`).
2. **Optional.** `Repository.findByIdSafe` returns `Optional<T>` next to the throwing `findById`; `findById` is built on top of it. Call sites use `.map`, `.ifPresentOrElse` and `.orElseThrow`.
3. **JSON persistence.** `Ticket` carries `@JsonTypeInfo` and `@JsonSubTypes` (`bug`, `feature`, `access`), so subtypes round-trip as their real classes. `JsonMapperProvider` builds the `ObjectMapper` once. `TicketJsonStore` saves and loads and wraps Jackson's exceptions in `InvalidTicketDataException`, so callers never see Jackson types.
4. **Migration.** `TicketMigration.loadOrMigrate` reads `tickets.json` if it exists; otherwise it reads the old `tickets.txt` once, writes `tickets.json`, and uses JSON from then on.
5. **Tests.** 16 JUnit 5 tests with AssertJ assertions. Mockito mocks `Ticket` to isolate the analytics logic and to control `createdAt`. `@TempDir` keeps file tests away from the real data files.

## Getting started

Prerequisites: JDK 17 and Maven 3.9 or newer.

```bash
mvn compile
mvn test
```

Run any example from the project root (the data files are read from the working directory):

```bash
mvn exec:java "-Dexec.mainClass=com.queuedesk.QueueNumberSimulator"
```

Quote the whole `-D...` argument as shown; PowerShell otherwise splits it at the dot.

| Class | What it shows |
|---|---|
| `QueueNumberSimulator` | Month 1 console simulator (reads input) |
| `TicketTestRunner` | Loads `tickets.txt`, prints and writes the daily report, triggers `TicketNotFoundException` |
| `AnalyticsDemo` | Every `TicketAnalytics` method on a small sample |
| `OptionalDemo` | `findById` versus `findByIdSafe`, and the three `Optional` call-site styles |
| `JsonRoundTripDemo` | Serializes mixed tickets to JSON and reads them back |
| `JsonStoreDemo` | Migration, save and reload, and error handling for bad or missing files |

## Data formats

`tickets.txt` (legacy), one ticket per line: `title|description|requester|priority|status`

```
Login page broken|Cannot log in on Chrome|Alice|HIGH|OPEN
```

`tickets.json`, a list where each entry carries a `type` property:

```json
[ {
  "type" : "bug",
  "id" : 1,
  "title" : "Login crashes",
  "priority" : "CRITICAL",
  "status" : "OPEN",
  "assignee" : "Dave",
  "tags" : [ "auth" ],
  "severity" : "BLOCKER",
  "escalated" : true,
  "escalationLevel" : 2
} ]
```

(Shortened; real entries also include `description`, `requester`, `createdAt` and `stepsToReproduce`.)

## Design notes

- Escalation is an interface, not part of `Ticket`, because only some ticket types can be escalated.
- Bad input is a checked exception (`InvalidTicketDataException`); a missing ticket is unchecked (`TicketNotFoundException`) and is caught once at the top of the console app.
- `findById` suits callers that expect the ticket to exist; `findByIdSafe` suits callers for whom a miss is normal.
- Jackson is confined to `TicketJsonStore`, `JsonMapperProvider` and the annotations on the model.
- A root-level `List<Ticket>` loses its element type at runtime, so `TicketJsonStore` writes with `writerFor(new TypeReference<List<Ticket>>() { })`. Without it the `type` property is dropped and the file cannot be read back.
- Subclasses have a second, `@JsonCreator` constructor that restores the saved `id` and `createdAt`, and the id counter is moved past the highest loaded id.

## Known limitations

- `tickets.txt` has no ticket-type column, so migrated tickets all become `FeatureRequestTicket` entries with zero votes.
- After loading from JSON, `Ticket.totalCreated()` reports the highest id seen, not a count of created objects.
- The demo classes are manual harnesses; the automated checks are the JUnit tests.

## Roadmap

The program continues with Spring Boot, persistence and services (LoanApplication), followed by Kafka, Redis and Docker.