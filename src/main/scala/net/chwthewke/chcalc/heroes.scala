package net.chwthewke.chcalc

import scala.annotation.tailrec
import scala.collection.immutable.SortedMap
import scala.collection.mutable

case class HeroDef(
  name : String,
  cost : BigInt,
  damage : BigInt,
  marks : List[Mark],
  upgrades : SortedMap[Int, Int] )

sealed trait Mark
case object Knight extends Mark

case class Ancients( dogcog : Int, argaiv : Int )

case class Hero( base : HeroDef, ancients : Ancients, level : Int, gild : Int ) {
  lazy val dps : BigInt = computeDps

  private def computeDps() : BigInt = {
    val muls = ( ( level - 175 ) / 25 ).min( 157 )

    val mul5 = if ( base.marks.contains( Knight ) ) ( ( level - 500 ) / 25 ).min( 9 ) else 0
    val mul10 = ( level / 1000 ).min( 3 )
    val mul4 = muls - ( mul5 + mul10 )

    base.damage * level *
      BigInt( 4 ).pow( mul4 ) * BigInt( 5 ).pow( mul5 ) * BigInt( 10 ).pow( mul10 ) *
      ( 100 + ( 50 + 2 * ancients.argaiv ) * gild ) / 100
  }

  def cost( costs : Costs ) : BigInt = {
    val rawCost = costs.rawCost( base, level + 1 )
    return rawCost * ( 100 - 2 * ancients.dogcog ) / 100
  }

}

class Costs( val cache : mutable.Map[String, SortedMap[Int, BigInt]] ) {
  def rawCost( hero : HeroDef, level : Int ) : BigInt = {
    val heroCosts = cache( hero.name )
    val knownLevel = heroCosts.keySet.until( level + 1 ).last
    val knownCost = heroCosts( knownLevel )

    @tailrec
    def go( acc : BigInt, cs : SortedMap[Int, BigInt], k : Int, l : Int ) : ( BigInt, SortedMap[Int, BigInt] ) =
      l - k match {
        case 0 => ( acc, cs )
        case n =>
          val nxt = acc * 107 / 100
          val nxtM = cs + ( k + 1 -> nxt )
          go( nxt, nxtM, k + 1, l )
      }

    val ( cost, updatedHeroCosts ) = go( knownCost, heroCosts, knownLevel, level )

    cache.update( hero.name, updatedHeroCosts )

    cost
  }
}

object Costs {
  def apply( defs : Seq[HeroDef] ) : Costs =
    new Costs( mutable.Map( defs.map( d => d.name -> SortedMap( 1 -> d.cost ) ) : _* ) )
}
