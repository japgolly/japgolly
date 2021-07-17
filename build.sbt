import Dependencies._

ThisBuild / version      := "1.0-SNAPSHOT"
ThisBuild / organization := "com.github.japgolly.japgolly"
ThisBuild / shellPrompt  := ((s: State) => Project.extract(s).currentRef.project + "> ")

def scalacCommonFlags: Seq[String] = Seq(
  "-deprecation",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-unchecked",
)

def scalac2Flags = Seq(
  "-Wconf:msg=may.not.be.exhaustive:e",            // Make non-exhaustive matches errors instead of warnings
  "-Wconf:msg=Reference.to.uninitialized.value:e", // Make uninitialised value calls errors instead of warnings
  "-Wunused:explicits",                            // Warn if an explicit parameter is unused.
  "-Wunused:implicits",                            // Warn if an implicit parameter is unused.
  "-Wunused:imports",                              // Warn if an import selector is not referenced.
  "-Wunused:locals",                               // Warn if a local definition is unused.
  "-Wunused:nowarn",                               // Warn if a @nowarn annotation does not suppress any warnings.
  "-Wunused:patvars",                              // Warn if a variable bound in a pattern is unused.
  "-Wunused:privates",                             // Warn if a private member is unused.
  "-Xlint:adapted-args",                           // An argument list was modified to match the receiver.
  "-Xlint:constant",                               // Evaluation of a constant arithmetic expression resulted in an error.
  "-Xlint:delayedinit-select",                     // Selecting member of DelayedInit.
  "-Xlint:deprecation",                            // Enable -deprecation and also check @deprecated annotations.
  "-Xlint:eta-zero",                               // Usage `f` of parameterless `def f()` resulted in eta-expansion, not empty application `f()`.
  "-Xlint:implicit-not-found",                     // Check @implicitNotFound and @implicitAmbiguous messages.
  "-Xlint:inaccessible",                           // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                              // A type argument was inferred as Any.
  "-Xlint:missing-interpolator",                   // A string literal appears to be missing an interpolator id.
  "-Xlint:nonlocal-return",                        // A return statement used an exception for flow control.
  "-Xlint:nullary-unit",                           // `def f: Unit` looks like an accessor; add parens to look side-effecting.
  "-Xlint:option-implicit",                        // Option.apply used an implicit view.
  "-Xlint:poly-implicit-overload",                 // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",                         // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                            // In a pattern, a sequence wildcard `_*` should match all of a repeated parameter.
  "-Xlint:valpattern",                             // Enable pattern checks in val definitions.
  "-Xmixin-force-forwarders:false",                // Only generate mixin forwarders required for program correctness.
  "-Yjar-compression-level", "9",                  // compression level to use when writing jar files
  "-Ymacro-annotations",                           // Enable support for macro annotations, formerly in macro paradise.
  "-Ypatmat-exhaust-depth", "off",
)

def sourceMapsToGithub: Project => Project =
  p => p.settings(
    scalacOptions ++= {
      val isDotty = scalaVersion.value startsWith "3"
      val ver     = version.value
      if (isSnapshot.value)
        Nil
      else {
        val a = p.base.toURI.toString.replaceFirst("[^/]+/?$", "")
        val g = s"https://raw.githubusercontent.com/japgolly/japgolly"
        val flag = if (isDotty) "-scalajs-mapSourceURI" else "-P:scalajs:mapSourceURI"
        s"$flag:$a->$g/v$ver/" :: Nil
      }
    }
  )

def commonSettings: Project => Project = _
  .configure(sourceMapsToGithub)
  .settings(
    scalaVersion         := Ver.scala,
    scalacOptions       ++= (scalacCommonFlags ++ scalac2Flags),
    libraryDependencies ++= Seq(Dep.betterMonadicFor, Dep.kindProjector),
  )

lazy val root = Project("root", file("."))
  .configure(commonSettings)
  .aggregate(webapp)

lazy val webapp = project
  .enablePlugins(ScalaJSPlugin)
  .configure(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dep.monocleCore.value,
      Dep.monocleMacro.value,
      Dep.scalaCss.value,
      Dep.scalaCssReact.value,
      Dep.scalaJsDom.value,
      Dep.scalaJsReactCore.value,
      Dep.scalaJsReactExtra.value,
      Dep.scalaJsReactMonocle.value,
      Dep.univEq.value,
    ),
  )
