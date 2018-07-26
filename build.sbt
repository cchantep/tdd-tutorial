import com.typesafe.sbt.SbtScalariform.ScalariformKeys

organization := "org.reactivemongo"

name := "tdd-showcase"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.6"

// Compiler options
scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-feature",
  //"-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-numeric-widen",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-g:vars"
)

scalacOptions in Compile ++= Seq(
  "-Ywarn-infer-any",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-Xlint:missing-interpolator"
)

scalacOptions in Compile += "-target:jvm-1.8"

scalacOptions in (Compile, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

scalacOptions in (Test, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

scalacOptions in (Test, console) += "-Yrepl-class-based"

// Dependencies
libraryDependencies ++= {
  val slf4jVer = "1.7.12"

  Seq(
    "dnsjava" % "dnsjava" % "2.1.8",
    "org.slf4j" % "slf4j-api" % slf4jVer % Provided,
    "org.slf4j" % "slf4j-simple" % slf4jVer % Test,
    "org.specs2" %% "specs2-core" % "4.3.2" % Test
  )
}

// Formatting
scalariformAutoformat := true

ScalariformKeys.preferences := {
  import scalariform.formatter.preferences._

  FormattingPreferences().
    setPreference(AlignParameters, false).
    setPreference(AlignSingleLineCaseStatements, true).
    setPreference(CompactControlReadability, false).
    setPreference(CompactStringConcatenation, false).
    setPreference(DoubleIndentConstructorArguments, false).
    setPreference(FormatXml, true).
    setPreference(IndentLocalDefs, false).
    setPreference(IndentPackageBlocks, true).
    setPreference(IndentSpaces, 2).
    setPreference(MultilineScaladocCommentsStartOnFirstLine, false).
    setPreference(PreserveSpaceBeforeArguments, false).
    setPreference(RewriteArrowSymbols, false).
    setPreference(SpaceBeforeColon, false).
    setPreference(SpaceInsideBrackets, false).
    setPreference(SpacesAroundMultiImports, true).
    setPreference(SpacesWithinPatternBinders, true)
}
