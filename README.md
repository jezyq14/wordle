# Wordle

Gra Wordle stworzona na podstawie wersji z NYT + dodatkowy tryb nielimitowany. Polega ona na zgadnięciu w 6 próbach
5-literowego słowa spośród 2315 dostępnych.

## Użyte technologie

- Java 21
- [JavaFX 21](https://openjfx.io/)
- [Gradle](https://gradle.org/) (Kotlin DSL)
- [ControlsFX](https://controlsfx.github.io/) (Toasty)
- [Jackson](https://github.com/fasterxml/jackson) (parser JSON)
- `jlink` i `jpackage`
- Github Actions

## Funkcjonalność

- Tryb Daily-zgadywanie słowa z NYT
- Tryb Unlimited - losowe słowo ze słownika 2315 słów
- Wpisywanie z klawiatury guzikowej, jak i fizycznej

## Uruchamianie

### Windows

1. Pobierz najnowszy [Wordle-Windows-x64.zip](https://github.com/jezyq14/wordle/releases/latest/download/Wordle-Windows-x64.zip).
2. Wypakuj archiwum.
3. Uruchom plik `Wordle.exe`.

### Linux

1. Pobierz najnowsze archiwum [Wordle-Linux-x64.tar.gz](https://github.com/jezyq14/wordle/releases/latest/download/Wordle-Linux-x64.tar.gz).
2. Wypakuj i przejdź do folderu:
   ```bash
   tar -xvf Wordle-Linux-x64.tar.gz
   cd Wordle
   ```
3. Nadaj uprawnienia i uruchom:
   ```bash
   chmod +x ./bin/Wordle
   ./bin/Wordle
   ```