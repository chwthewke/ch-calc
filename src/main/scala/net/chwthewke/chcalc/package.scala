package net.chwthewke

import scala.collection.immutable.SortedMap

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

  implicit class BigIntFormatter( val self : BigInt ) extends AnyVal {
    def fmtBig : String = {

      val str = self.toString
      val triples = str.length / 3 + 1
      val removableTriples = triples - 2

      if ( triples <= 2 )
        str
      else if ( removableTriples <= Suffixes.length )
        str.substring( 0, str.length - 3 * removableTriples ) + Suffixes( removableTriples - 1 )
      else
        f"${self.doubleValue}%.3e".filter( _ != '+' )
    }
  }

}
