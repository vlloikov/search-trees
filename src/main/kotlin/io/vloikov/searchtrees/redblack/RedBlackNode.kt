package io.vloikov.searchtrees.redblack

import io.vloikov.searchtrees.TreeNode

/** Internal node representation used by [RedBlackTree]. */
internal class RedBlackNode<K : Comparable<K>, V>(
	override val key: K,
	override var value: V,
) : TreeNode<K, V> {
	internal var left: RedBlackNode<K, V>? = null
	internal var right: RedBlackNode<K, V>? = null
	internal var parent: RedBlackNode<K, V>? = null
	internal var color: Color = Color.RED

	/** The two valid red-black node colors. */
	internal enum class Color {
		RED,
		BLACK,
	}
}
