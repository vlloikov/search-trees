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

	@Test
	fun removeMissingKeyDoesNotChangeTree() {
		val tree = AvlTree<Int, String>()
		listOf(10, 5, 15).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(99)

		assertNull(removedValue)
		assertEquals(3, tree.size)
		assertEquals(10, tree.root?.key)
		assertTrue(listOf(5, 10, 15).all { key -> key in tree })
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeOnlyNodeMakesTreeEmpty() {
		val tree = AvlTree<Int, String>()
		tree.insert(10, "root")

		val removedValue = tree.remove(10)

		assertEquals("root", removedValue)
		assertTrue(tree.isEmpty())
		assertEquals(0, tree.size)
		assertNull(tree.root)
		assertFalse(10 in tree)
		assertNull(tree.min())
		assertNull(tree.max())
	}

	@Test
	fun removeLeafKeepsOtherNodes() {
		val tree = AvlTree<Int, String>()
		listOf(10, 5, 15).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(15)

		assertEquals("15", removedValue)
		assertEquals(2, tree.size)
		assertFalse(15 in tree)
		assertTrue(5 in tree)
		assertTrue(10 in tree)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeNodeWithLeftChildPromotesChild() {
		val tree = AvlTree<Int, String>()
		listOf(10, 5, 15, 2).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(5)

		assertEquals("5", removedValue)
		assertEquals(3, tree.size)
		assertFalse(5 in tree)
		assertTrue(listOf(2, 10, 15).all { key -> key in tree })
		assertEquals(2, tree.root?.left?.key)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeNodeWithRightChildPromotesChild() {
		val tree = AvlTree<Int, String>()
		listOf(10, 5, 15, 20).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(15)

		assertEquals("15", removedValue)
		assertEquals(3, tree.size)
		assertFalse(15 in tree)
		assertTrue(listOf(5, 10, 20).all { key -> key in tree })
		assertEquals(20, tree.root?.right?.key)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeNodeWithTwoChildrenUsesDirectSuccessor() {
		val tree = AvlTree<Int, String>()
		listOf(20, 10, 30, 40).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(20)

		assertEquals("20", removedValue)
		assertEquals(3, tree.size)
		assertFalse(20 in tree)
		assertEquals(30, tree.root?.key)
		assertEquals(10, tree.root?.left?.key)
		assertEquals(40, tree.root?.right?.key)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeNodeWithTwoChildrenPreservesSuccessorRightChild() {
		val tree = AvlTree<Int, String>()
		listOf(20, 10, 40, 30, 50, 25, 35, 27, 37).forEach { key ->
			tree.insert(key, key.toString())
		}

		assertEquals(30, tree.root?.key)
		assertEquals(35, tree.root?.right?.left?.key)
		assertEquals(37, tree.root?.right?.left?.right?.key)

		val removedValue = tree.remove(30)

		assertEquals("30", removedValue)
		assertEquals(8, tree.size)
		assertFalse(30 in tree)
		assertTrue(
			listOf(10, 20, 25, 27, 35, 37, 40, 50)
				.all { key -> key in tree },
		)
		assertEquals(35, tree.root?.key)
		assertEquals(37, tree.root?.right?.left?.key)
		assertEquals(10, tree.min()?.key)
		assertEquals(50, tree.max()?.key)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeBalancesLeftLeftCase() {
		val tree = AvlTree<Int, String>()
		listOf(4, 2, 5, 1, 3).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(5)

		assertEquals("5", removedValue)
		assertEquals(2, tree.root?.key)
		assertTrue(listOf(1, 2, 3, 4).all { key -> key in tree })
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeBalancesRightRightCase() {
		val tree = AvlTree<Int, String>()
		listOf(2, 1, 4, 3, 5).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(1)

		assertEquals("1", removedValue)
		assertEquals(4, tree.root?.key)
		assertTrue(listOf(2, 3, 4, 5).all { key -> key in tree })
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeBalancesLeftRightCase() {
		val tree = AvlTree<Int, String>()
		listOf(4, 2, 5, 3).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(5)

		assertEquals("5", removedValue)
		assertEquals(3, tree.root?.key)
		assertEquals(2, tree.root?.left?.key)
		assertEquals(4, tree.root?.right?.key)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun removeBalancesRightLeftCase() {
		val tree = AvlTree<Int, String>()
		listOf(2, 1, 4, 3).forEach { key ->
			tree.insert(key, key.toString())
		}

		val removedValue = tree.remove(1)

		assertEquals("1", removedValue)
		assertEquals(3, tree.root?.key)
		assertEquals(2, tree.root?.left?.key)
		assertEquals(4, tree.root?.right?.key)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun iteratorIsEmptyForEmptyTree() {
		val tree = AvlTree<Int, String>()

		assertTrue(tree.toList().isEmpty())
	}

	@Test
	fun iteratorReturnsOnlyNode() {
		val tree = AvlTree<Int, String>()
		tree.insert(10, "root")

		val nodes = tree.toList()

		assertEquals(listOf(10), nodes.map { node -> node.key })
		assertEquals(listOf("root"), nodes.map { node -> node.value })
	}

	@Test
	fun iteratorReturnsNodesInAscendingKeyOrderAfterRotations() {
		val tree = AvlTree<Int, String>()
		listOf(30, 10, 20, 40, 35, 50, 5, 15, 25, 45).forEach { key ->
			tree.insert(key, key.toString())
		}

		val nodes = tree.toList()

		assertEquals(
			listOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50),
			nodes.map { node -> node.key },
		)
		assertEquals(
			listOf("5", "10", "15", "20", "25", "30", "35", "40", "45", "50"),
			nodes.map { node -> node.value },
		)
	}

	@Test
	fun iteratorWorksAfterBalancedRemoval() {
		val tree = AvlTree<Int, String>()
		listOf(4, 2, 5, 1, 3).forEach { key ->
			tree.insert(key, key.toString())
		}
		tree.remove(5)

		assertEquals(
			listOf(1, 2, 3, 4),
			tree.map { node -> node.key },
		)
		assertAvlInvariant(tree.root)
	}

	@Test
	fun iteratorContainsSingleNodeAfterValueReplacement() {
		val tree = AvlTree<Int, String>()
		tree.insert(10, "old")
		tree.insert(10, "new")

		val nodes = tree.toList()

		assertEquals(1, nodes.size)
		assertEquals(10, nodes.single().key)
		assertEquals("new", nodes.single().value)
	}

	@Test
	fun keysAndValuesAreEmptyForEmptyTree() {
		val tree = AvlTree<Int, String>()

		assertTrue(tree.keys().toList().isEmpty())
		assertTrue(tree.values().toList().isEmpty())
	}

	@Test
	fun keysAndValuesFollowAscendingKeyOrderAfterRotations() {
		val tree = AvlTree<Int, String>()
		tree.insert(30, "thirty")
		tree.insert(10, "ten")
		tree.insert(20, "twenty")
		tree.insert(40, "forty")
		tree.insert(50, "fifty")

		assertEquals(
			listOf(10, 20, 30, 40, 50),
			tree.keys().toList(),
		)
		assertEquals(
			listOf("ten", "twenty", "thirty", "forty", "fifty"),
			tree.values().toList(),
		)
	}

	@Test
	fun keysAndValuesReflectRemovalAndReplacement() {
		val tree = AvlTree<Int, String>()
		tree.insert(20, "old")
		tree.insert(10, "ten")
		tree.insert(30, "thirty")

		tree.insert(20, "new")
		tree.remove(10)

		assertEquals(listOf(20, 30), tree.keys().toList())
		assertEquals(listOf("new", "thirty"), tree.values().toList())
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
