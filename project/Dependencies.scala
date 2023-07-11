import sbt._
import sbt.Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  object Ver {

    val betterMonadicFor = "0.3.1"
    val kindProjector    = "0.13.2"
    val monocle          = "3.2.0"
    val scala            = "2.13.11"
    val scalaCss         = "1.0.0"
    val scalaJsDom       = "2.6.0"
    val scalaJsReact     = "2.1.1"
    val univEq           = "2.0.1"
  }

  object Dep {
    val monocleCore         = Def.setting("dev.optics"                        %%% "monocle-core"       % Ver.monocle)
    val monocleMacro        = Def.setting("dev.optics"                        %%% "monocle-macro"      % Ver.monocle)
    val scalaCss            = Def.setting("com.github.japgolly.scalacss"      %%% "core"               % Ver.scalaCss)
    val scalaCssReact       = Def.setting("com.github.japgolly.scalacss"      %%% "ext-react"          % Ver.scalaCss)
    val scalaJsDom          = Def.setting("org.scala-js"                      %%% "scalajs-dom"        % Ver.scalaJsDom cross CrossVersion.for3Use2_13)
    val scalaJsReactCore    = Def.setting("com.github.japgolly.scalajs-react" %%% "core"               % Ver.scalaJsReact)
    val scalaJsReactExtra   = Def.setting("com.github.japgolly.scalajs-react" %%% "extra"              % Ver.scalaJsReact)
    val scalaJsReactMonocle = Def.setting("com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3" % Ver.scalaJsReact)
    val univEq              = Def.setting("com.github.japgolly.univeq"        %%% "univeq"             % Ver.univEq)

    // Compiler plugins
    val betterMonadicFor = compilerPlugin("com.olegpy"     %% "better-monadic-for" % Ver.betterMonadicFor)
    val kindProjector    = compilerPlugin("org.typelevel"  %% "kind-projector"     % Ver.kindProjector cross CrossVersion.full)
  }
}
