 case class TreeNode(var data: Int, var left: TreeNode, var right: TreeNode) {
  def this(data: Int) =
    this(data, null, null)
}

  case class BT(var root: TreeNode) {
    def this() =
      this(null)


    def countLeafNodes(node: TreeNode): Int =
      if (node != null)
        val result: Int = countLeafNodes(node.left) + countLeafNodes(node.right)
        if (node.left == null && node.right == null) result + 1
        else result
      else 0


    def height(node: TreeNode): Int =
      if (node != null)
        val a: Int = this.height(node.left)
        val b: Int = this.height(node.right)
        if (a > b) a + 1
        else b + 1
      else -1

    def preOrderTraversal(node: TreeNode): Unit =
      if(node != null)
        println(" "+ node.data)
        preOrderTraversal(node.left)
        preOrderTraversal(node.right)


}



object Main {
  def main(args: Array[String]): Unit = {
    val tree: BT = new BT()
    /*
      Binary Tree
      ------------------
             9
           /   \
          7     2
         /    /   \
        5    6     3
            / \   / \
           1   4 8   10
    */
    // Construct tree
    tree.root = new TreeNode(9)
    tree.root.left = new TreeNode(7)
    tree.root.right = new TreeNode(2)
    tree.root.right.right = new TreeNode(3)
    tree.root.right.left = new TreeNode(6)
    tree.root.right.left.left = new TreeNode(1)
    tree.root.right.left.right = new TreeNode(4)
    tree.root.left.left = new TreeNode(5)
    tree.root.right.right.left = new TreeNode(8)
    tree.root.right.right.right = new TreeNode(10)

    val result: Int = tree.countLeafNodes(tree.root)
    println(" Total leaf nodes : " + result)
    println(" Height of the tree: "+tree.height(tree.root))
    tree.preOrderTraversal(tree.root)
  }
}