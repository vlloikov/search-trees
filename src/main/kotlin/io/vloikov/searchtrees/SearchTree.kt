package io.vloikov.searchtrees

/**
 * A search tree containing unique key-value pairs.
 *
 * Keys are ordered using [Comparable.compareTo].
 * Two keys identify the same position when their comparison returns zero.
 * Iteration is performed in ascending key order.
 *
 * @param K the comparable key type
 * @param V the stored value type
 */
public interface SearchTree<K : Comparable<K>, V> : Iterable<TreeNode<K, V>> {
	/** The number of nodes currently stored in this tree. */
	public val size: Int

	/** Returns `true` when this tree contains no nodes. */
	public fun isEmpty(): Boolean

	/** Returns `true` when this tree contains a node whose key compares as equal to [key]. */
	public operator fun contains(key: K): Boolean

	/** Returns the node matching [key], or `null` when no matching node exists. */
	public fun find(key: K): TreeNode<K, V>?

	/** Returns the node with the minimum key, or `null` when this tree is empty. */
	public fun min(): TreeNode<K, V>?

	/** Returns the node with the maximum key, or `null` when this tree is empty. */
	public fun max(): TreeNode<K, V>?

	/**
	 * Associates [value] with [key].
	 *
	 * A new key creates a node. A key that compares as equal to an existing key replaces that node's value.
	 *
	 * @return the previous value, or `null` when a new node was inserted
	 */
	public fun insert(
		key: K,
		value: V,
	): V?

	/**
	 * Removes the node matching [key].
	 *
	 * @return the removed value, or `null` when no matching node existed
	 */
	public fun remove(key: K): V?

	/** Returns a lazy sequence of keys in ascending order. */
	public fun keys(): Sequence<K>

	/** Returns a lazy sequence of values ordered by their corresponding keys. */
	public fun values(): Sequence<V>

	/** Returns an in-order iterator over the nodes in this tree. */
	override fun iterator(): Iterator<TreeNode<K, V>>
}
