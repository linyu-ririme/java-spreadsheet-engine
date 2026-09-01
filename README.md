# Java Spreadsheet Engine

[![Test Java Spreadsheet Engine](https://github.com/linyu-ririme/java-spreadsheet-engine/actions/workflows/test.yml/badge.svg)](https://github.com/linyu-ririme/java-spreadsheet-engine/actions/workflows/test.yml)

A compact spreadsheet calculation engine written in Java for an object-oriented programming project at the Conservatoire National des Arts et Métiers (CNAM).

The project focuses on the model and calculation layer behind a spreadsheet: polymorphic cell contents, formula evaluation, automatic recalculation, circular-reference detection, and rollback when an invalid update would corrupt the sheet.

## Features

- Numeric, text, formula, and empty cells through a shared `IContenu` interface
- Binary arithmetic formulas using `+`, `-`, `*`, and `/`
- Numeric literals, signed operands, and cell references such as `=A1+2` or `=B1*-3`
- Automatic recalculation after a referenced cell changes
- Direct and indirect circular-reference detection
- Transaction-like rollback when a formula or dependent update is invalid
- Spreadsheet-style display behavior, including `#DIV/0!`
- Input validation for references and grid boundaries
- A dependency-free test harness containing 65 assertions

## Architecture

| Class | Responsibility |
| --- | --- |
| `Feuille` | Stores cells, validates references, recalculates the grid, and rolls back invalid mutations |
| `IContenu` | Defines the common contract for cell content |
| `ContenuNombre` | Stores and formats numeric values |
| `ContenuTexte` | Stores text and rejects numeric evaluation |
| `ContenuFormule` | Parses and evaluates formulas and cell references |
| `SpreadsheetDemo` | Demonstrates recalculation, cycle rejection, and rollback |
| `TestTableur` | Exercises references, formulas, error cases, cycles, and state consistency |

## Run locally

Requirements: JDK 17 or later.

```bash
mkdir -p out
javac -encoding UTF-8 -d out src/main/java/*.java src/test/java/*.java
java -cp out TestTableur
java -cp out SpreadsheetDemo
```

Expected test result:

```text
Tests OK: 65
```

## Example

Given the following cells:

```text
A1 = 10
B1 = =A1*2
C1 = =B1+5
```

changing `A1` to `7` automatically recalculates `B1` to `14` and `C1` to `19`. An attempted update such as `A1 = =C1+1` is rejected as a circular reference, and the previous value of `A1` is restored.

## Scope and limitations

This is a learning project rather than a full Excel-compatible parser. A formula currently contains one binary operation, columns use single-letter references (`A` to `Z`), and data is stored in memory.

## Coursework attribution

The calculation engine and test suite in this repository are the student's implementation. A Swing GUI supplied for the CNAM course was used during the original assignment but is intentionally not redistributed here because the supplied file did not include a public redistribution license.

## License

The student-authored code in this repository is available under the MIT License.
