[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)

# Search Trees

Search Trees is a Kotlin library that provides a common API for three binary search tree implementations:

- a binary search tree;
- an AVL tree;
- a red-black tree.

Each tree stores unique key-value pairs. Keys implement `Comparable`, while values may be of any type.

## Project status

The project is currently at the architecture stage. Public contracts, implementation class skeletons, and internal node
models are defined, but the tree algorithms are intentionally left for the next stage.

## Planned operations

Every tree supports the following common operations:

- search for a node by key;
- insert a new key-value pair or replace the value associated with an existing key;
- remove a key-value pair;
- check whether a key exists;
- obtain the minimum and maximum nodes;
- check whether the tree is empty;
- iterate over nodes, keys, and values in ascending key order.

The complete API contract, class diagram, package layout, and design decisions are documented in
[Architecture](docs/architecture.md).



## License

This project is distributed under the [MIT License](LICENSE).
