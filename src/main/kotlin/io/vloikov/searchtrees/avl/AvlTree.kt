package io.vloikov.searchtrees.avl

import io.vloikov.searchtrees.SearchTree
import io.vloikov.searchtrees.TreeNode

/**
 * A height-balanced AVL search tree.
 *
 * Keys are ordered using their natural order. Inserting a key that already exists replaces its associated value.
 */
public class AvlTree<K : Comparable<K>, V> : SearchTree<K, V> {
	internal var root: AvlNode<K, V>? = null
		private set

	override var size: Int = 0
		private set

	override fun isEmpty(): Boolean = size == 0

	override fun contains(key: K): Boolean = findNode(key) != null

	override fun find(key: K): TreeNode<K, V>? = findNode(key)

	override fun min(): TreeNode<K, V>? = root?.let { node -> minimumNode(node) }

	override fun max(): TreeNode<K, V>? {
		var current = root ?: return null

		while (true) {
			current = current.right ?: return current
		}
	}

	override fun insert(
		key: K,
		value: V,
	): V? {
		val existingNode = findNode(key)

		if (existingNode != null) {
			val previousValue = existingNode.value
			existingNode.value = value
			return previousValue
		}

		root = insertNode(root, key, value)
		size++

		return null
	}

	override fun remove(key: K): V? {
		val removedNode = findNode(key) ?: return null
		val removedValue = removedNode.value

		root = removeNode(root, key)

		removedNode.left = null
		removedNode.right = null
		size--

		return removedValue
	}

	override fun keys(): Sequence<K> = asSequence().map { node -> node.key }

	override fun values(): Sequence<V> = asSequence().map { node -> node.value }

	override fun iterator(): Iterator<TreeNode<K, V>> =
		sequence<TreeNode<K, V>> {
			val stack = ArrayDeque<AvlNode<K, V>>()
			var current = root

			while (current != null || stack.isNotEmpty()) {
				while (current != null) {
					stack.addLast(current)
					current = current.left
				}

				val node = stack.removeLast()
				yield(node)
				current = node.right
			}
		}.iterator()

	private fun insertNode(
		node: AvlNode<K, V>?,
		key: K,
		value: V,
	): AvlNode<K, V> {
		if (node == null) {
			return AvlNode(key, value)
		}

		if (key.compareTo(node.key) < 0) {
			node.left = insertNode(node.left, key, value)
		} else {
			node.right = insertNode(node.right, key, value)
		}

		return balance(node)
	}

	private fun removeNode(
		node: AvlNode<K, V>?,
		key: K,
	): AvlNode<K, V>? {
		node ?: return null

		val comparison = key.compareTo(node.key)

		return when {
			comparison < 0 -> {
				node.left = removeNode(node.left, key)
				balance(node)
			}

			comparison > 0 -> {
				node.right = removeNode(node.right, key)
				balance(node)
			}

			else -> {
				val leftChild = node.left
				val rightChild = node.right

				if (leftChild == null || rightChild == null) {
					leftChild ?: rightChild
				} else {
					val successor = minimumNode(rightChild)

					successor.right = removeMinimum(rightChild)
					successor.left = leftChild

					balance(successor)
				}
			}
		}
	}

	private fun minimumNode(node: AvlNode<K, V>): AvlNode<K, V> {
		var current = node

		while (true) {
			current = current.left ?: return current
		}
	}

	private fun removeMinimum(node: AvlNode<K, V>): AvlNode<K, V>? {
		val leftChild = node.left ?: return node.right

		node.left = removeMinimum(leftChild)

		return balance(node)
	}

	private fun findNode(key: K): AvlNode<K, V>? {
		var current = root

		while (current != null) {
			val comparison = key.compareTo(current.key)

			current =
				when {
					comparison < 0 -> current.left
					comparison > 0 -> current.right
					else -> return current
				}
		}

		return null
	}

	private fun height(node: AvlNode<K, V>?): Int = node?.height ?: 0

	private fun updateHeight(node: AvlNode<K, V>) {
		node.height = maxOf(height(node.left), height(node.right)) + 1
	}

	private fun balanceFactor(node: AvlNode<K, V>): Int = height(node.left) - height(node.right)

	private fun rotateRight(node: AvlNode<K, V>): AvlNode<K, V> {
		val newRoot = checkNotNull(node.left)

		node.left = newRoot.right
		newRoot.right = node

		updateHeight(node)
		updateHeight(newRoot)

		return newRoot
	}

	private fun rotateLeft(node: AvlNode<K, V>): AvlNode<K, V> {
		val newRoot = checkNotNull(node.right)

		node.right = newRoot.left
		newRoot.left = node

		updateHeight(node)
		updateHeight(newRoot)

		return newRoot
	}

	private fun balance(node: AvlNode<K, V>): AvlNode<K, V> {
		updateHeight(node)
		val factor = balanceFactor(node)

		return when {
			factor > 1 -> {
				val leftChild = checkNotNull(node.left)

				if (balanceFactor(leftChild) < 0) {
					node.left = rotateLeft(leftChild)
				}

				rotateRight(node)
			}

			factor < -1 -> {
				val rightChild = checkNotNull(node.right)

				if (balanceFactor(rightChild) > 0) {
					node.right = rotateRight(rightChild)
				}

				rotateLeft(node)
			}

			else -> node
		}
	}
}
