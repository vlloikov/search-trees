package io.vloikov.searchtrees.bst

import io.vloikov.searchtrees.SearchTree
import io.vloikov.searchtrees.TreeNode

/**
 * An unbalanced binary search tree.
 *
 * Keys are ordered using their natural order. Inserting a key that already exists replaces its associated value.
 */
public class BinarySearchTree<K : Comparable<K>, V> : SearchTree<K, V> {
	private var root: BinarySearchNode<K, V>? = null

	override var size: Int = 0
		private set

	override fun isEmpty(): Boolean = size == 0

	override fun contains(key: K): Boolean = findNode(key) != null

	override fun find(key: K): TreeNode<K, V>? = findNode(key)

	override fun min(): TreeNode<K, V>? {
		var current = root ?: return null

		while (true) {
			current = current.left ?: return current
		}
	}

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
		val rootNode = root

		if (rootNode == null) {
			root = BinarySearchNode(key, value)
			size++
			return null
		}

		var current: BinarySearchNode<K, V> = rootNode

		while (true) {
			val comparison = key.compareTo(current.key)

			if (comparison == 0) {
				val previousValue = current.value
				current.value = value
				return previousValue
			}

			val next = if (comparison < 0) current.left else current.right

			if (next == null) {
				val newNode = BinarySearchNode(key, value)

				if (comparison < 0) {
					current.left = newNode
				} else {
					current.right = newNode
				}

				size++
				return null
			}

			current = next
		}
	}

	override fun remove(key: K): V? = TODO("Implemented during stage 2")

	override fun keys(): Sequence<K> = TODO("Implemented during stage 2")

	override fun values(): Sequence<V> = TODO("Implemented during stage 2")

	override fun iterator(): Iterator<TreeNode<K, V>> = TODO("Implemented during stage 2")

	private fun findNode(key: K): BinarySearchNode<K, V>? {
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
}
