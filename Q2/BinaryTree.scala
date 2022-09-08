//trait Tree
//case class Node[A](left: Tree, value: A, right: Tree) extends Tree

sealed abstract class BinaryTree {
  def data: Int

  def left: BinaryTree

  def right: BinaryTree

  def isEmpty: Boolean

  def preOrderTraversal: String

  def heightOfTheTree(Node:BinaryTree): Int

  def countLeafNode(Node: BinaryTree): Int 


}

case object EmptyTree extends BinaryTree {
  def data: Nothing = throw new NoSuchElementException

  def left: BinaryTree = throw new NoSuchElementException

  def right: BinaryTree = throw new NoSuchElementException

  def isEmpty = true

  def preOrderTraversal: String = ""

  def heightOfTheTree(Node: BinaryTree): Int = 0

  def countLeafNode(Node: BinaryTree): Int = 0


}

case class NonEmptyTree(d: Int, l: BinaryTree, r: BinaryTree) extends BinaryTree {
  def data: Int = d

  def left: BinaryTree = l

  def right: BinaryTree = r

  def isEmpty: Boolean = false

  def preOrderTraversal: String =
    if(left.isEmpty) "" + data
    else if(right.isEmpty) s"$data ${left.preOrderTraversal} "
    else s"$data ${left.preOrderTraversal} ${right.preOrderTraversal}"

  def heightOfTheTree(Node:BinaryTree): Int =
    if(Node.isEmpty) -1
    else
      val a: Int = this.heightOfTheTree(Node.left)
      val b: Int = this.heightOfTheTree(Node.right)
      if(a > b) a+1
      else b+1

  def countLeafNode(Node: BinaryTree): Int =
    if(Node.isEmpty) 0
    else 
      val result: Int = countLeafNode(Node.left) +
        countLeafNode(Node.right)
      if(Node.left.isEmpty && Node.right.isEmpty) result + 1
      else result










}



