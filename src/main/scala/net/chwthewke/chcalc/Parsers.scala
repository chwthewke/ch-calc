package net.chwthewke.chcalc

import org.parboiled2._
import scala.collection.immutable.SortedMap
import scala.util.Try

private class Parsers( val input : ParserInput ) extends Parser {
  import Parsers._

  def HeroDefRule = rule {
    Name ~ Sep ~
      BigNumberAndSep ~
      BigNumberAndSep ~
      6.times( UpgradePercent ~ Sep ) ~
      Marks ~ EOI ~>
      hero
  }

  def Name = rule { capture( oneOrMore( noneOf( "|" ) ) ) }

  def UpgradePercent = rule { capture( oneOrMore( CharPredicate.Digit | "." ) ) ~> toUpgradePercent }

  def BigNumberAndSep = rule { SuffixedNumber ~ Sep | SciNumber ~ Sep }

  def SciNumber = rule { PosInteger ~ '.' ~ capture( PosInteger ) ~ 'e' ~ PosInteger ~> sci }

  def SuffixedNumber = rule { PosInteger ~ Suffix ~> ( ( n, s ) => n * s ) }

  def PosInteger = rule { capture( oneOrMore( CharPredicate.Digit ) ) ~> ( _.toInt ) }

  def Suffix = rule { optional( SuffixMap ) ~> pow10D }

  def Marks = rule { zeroOrMore( MarkCodes ) ~> ( _.toList ) }

  private def hero : ( String, BigInt, BigInt, Seq[Int], List[Mark] ) => HeroDef =
    ( n, c, d, u, m ) => {
      val upgrades = SortedMap( upgradeLevels.zip( u.toList ) : _* )
      HeroDef( n, c, d, m, upgrades )
    }

  private val toUpgradePercent : String => Int = s => ( s.toDouble * 100 ).toInt

  private val sci : ( Int, Int, String, Int ) => BigInt = ( i, f, fs, e ) => {
    val d = fs.length
    ( i * pow10( d ) + f ) * pow10( e - d )
  }

  private val pow10D : Option[Int] => BigInt = n => pow10( n.getOrElse( 0 ) )

  private def pow10( n : Int ) : BigInt = BigInt( 10 ) pow n
}

object Parsers {
  def heroDef( s : String ) : Try[HeroDef] =
    new Parsers( s ).HeroDefRule.run()

  private val MarkCodes : Map[String, Mark] = Map( "*" -> Knight )

  private val Sep = '|'

  private val SuffixMap : Map[String, Int] =
    Suffixes.zipWithIndex.map { case ( s, i ) => s -> ( i + 1 ) * 3 }.toMap

  private val upgradeLevels : List[Int] = List( 10, 25, 50, 75, 100, 125 )
}
