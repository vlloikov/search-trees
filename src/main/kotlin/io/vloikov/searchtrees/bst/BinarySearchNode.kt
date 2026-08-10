package io.vloikov.searchtrees.bst

import io.vloikov.searchtrees.TreeNode

/** Internal node representation used by [BinarySearchTree]. */
internal class BinarySearchNode<K : Comparable<K>, V>(
	override val key: K,
	override var value: V,
) : TreeNode<K, V> {
	internal var left: BinarySearchNode<K, V>? = null
	internal var right: BinarySearchNode<K, V>? = null
}
