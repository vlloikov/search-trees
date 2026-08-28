package io.vloikov.searchtrees.redblack

import io.vloikov.searchtrees.SearchTree
import io.vloikov.searchtrees.TreeNode

/**
 * A self-balancing red-black search tree.
 *
 * Keys are ordered using their natural order. Inserting a key that already exists replaces its associated value.
 */
public class RedBlackTree<K : Comparable<K>, V> : SearchTree<K, V> {
	internal var root: RedBlackNode<K, V>? = null
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
		var parent: RedBlackNode<K, V>? = null
		var current = root

		while (current != null) {
			parent = current

			val comparison = key.compareTo(current.key)

			when {
				comparison < 0 -> current = current.left
				comparison > 0 -> current = current.right
				else -> {
					val previousValue = current.value
					current.value = value
					return previousValue
				}
			}
		}

		val newNode = RedBlackNode(key, value)
		newNode.parent = parent

		when {
			parent == null -> root = newNode
			key.compareTo(parent.key) < 0 -> parent.left = newNode
			else -> parent.right = newNode
		}

		balanceAfterInsert(newNode)
		size++

		return null
	}

	override fun remove(key: K): V? {
		val removedNode = findNode(key) ?: return null
		val removedValue = removedNode.value
		val context = deleteNode(removedNode)

		if (context.removedColor == RedBlackNode.Color.BLACK) {
			balanceAfterRemove(context.replacement, context.replacementParent)
		}

		removedNode.left = null
		removedNode.right = null
		removedNode.parent = null
		size--

		return removedValue
	}

	override fun keys(): Sequence<K> = TODO("Implemented during stage 2")

	override fun values(): Sequence<V> = TODO("Implemented during stage 2")

	override fun iterator(): Iterator<TreeNode<K, V>> = TODO("Implemented during stage 2")

	private fun findNode(key: K): RedBlackNode<K, V>? {
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

	private fun minimumNode(node: RedBlackNode<K, V>): RedBlackNode<K, V> {
		var current = node

		while (true) {
			current = current.left ?: return current
		}
	}

	private fun balanceAfterInsert(node: RedBlackNode<K, V>) {
		var current = node

		while (current.parent?.color == RedBlackNode.Color.RED) {
			val parent = checkNotNull(current.parent)
			val grandparent = checkNotNull(parent.parent)

			current =
				if (parent === grandparent.left) {
					balanceLeftBranch(current, parent, grandparent)
				} else {
					balanceRightBranch(current, parent, grandparent)
				}
		}

		root?.color = RedBlackNode.Color.BLACK
	}

	private fun balanceLeftBranch(
		node: RedBlackNode<K, V>,
		parent: RedBlackNode<K, V>,
		grandparent: RedBlackNode<K, V>,
	): RedBlackNode<K, V> {
		val uncle = grandparent.right

		if (uncle?.color == RedBlackNode.Color.RED) {
			parent.color = RedBlackNode.Color.BLACK
			uncle.color = RedBlackNode.Color.BLACK
			grandparent.color = RedBlackNode.Color.RED
			return grandparent
		}

		var current = node

		if (current === parent.right) {
			current = parent
			rotateLeft(current)
		}

		val balancedParent = checkNotNull(current.parent)
		val balancedGrandparent = checkNotNull(balancedParent.parent)

		balancedParent.color = RedBlackNode.Color.BLACK
		balancedGrandparent.color = RedBlackNode.Color.RED
		rotateRight(balancedGrandparent)

		return current
	}

	private fun balanceRightBranch(
		node: RedBlackNode<K, V>,
		parent: RedBlackNode<K, V>,
		grandparent: RedBlackNode<K, V>,
	): RedBlackNode<K, V> {
		val uncle = grandparent.left

		if (uncle?.color == RedBlackNode.Color.RED) {
			parent.color = RedBlackNode.Color.BLACK
			uncle.color = RedBlackNode.Color.BLACK
			grandparent.color = RedBlackNode.Color.RED
			return grandparent
		}

		var current = node

		if (current === parent.left) {
			current = parent
			rotateRight(current)
		}

		val balancedParent = checkNotNull(current.parent)
		val balancedGrandparent = checkNotNull(balancedParent.parent)

		balancedParent.color = RedBlackNode.Color.BLACK
		balancedGrandparent.color = RedBlackNode.Color.RED
		rotateLeft(balancedGrandparent)

		return current
	}

	private fun deleteNode(node: RedBlackNode<K, V>): RemovalContext<K, V> =
		when {
			node.left == null -> replaceNodeWithChild(node, node.right)
			node.right == null -> replaceNodeWithChild(node, node.left)
			else -> replaceNodeWithSuccessor(node)
		}

	private fun replaceNodeWithChild(
		node: RedBlackNode<K, V>,
		child: RedBlackNode<K, V>?,
	): RemovalContext<K, V> {
		val parent = node.parent
		val removedColor = node.color

		transplant(node, child)

		return RemovalContext(child, parent, removedColor)
	}

	private fun replaceNodeWithSuccessor(node: RedBlackNode<K, V>): RemovalContext<K, V> {
		val successor = minimumNode(checkNotNull(node.right))
		val removedColor = successor.color
		val replacement = successor.right
		val replacementParent: RedBlackNode<K, V>?

		if (successor.parent === node) {
			replacementParent = successor
			replacement?.parent = successor
		} else {
			replacementParent = successor.parent
			transplant(successor, replacement)
			successor.right = node.right
			successor.right?.parent = successor
		}

		transplant(node, successor)
		successor.left = node.left
		successor.left?.parent = successor
		successor.color = node.color

		return RemovalContext(replacement, replacementParent, removedColor)
	}

	private fun transplant(
		node: RedBlackNode<K, V>,
		replacement: RedBlackNode<K, V>?,
	) {
		val parent = node.parent

		when {
			parent == null -> root = replacement
			node === parent.left -> parent.left = replacement
			else -> parent.right = replacement
		}

		replacement?.parent = parent
	}

	private fun balanceAfterRemove(
		node: RedBlackNode<K, V>?,
		nodeParent: RedBlackNode<K, V>?,
	) {
		var current = node
		var parent = current?.parent ?: nodeParent

		while (current !== root && colorOf(current) == RedBlackNode.Color.BLACK && parent != null) {
			val currentParent = checkNotNull(parent)
			val state =
				if (current === currentParent.left) {
					balanceLeftRemoval(currentParent)
				} else {
					balanceRightRemoval(currentParent)
				}

			current = state.first
			parent = state.second
		}

		current?.color = RedBlackNode.Color.BLACK
	}

	private fun balanceLeftRemoval(parent: RedBlackNode<K, V>): Pair<RedBlackNode<K, V>?, RedBlackNode<K, V>?> {
		var sibling = parent.right

		if (colorOf(sibling) == RedBlackNode.Color.RED) {
			sibling?.color = RedBlackNode.Color.BLACK
			parent.color = RedBlackNode.Color.RED
			rotateLeft(parent)
			sibling = parent.right
		}

		return if (
			colorOf(sibling?.left) == RedBlackNode.Color.BLACK &&
			colorOf(sibling?.right) == RedBlackNode.Color.BLACK
		) {
			sibling?.color = RedBlackNode.Color.RED
			parent to parent.parent
		} else {
			if (colorOf(sibling?.right) == RedBlackNode.Color.BLACK) {
				sibling?.left?.color = RedBlackNode.Color.BLACK
				sibling?.color = RedBlackNode.Color.RED
				sibling?.let { node -> rotateRight(node) }
				sibling = parent.right
			}

			sibling?.color = parent.color
			parent.color = RedBlackNode.Color.BLACK
			sibling?.right?.color = RedBlackNode.Color.BLACK
			rotateLeft(parent)
			root to null
		}
	}

	private fun balanceRightRemoval(parent: RedBlackNode<K, V>): Pair<RedBlackNode<K, V>?, RedBlackNode<K, V>?> {
		var sibling = parent.left

		if (colorOf(sibling) == RedBlackNode.Color.RED) {
			sibling?.color = RedBlackNode.Color.BLACK
			parent.color = RedBlackNode.Color.RED
			rotateRight(parent)
			sibling = parent.left
		}

		return if (
			colorOf(sibling?.left) == RedBlackNode.Color.BLACK &&
			colorOf(sibling?.right) == RedBlackNode.Color.BLACK
		) {
			sibling?.color = RedBlackNode.Color.RED
			parent to parent.parent
		} else {
			if (colorOf(sibling?.left) == RedBlackNode.Color.BLACK) {
				sibling?.right?.color = RedBlackNode.Color.BLACK
				sibling?.color = RedBlackNode.Color.RED
				sibling?.let { node -> rotateLeft(node) }
				sibling = parent.left
			}

			sibling?.color = parent.color
			parent.color = RedBlackNode.Color.BLACK
			sibling?.left?.color = RedBlackNode.Color.BLACK
			rotateRight(parent)
			root to null
		}
	}

	private fun colorOf(node: RedBlackNode<K, V>?): RedBlackNode.Color = node?.color ?: RedBlackNode.Color.BLACK

	private fun rotateLeft(node: RedBlackNode<K, V>) {
		val pivot = node.right ?: return

		node.right = pivot.left
		pivot.left?.parent = node

		val parent = node.parent
		pivot.parent = parent

		when {
			parent == null -> root = pivot
			node === parent.left -> parent.left = pivot
			else -> parent.right = pivot
		}

		pivot.left = node
		node.parent = pivot
	}

	private fun rotateRight(node: RedBlackNode<K, V>) {
		val pivot = node.left ?: return

		node.left = pivot.right
		pivot.right?.parent = node

		val parent = node.parent
		pivot.parent = parent

		when {
			parent == null -> root = pivot
			node === parent.left -> parent.left = pivot
			else -> parent.right = pivot
		}

		pivot.right = node
		node.parent = pivot
	}

	private data class RemovalContext<K : Comparable<K>, V>(
		val replacement: RedBlackNode<K, V>?,
		val replacementParent: RedBlackNode<K, V>?,
		val removedColor: RedBlackNode.Color,
	)
}
