package io.vloikov.searchtrees.avl

import io.vloikov.searchtrees.TreeNode

/** Internal node representation used by [AvlTree]. */
internal class AvlNode<K : Comparable<K>, V>(
	override val key: K,
	override var value: V,
) : TreeNode<K, V> {
	internal var left: AvlNode<K, V>? = null
	internal var right: AvlNode<K, V>? = null
	internal var height: Int = 1
}
