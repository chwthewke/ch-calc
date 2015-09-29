package net.chwthewke.chcalc

import org.parboiled2._
import scala.collection.immutable.SortedMap
import scala.math.BigInt
import scala.util.Try

case class HeroDef(
  name : String,
  cost : BigInt,
  damage : BigInt,
  marks : List[Mark],
  upgrades : SortedMap[Int, Int] )

sealed trait Mark
case object Knight extends Mark
