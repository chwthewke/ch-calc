import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform._
import com.typesafe.sbteclipse.core.EclipsePlugin._
import scalariform.formatter.preferences._
import scoverage.ScoverageSbtPlugin
import sbtbuildinfo.Plugin._

object ChcalcBuild extends Build {

  object Dependencies {
    val scalazVersion = "7.1.1"

    val scalaz = "org.scalaz" %% "scalaz-core" % scalazVersion withSources () withJavadoc ()

    val scalatest = "org.scalatest" %% "scalatest" % "2.2.4" % "test" withSources () withJavadoc ()

    val scalacheck = Seq(
      "org.scalacheck" %% "scalacheck" % "1.12.2" % "test" withSources () withJavadoc (),
      "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test" withSources () withJavadoc ()
    )

    val parboiled = "org.parboiled" %% "parboiled" % "2.1.0"
  }

  override def settings = super.settings :+ ( EclipseKeys.skipParents in ThisBuild := false )

  lazy val chcalcScalariformSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := defaultPreferences
      .setPreference( AlignSingleLineCaseStatements, true )
      .setPreference( SpaceBeforeColon, true )
      .setPreference( SpaceInsideParentheses, true )
  )

  lazy val sharedSettings =
    Seq(
      organization := "net.chwthewke",
      scalaVersion := "2.11.7" )

  lazy val chcalcSettings =
    Defaults.coreDefaultSettings ++
      SbtBuildInfo.buildSettings( "net.chwthewke.chcalc" ) ++
      SbtEclipse.buildSettings ++
      chcalcScalariformSettings ++
      sharedSettings ++
      Seq(
        libraryDependencies ++= Seq(
          Dependencies.scalatest,
          Dependencies.scalaz,
          Dependencies.parboiled ) ++
          Dependencies.scalacheck,
        scalacOptions ++= Seq( "-feature", "-deprecation" ),
        unmanagedSourceDirectories in Compile := ( scalaSource in Compile ).value :: Nil,
        unmanagedSourceDirectories in Test := ( scalaSource in Test ).value :: Nil
      )

  lazy val chcalc = Project(
    id = "ch-calc",
    base = file( "." ),
    settings = chcalcSettings ++
      Seq(
        name := "ch-calc",
        mainClass := Some( "net.chwthewke.chcalc.Main" ),
        initialCommands := """|import net.chwthewke.chcalc._
                              |import scalaz._,Scalaz._""".stripMargin
      )
  )
}
