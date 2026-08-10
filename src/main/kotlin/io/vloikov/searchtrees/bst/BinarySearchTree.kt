package io.vloikov.searchtrees.bst

import io.vloikov.searchtrees.SearchTree
import io.vloikov.searchtrees.TreeNode

/**
 * An unbalanced binary search tree.
 *
 * The algorithms are introduced during the implementation stage. This class currently defines the agreed public
 * contract only.
 */
public class BinarySearchTree<K : Comparable<K>, V> : SearchTree<K, V> {
	override val size: Int
		get() = TODO("Implemented during stage 2")

	override fun isEmpty(): Boolean = TODO("Implemented during stage 2")

	override fun contains(key: K): Boolean = TODO("Implemented during stage 2")

	override fun find(key: K): TreeNode<K, V>? = TODO("Implemented during stage 2")

	override fun min(): TreeNode<K, V>? = TODO("Implemented during stage 2")

	override fun max(): TreeNode<K, V>? = TODO("Implemented during stage 2")

	override fun insert(
		key: K,
		value: V,
	): V? = TODO("Implemented during stage 2")

	override fun remove(key: K): V? = TODO("Implemented during stage 2")

	override fun keys(): Sequence<K> = TODO("Implemented during stage 2")

	override fun values(): Sequence<V> = TODO("Implemented during stage 2")

	override fun iterator(): Iterator<TreeNode<K, V>> = TODO("Implemented during stage 2")
}
