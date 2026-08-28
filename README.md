# Search Trees

[![Build Pipeline](https://github.com/vlloikov/search-trees/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/vlloikov/search-trees/actions/workflows/build.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![JVM](https://img.shields.io/badge/JVM-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Version](https://img.shields.io/badge/version-0.4.0-blue)](https://github.com/vlloikov/search-trees)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Kotlin/JVM library that implements binary search trees behind a shared, type-safe API. Trees store unique key-value
pairs, order keys through `Comparable`, and expose structural nodes as read-only views.

## Project status

Version `0.4.0` includes complete implementations of an unbalanced binary search tree, a height-balanced AVL tree,
and a red-black tree.

| Implementation | Status | Height | Search, insertion, removal |
|---|---|---:|---:|
| `BinarySearchTree` | Implemented | `O(h)`, up to `O(n)` | `O(h)` |
| `AvlTree` | Implemented | `O(log n)` | `O(log n)` |
| `RedBlackTree` | Implemented | `O(log n)` | `O(log n)` |

All three trees support the shared public API and ordered traversal. The project remains in pre-`1.0` development
while the architecture is reviewed, duplicated implementation logic is evaluated, and the public API is stabilized.

## Features

- one `SearchTree<K, V>` contract for every implementation;
- insertion, replacement, search, removal, membership checks, minimum, and maximum;
- lazy in-order iteration over nodes, keys, and values;
- unique keys determined by `Comparable.compareTo`;
- read-only public `TreeNode` views with balancing details kept internal;
- AVL rotations and height restoration after insertion and removal;
- red-black recoloring and rotations after insertion and removal;
- unit tests for public behavior, structural cases, and balancing invariants;
- automated formatting, static analysis, testing, coverage, documentation, and assembly.

## Technology stack

| Area | Technology |
|---|---|
| Language | Kotlin 2.3.20 |
| Runtime | JVM 21 |
| Build | Gradle 9.4.0 with Kotlin DSL and the Gradle Wrapper |
| Testing | Kotlin Test and JUnit Platform |
| Code quality | ktlint and Detekt |
| Coverage | Kover |
| API documentation | KDoc and Dokka |
| Continuous integration | GitHub Actions |

## Getting started

Clone the repository and run the complete local verification pipeline:

```bash
git clone https://github.com/vlloikov/search-trees.git
cd search-trees
./gradlew ci
```

## Usage

Both completed trees implement the same interface. Use `AvlTree` when logarithmic height must be maintained, or
`BinarySearchTree` when an unbalanced implementation is sufficient.

```kotlin
import io.vloikov.searchtrees.SearchTree
import io.vloikov.searchtrees.avl.AvlTree

fun main() {
	val tree: SearchTree<Int, String> = AvlTree()

	tree.insert(40, "forty")
	tree.insert(20, "twenty")
	tree.insert(60, "sixty")
	tree.insert(10, "ten")

	println(tree.find(20)?.value) // twenty
	println(60 in tree) // true
	println(tree.min()?.key) // 10
	println(tree.max()?.key) // 60

	val previousValue = tree.insert(20, "updated")
	println(previousValue) // twenty

	println(tree.keys().toList()) // [10, 20, 40, 60]
	println(tree.values().toList()) // [ten, updated, forty, sixty]

	for (node in tree) {
		println("${node.key}: ${node.value}")
	}

	val removedValue = tree.remove(40)
	println(removedValue) // forty
}
```

To use the ordinary binary search tree, only the implementation changes:

```kotlin
import io.vloikov.searchtrees.bst.BinarySearchTree

val tree = BinarySearchTree<Int, String>()
```

The red-black tree is selected in the same way:

```kotlin
import io.vloikov.searchtrees.redblack.RedBlackTree

val tree = RedBlackTree<Int, String>()
```

### Common API

| Operation | Behavior |
|---|---|
| `insert(key, value)` | Inserts a new node or replaces an existing value; returns the previous value or `null` |
| `remove(key)` | Removes a node; returns the removed value or `null` |
| `find(key)` | Returns a read-only `TreeNode`, or `null` when the key is absent |
| `key in tree` | Checks whether an equivalent key exists |
| `min()` / `max()` | Returns the node with the smallest or largest key |
| `size` / `isEmpty()` | Reports the current number of nodes and whether the tree is empty |
| `iterator()` | Iterates over nodes in ascending key order |
| `keys()` / `values()` | Returns lazy sequences ordered by key |

When `V` is nullable, a `null` mutation result may mean either that no previous value existed or that the stored value
was itself `null`. Use `contains` first when this distinction matters.

## Build and verification

The project keeps local checks and GitHub Actions aligned through the `ci` Gradle task.

| Command | Purpose |
|---|---|
| `./gradlew test` | Runs all unit tests |
| `./gradlew ci` | Runs the complete verification pipeline used by CI |
| `./gradlew ktlintCheck` | Checks Kotlin formatting |
| `./gradlew formatCode` | Formats Kotlin sources with ktlint |
| `./gradlew detekt` | Runs static analysis |
| `./gradlew koverHtmlReport` | Generates the HTML coverage report |
| `./gradlew dokkaGenerateHtml` | Generates API documentation from KDoc |
| `./gradlew assemble` | Builds the library artifacts |

Generated reports are available locally at:

- tests: `build/reports/tests/test/index.html`;
- coverage: `build/reports/kover/html/index.html`;
- API documentation: `build/dokka/html/index.html`.

## Continuous integration

The [GitHub Actions pipeline](https://github.com/vlloikov/search-trees/actions/workflows/build.yml) runs for every pull
request and every push to `main`. It is split into dedicated jobs for:

1. code quality with ktlint and Detekt;
2. unit tests and a Kover coverage report;
3. Dokka API documentation;
4. final artifact assembly.

Coverage and documentation are uploaded as workflow artifacts, while failures in quality checks or tests block the
dependent jobs.

## Project structure

```text
src/main/kotlin/io/vloikov/searchtrees/
├── SearchTree.kt
├── TreeNode.kt
├── bst/
├── avl/
└── redblack/

src/test/kotlin/io/vloikov/searchtrees/
├── bst/
├── avl/
└── redblack/

config/detekt/          Detekt configuration
docs/                   Architecture documentation
.github/workflows/      Continuous integration
```

The public contract, package layout, class diagrams, operation semantics, and encapsulation decisions are described in
[the architecture document](docs/architecture.md).

## Roadmap

- [x] Define the common architecture and public API.
- [x] Implement and test `BinarySearchTree`.
- [x] Implement, balance, and test `AvlTree`.
- [x] Implement, balance, and test `RedBlackTree`.
- [ ] Add shared contract and cross-implementation tests.
- [ ] Review duplicated logic and stabilize the architecture and public API.
- [ ] Complete the project specification and release `1.0.0`.

## License

This project is available under the [MIT License](LICENSE).
