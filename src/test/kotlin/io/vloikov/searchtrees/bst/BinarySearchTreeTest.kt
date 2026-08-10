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
}
