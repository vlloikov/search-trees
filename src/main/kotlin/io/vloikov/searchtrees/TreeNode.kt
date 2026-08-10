package io.vloikov.searchtrees

/**
 * A read-only view of a node stored in a search tree.
 *
 * Structural and balancing metadata is intentionally hidden from library users.
 *
 * @param K the key type
 * @param V the value type
 */
public interface TreeNode<out K, out V> {
	/** The key that determines this node's position in its tree. */
	public val key: K

	/** The value associated with [key]. */
	public val value: V
}
