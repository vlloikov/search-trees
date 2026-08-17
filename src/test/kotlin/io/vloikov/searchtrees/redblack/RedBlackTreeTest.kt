package io.vloikov.searchtrees.redblack

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RedBlackTreeTest {
	@Test
	fun newTreeIsEmpty() {
		val tree = RedBlackTree<Int, String>()

		assertTrue(tree.isEmpty())
		assertEquals(0, tree.size)
		assertNull(tree.root)
		assertFalse(10 in tree)
		assertNull(tree.find(10))
	}

	@Test
	fun minAndMaxAreNullForEmptyTree() {
		val tree = RedBlackTree<Int, String>()

		assertNull(tree.min())
		assertNull(tree.max())
	}

	@Test
	fun insertStoresBlackRootAndUpdatesTreeState() {
		val tree = RedBlackTree<Int, String>()

		val previousValue = tree.insert(10, "root")

		assertNull(previousValue)
		assertFalse(tree.isEmpty())
		assertEquals(1, tree.size)
		assertEquals(10, tree.root?.key)
		assertEquals("root", tree.root?.value)
		assertEquals(RedBlackNode.Color.BLACK, tree.root?.color)
		assertNull(tree.root?.parent)
		assertTrue(10 in tree)
	}

	@Test
	fun insertReplacesValueForExistingKey() {
		val tree = RedBlackTree<Int, String>()
		tree.insert(10, "old")

		val previousValue = tree.insert(10, "new")

		assertEquals("old", previousValue)
		assertEquals("new", tree.find(10)?.value)
		assertEquals(1, tree.size)
		assertRedBlackInvariant(tree)
	}

	@Test
	fun insertMaintainsParentLinks() {
		val tree = RedBlackTree<Int, String>()
		listOf(10, 5, 15).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertSame(tree.root, tree.root?.left?.parent)
		assertSame(tree.root, tree.root?.right?.parent)
		assertRedBlackInvariant(tree)
	}

	@Test
	fun insertBalancesLeftLeftCase() {
		val tree = RedBlackTree<Int, String>()
		listOf(30, 20, 10).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertBalancesRightRightCase() {
		val tree = RedBlackTree<Int, String>()
		listOf(10, 20, 30).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertBalancesLeftRightCase() {
		val tree = RedBlackTree<Int, String>()
		listOf(30, 10, 20).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertBalancesRightLeftCase() {
		val tree = RedBlackTree<Int, String>()
		listOf(10, 30, 20).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertRecolorsParentAndUncle() {
		val tree = RedBlackTree<Int, String>()
		listOf(10, 5, 15, 1).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertEquals(RedBlackNode.Color.BLACK, tree.root?.color)
		assertEquals(RedBlackNode.Color.BLACK, tree.root?.left?.color)
		assertEquals(RedBlackNode.Color.BLACK, tree.root?.right?.color)
		assertEquals(RedBlackNode.Color.RED, tree.root?.left?.left?.color)
		assertRedBlackInvariant(tree)
	}

	@Test
	fun findMinAndMaxWorkAfterBalancedInsertion() {
		val tree = RedBlackTree<Int, String>()
		listOf(20, 10, 30, 5, 15, 25, 40).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertEquals("15", tree.find(15)?.value)
		assertNull(tree.find(99))
		assertTrue(25 in tree)
		assertFalse(99 in tree)
		assertEquals(5, tree.min()?.key)
		assertEquals(40, tree.max()?.key)
		assertRedBlackInvariant(tree)
	}

	@Test
	fun insertionPreservesRedBlackInvariantsForLargerTree() {
		val tree = RedBlackTree<Int, String>()
		val keys = listOf(41, 38, 31, 12, 19, 8, 50, 60, 55, 1, 7, 6)

		keys.forEach { key ->
			tree.insert(key, key.toString())
			assertRedBlackInvariant(tree)
		}

		assertEquals(keys.size, tree.size)
		assertTrue(keys.all { key -> key in tree })
	}

	private fun assertBalancedThreeNodeTree(tree: RedBlackTree<Int, String>) {
		assertEquals(20, tree.root?.key)
		assertEquals(10, tree.root?.left?.key)
		assertEquals(30, tree.root?.right?.key)
		assertEquals(RedBlackNode.Color.BLACK, tree.root?.color)
		assertEquals(RedBlackNode.Color.RED, tree.root?.left?.color)
		assertEquals(RedBlackNode.Color.RED, tree.root?.right?.color)
		assertRedBlackInvariant(tree)
	}

	private fun assertRedBlackInvariant(tree: RedBlackTree<Int, String>) {
		val root = tree.root ?: return

		assertEquals(RedBlackNode.Color.BLACK, root.color)
		assertNull(root.parent)
		assertOrderingAndParentLinks(root, null, null)
		blackHeight(root)
	}

	private fun assertOrderingAndParentLinks(
		node: RedBlackNode<Int, String>?,
		minimumKey: Int?,
		maximumKey: Int?,
	) {
		node ?: return

		if (minimumKey != null) {
			assertTrue(node.key > minimumKey)
		}
		if (maximumKey != null) {
			assertTrue(node.key < maximumKey)
		}

		node.left?.let { child -> assertSame(node, child.parent) }
		node.right?.let { child -> assertSame(node, child.parent) }

		assertOrderingAndParentLinks(node.left, minimumKey, node.key)
		assertOrderingAndParentLinks(node.right, node.key, maximumKey)
	}

	private fun blackHeight(node: RedBlackNode<Int, String>?): Int {
		if (node == null) {
			return 1
		}

		if (node.color == RedBlackNode.Color.RED) {
			assertEquals(RedBlackNode.Color.BLACK, node.left?.color ?: RedBlackNode.Color.BLACK)
			assertEquals(RedBlackNode.Color.BLACK, node.right?.color ?: RedBlackNode.Color.BLACK)
		}

		val leftBlackHeight = blackHeight(node.left)
		val rightBlackHeight = blackHeight(node.right)

		assertEquals(leftBlackHeight, rightBlackHeight)

		return leftBlackHeight + if (node.color == RedBlackNode.Color.BLACK) 1 else 0
	}
}
