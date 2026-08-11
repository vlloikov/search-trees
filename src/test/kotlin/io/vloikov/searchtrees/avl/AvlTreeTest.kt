package io.vloikov.searchtrees.avl

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AvlTreeTest {
	@Test
	fun newTreeIsEmpty() {
		val tree = AvlTree<Int, String>()

		assertTrue(tree.isEmpty())
		assertEquals(0, tree.size)
		assertFalse(10 in tree)
		assertNull(tree.find(10))
	}

	@Test
	fun insertStoresRootAndUpdatesTreeState() {
		val tree = AvlTree<Int, String>()

		val previousValue = tree.insert(10, "root")

		assertNull(previousValue)
		assertFalse(tree.isEmpty())
		assertEquals(1, tree.size)
		assertTrue(10 in tree)
		assertEquals(10, tree.find(10)?.key)
		assertEquals("root", tree.find(10)?.value)
	}

	@Test
	fun insertReplacesValueForExistingKey() {
		val tree = AvlTree<Int, String>()
		tree.insert(10, "old")

		val previousValue = tree.insert(10, "new")

		assertEquals("old", previousValue)
		assertEquals("new", tree.find(10)?.value)
		assertEquals(1, tree.size)
	}

	@Test
	fun insertBalancesLeftLeftCase() {
		val tree = AvlTree<Int, String>()
		listOf(30, 20, 10).forEach { key -> tree.insert(key, key.toString()) }

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertBalancesRightRightCase() {
		val tree = AvlTree<Int, String>()
		listOf(10, 20, 30).forEach { key -> tree.insert(key, key.toString()) }

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertBalancesLeftRightCase() {
		val tree = AvlTree<Int, String>()
		listOf(30, 10, 20).forEach { key -> tree.insert(key, key.toString()) }

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertBalancesRightLeftCase() {
		val tree = AvlTree<Int, String>()
		listOf(10, 30, 20).forEach { key -> tree.insert(key, key.toString()) }

		assertBalancedThreeNodeTree(tree)
	}

	@Test
	fun insertMaintainsAvlInvariantForAscendingKeys() {
		val tree = AvlTree<Int, String>()

		(1..100).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertEquals(100, tree.size)
		assertTrue((1..100).all { key -> key in tree })
		assertAvlInvariant(tree.root)
	}

	@Test
	fun minAndMaxReturnNullForEmptyTree() {
		val tree = AvlTree<Int, String>()

		assertNull(tree.min())
		assertNull(tree.max())
	}

	@Test
	fun minAndMaxReturnOnlyNode() {
		val tree = AvlTree<Int, String>()
		tree.insert(10, "root")

		assertEquals(10, tree.min()?.key)
		assertEquals("root", tree.min()?.value)
		assertEquals(10, tree.max()?.key)
		assertEquals("root", tree.max()?.value)
	}

	@Test
	fun minReturnsRootWhenLeftSubtreeIsEmpty() {
		val tree = AvlTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(15, "right")

		assertEquals(10, tree.min()?.key)
		assertEquals("root", tree.min()?.value)
	}

	@Test
	fun maxReturnsRootWhenRightSubtreeIsEmpty() {
		val tree = AvlTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(5, "left")

		assertEquals(10, tree.max()?.key)
		assertEquals("root", tree.max()?.value)
	}

	@Test
	fun minAndMaxRemainCorrectAfterRotations() {
		val tree = AvlTree<Int, String>()

		(1..100).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertEquals(1, tree.min()?.key)
		assertEquals("1", tree.min()?.value)
		assertEquals(100, tree.max()?.key)
		assertEquals("100", tree.max()?.value)
	}

	private fun assertBalancedThreeNodeTree(tree: AvlTree<Int, String>) {
		val root = assertNotNull(tree.root)

		assertEquals(3, tree.size)
		assertEquals(20, root.key)
		assertEquals(10, root.left?.key)
		assertEquals(30, root.right?.key)
		assertEquals(2, root.height)
		assertEquals(1, root.left?.height)
		assertEquals(1, root.right?.height)
	}

	private fun assertAvlInvariant(node: AvlNode<Int, String>?): Int {
		if (node == null) {
			return 0
		}

		val leftHeight = assertAvlInvariant(node.left)
		val rightHeight = assertAvlInvariant(node.right)
		val expectedHeight = maxOf(leftHeight, rightHeight) + 1

		assertTrue(abs(leftHeight - rightHeight) <= 1)
		assertEquals(expectedHeight, node.height)

		return expectedHeight
	}
}
