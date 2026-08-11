package io.vloikov.searchtrees.bst

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BinarySearchTreeTest {
	@Test
	fun newTreeIsEmpty() {
		val tree = BinarySearchTree<Int, String>()

		assertTrue(tree.isEmpty())
		assertEquals(0, tree.size)
		assertFalse(10 in tree)
		assertNull(tree.find(10))
	}

	@Test
	fun insertStoresRootAndUpdatesTreeState() {
		val tree = BinarySearchTree<Int, String>()

		val previousValue = tree.insert(10, "root")

		assertNull(previousValue)
		assertFalse(tree.isEmpty())
		assertEquals(1, tree.size)
		assertTrue(10 in tree)
		assertEquals(10, tree.find(10)?.key)
		assertEquals("root", tree.find(10)?.value)
	}

	@Test
	fun insertStoresKeysOnBothSidesOfRoot() {
		val tree = BinarySearchTree<Int, String>()

		tree.insert(10, "root")
		tree.insert(5, "left")
		tree.insert(15, "right")

		assertEquals(3, tree.size)
		assertEquals("left", tree.find(5)?.value)
		assertEquals("right", tree.find(15)?.value)
		assertNull(tree.find(20))
	}

	@Test
	fun insertReplacesValueForExistingKey() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "old")

		val previousValue = tree.insert(10, "new")

		assertEquals("old", previousValue)
		assertEquals("new", tree.find(10)?.value)
		assertEquals(1, tree.size)
	}

	@Test
	fun minAndMaxReturnNullForEmptyTree() {
		val tree = BinarySearchTree<Int, String>()

		assertNull(tree.min())
		assertNull(tree.max())
	}

	@Test
	fun minAndMaxReturnOnlyNode() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")

		assertEquals(10, tree.min()?.key)
		assertEquals("root", tree.min()?.value)
		assertEquals(10, tree.max()?.key)
		assertEquals("root", tree.max()?.value)
	}

	@Test
	fun minReturnsRootWhenLeftSubtreeIsEmpty() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(15, "right")

		assertEquals(10, tree.min()?.key)
		assertEquals("root", tree.min()?.value)
	}

	@Test
	fun maxReturnsRootWhenRightSubtreeIsEmpty() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(5, "left")

		assertEquals(10, tree.max()?.key)
		assertEquals("root", tree.max()?.value)
	}

	@Test
	fun minAndMaxReturnDeepestExtremeNodes() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(5, "left")
		tree.insert(15, "right")
		tree.insert(2, "minimum")
		tree.insert(7, "left-middle")
		tree.insert(12, "right-middle")
		tree.insert(20, "maximum")

		assertEquals(2, tree.min()?.key)
		assertEquals("minimum", tree.min()?.value)
		assertEquals(20, tree.max()?.key)
		assertEquals("maximum", tree.max()?.value)
	}

	@Test
	fun removeMissingKeyDoesNotChangeTree() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(5, "left")

		val removedValue = tree.remove(99)

		assertNull(removedValue)
		assertEquals(2, tree.size)
		assertTrue(10 in tree)
		assertTrue(5 in tree)
	}

	@Test
	fun removeOnlyNodeMakesTreeEmpty() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")

		val removedValue = tree.remove(10)

		assertEquals("root", removedValue)
		assertTrue(tree.isEmpty())
		assertEquals(0, tree.size)
		assertFalse(10 in tree)
		assertNull(tree.min())
		assertNull(tree.max())
	}

	@Test
	fun removeLeafKeepsOtherNodes() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(5, "left")
		tree.insert(15, "leaf")

		val removedValue = tree.remove(15)

		assertEquals("leaf", removedValue)
		assertEquals(2, tree.size)
		assertFalse(15 in tree)
		assertTrue(10 in tree)
		assertTrue(5 in tree)
	}

	@Test
	fun removeNodeWithLeftChildPromotesChild() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(5, "removed")
		tree.insert(2, "child")

		val removedValue = tree.remove(5)

		assertEquals("removed", removedValue)
		assertEquals(2, tree.size)
		assertFalse(5 in tree)
		assertEquals("child", tree.find(2)?.value)
		assertTrue(10 in tree)
	}

	@Test
	fun removeNodeWithRightChildPromotesChild() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")
		tree.insert(5, "removed")
		tree.insert(7, "child")

		val removedValue = tree.remove(5)

		assertEquals("removed", removedValue)
		assertEquals(2, tree.size)
		assertFalse(5 in tree)
		assertEquals("child", tree.find(7)?.value)
		assertTrue(10 in tree)
	}

	@Test
	fun removeRootWithTwoChildrenUsesDirectSuccessor() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "removed")
		tree.insert(5, "left")
		tree.insert(15, "successor")
		tree.insert(20, "right")

		val removedValue = tree.remove(10)

		assertEquals("removed", removedValue)
		assertEquals(3, tree.size)
		assertFalse(10 in tree)
		assertTrue(5 in tree)
		assertTrue(15 in tree)
		assertTrue(20 in tree)
	}

	@Test
	fun removeNodeWithTwoChildrenPreservesSuccessorRightChild() {
		val tree = BinarySearchTree<Int, String>()
		listOf(10, 5, 20, 2, 8, 6, 7, 9).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(5)

		assertEquals("5", removedValue)
		assertEquals(7, tree.size)
		assertFalse(5 in tree)
		assertTrue(listOf(2, 6, 7, 8, 9, 10, 20).all { key -> key in tree })
		assertEquals(2, tree.min()?.key)
		assertEquals(20, tree.max()?.key)
	}

	@Test
	fun iteratorIsEmptyForEmptyTree() {
		val tree = BinarySearchTree<Int, String>()

		assertTrue(tree.toList().isEmpty())
	}

	@Test
	fun iteratorReturnsOnlyNode() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "root")

		val nodes = tree.toList()

		assertEquals(listOf(10), nodes.map { node -> node.key })
		assertEquals(listOf("root"), nodes.map { node -> node.value })
	}

	@Test
	fun iteratorReturnsNodesInAscendingKeyOrder() {
		val tree = BinarySearchTree<Int, String>()
		listOf(10, 5, 15, 2, 7, 12, 20).forEach { key ->
			tree.insert(key, key.toString())
		}

		val nodes = tree.toList()

		assertEquals(listOf(2, 5, 7, 10, 12, 15, 20), nodes.map { node -> node.key })
		assertEquals(listOf("2", "5", "7", "10", "12", "15", "20"), nodes.map { node -> node.value })
	}

	@Test
	fun iteratorHandlesLeftSkewedTree() {
		val tree = BinarySearchTree<Int, String>()
		listOf(5, 4, 3, 2, 1).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertEquals(listOf(1, 2, 3, 4, 5), tree.map { node -> node.key })
	}

	@Test
	fun iteratorHandlesRightSkewedTree() {
		val tree = BinarySearchTree<Int, String>()
		listOf(1, 2, 3, 4, 5).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertEquals(listOf(1, 2, 3, 4, 5), tree.map { node -> node.key })
	}

	@Test
	fun iteratorWorksAfterRemoval() {
		val tree = BinarySearchTree<Int, String>()
		listOf(10, 5, 20, 2, 8, 6, 7, 9).forEach { key ->
			tree.insert(key, key.toString())
		}
		tree.remove(5)

		assertEquals(listOf(2, 6, 7, 8, 9, 10, 20), tree.map { node -> node.key })
	}

	@Test
	fun keysAndValuesFollowAscendingKeyOrder() {
		val tree = BinarySearchTree<Int, String>()
		tree.insert(10, "ten")
		tree.insert(5, "five")
		tree.insert(15, "fifteen")

		assertEquals(listOf(5, 10, 15), tree.keys().toList())
		assertEquals(listOf("five", "ten", "fifteen"), tree.values().toList())
	}

	@Test
	fun keysAndValuesAreEmptyForEmptyTree() {
		val tree = BinarySearchTree<Int, String>()

		assertTrue(tree.keys().toList().isEmpty())
		assertTrue(tree.values().toList().isEmpty())
	}
}
