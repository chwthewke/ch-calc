package net.chwthewke.chcalc

import scala.language.reflectiveCalls
import scala.io.Codec
import scala.io.Source
import scala.util.Failure
import scala.util.Try

object Main {
  def main( args : Array[String] ) = {}

  def loadHeroDefs : Try[List[HeroDef]] = {

    usingT( getClass.getClassLoader.getResourceAsStream( "heroes.dat" ) ) { in =>
      usingT( Source.fromInputStream( in )( Codec.UTF8 ) ) { s =>
        val lines = s.getLines().toList
        val z : Try[List[HeroDef]] = Try( Nil )
        ( lines :\ z ) { ( l, acc ) =>
          Parsers.heroDef( l ).transform( d => acc.map( d :: _ ), Failure( _ ) )
        }

      }
    }
  }

  // TODO: replace Try with Disjunction

  private def usingT[A <: { def close() : Unit }, B]( a : => A )( f : A => Try[B] ) : Try[B] = {
    val resource = a
    val result = Try( f( a ) ).flatten
    Try( resource.close() ).flatMap( _ => result )
  }

  private def using[A <: { def close() : Unit }, B]( a : => A )( f : A => B ) : Try[B] =
    usingT( a )( x => Try( f( x ) ) )
}
