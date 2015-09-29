package net.chwthewke

package object chcalc {
  type Seq[T] = scala.collection.immutable.Seq[T]
  val Seq = scala.collection.immutable.Seq

  val Suffixes : List[String] =
    List(
      "K", "M", "B",
      "T", "q", "Q",
      "s", "S", "O",
      "N", "d", "U",
      "D", "!", "@",
      "#", "$", "%",
      "^", "&", "*" )
}
