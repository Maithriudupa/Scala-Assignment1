object Test extends App{
  val root: NonEmptyTree =  NonEmptyTree(9,
    NonEmptyTree(7 ,
      NonEmptyTree(5, EmptyTree, EmptyTree),
      EmptyTree),
    NonEmptyTree(2,
      NonEmptyTree(6,
        NonEmptyTree(1, EmptyTree, EmptyTree),
        NonEmptyTree(4, EmptyTree, EmptyTree)),
      NonEmptyTree(3,
        NonEmptyTree(8, EmptyTree, EmptyTree),
        NonEmptyTree(10, EmptyTree, EmptyTree))
    ))

  //Binary Tree
  //-----------------
  //              9
  //           /    \
  //          7      2
  //         /    /    \
  //        5     6     3
  //             /  \  /  \
  //            1    4 8   10

  println(root.preOrderTraversal)
  println(root.heightOfTheTree(root))
  println(root.countLeafNode(root))

}
