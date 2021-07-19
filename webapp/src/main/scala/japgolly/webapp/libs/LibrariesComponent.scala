package japgolly.webapp.libs

import japgolly.scalajs.react.ReactMonocle._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.webapp.facades.GraphvizReact
import monocle.macros.GenLens
import scalacss.ScalaCssReact._

object LibrariesComponent {

  type Props = StateSnapshot[State]

  final case class State(opts: Set[Option])

  object State {
    def init = apply(
      Set(Option.Optional),
    )
    val opts = GenLens[State](_.opts)
  }

  sealed trait Option
  object Option {
    case object Optional  extends Option
    case object Dev       extends Option
    case object Apps      extends Option
    case object ScalaVers extends Option
    case object Scalaz    extends Option

    val mutallyExclusive = Set[Set[Option]](
      Set(Dev, Optional),
      Set(ScalaVers, Scalaz),
    )
  }

  // ===================================================================================================================

  final class Backend {

    val * = Styles.get()

    private val renderHeader =
      <.h2(
        *.header,
        "@japgolly Scala libraries")

    private def renderOptions(ss: StateSnapshot[Set[Option]]): VdomNode = {
      def renderOption(o: Option, label: VdomNode) = {
        val on = ss.value.contains(o)
        val toggle =
          if (on)
            ss.modState(_ - o)
          else
            ss.modState { s0 =>
              var s = s0
              for (e <- Option.mutallyExclusive)
                if (e.contains(o))
                  s --= e
              s + o
            }

        <.label(
          *.option,
          <.input.checkbox( ^.checked := on, ^.onChange --> toggle),
          label)
      }

      <.div(
        *.options,
        renderOption(Option.Optional,  "Show optional deps"),
        renderOption(Option.Dev,       "Show dev deps"),
        renderOption(Option.Apps,      "Show apps (as well as libraries)"),
        renderOption(Option.ScalaVers, "Show Scala versions"),
        renderOption(Option.Scalaz,    "Still on/supports Scalaz"),
      )
    }

    private def renderToDot(opts: Set[Option]): String = {
      import ScalaLibraries._

      def allowLib(l: Lib): Boolean =
        (opts.contains(Option.Apps) || !l(Tag.App))

      def renderNodes(libs: Set[Lib]): String = {
        val _ = libs // unused for now
        var dot = ""
        for (l <- Manifest.scalaLibraries.libs)
          if (allowLib(l)) {
            dot += l.id.toString
            var name = l.repoName

            if (opts.contains(Option.ScalaVers)) {
              name += "\\nScala vers: " + l.verStrs.mkString(", ")
              if (!l.scala3)
                dot += "[fillcolor=gray]"
            }

            if (l.tags.contains(Tag.App)) {
              name = s"[app] $name"
              dot += "[fillcolor=lightblue1]"
            }

            if (opts.contains(Option.Scalaz) && l(Tag.Scalaz)) {
              name += "\\nStill on/supports Scalaz"
              dot += "[fillcolor=orange]"
            }

            dot += s"[label=\"${name}\"]"
            dot += '\n'
          }
        dot
      }

      def renderEdges: (String, Set[Lib]) = {
        var dot = ""
        var libs = Set.empty[Lib]
        var deps = Manifest.scalaLibraries.deps

        if (opts.contains(Option.Dev)) {
          deps = deps.map(_.copy(fromScope = Scope.Main, toScope = Scope.Main, optional = false))
        }

        for (d <- deps) {

          var allow = allowLib(d.fromLib) && allowLib(d.toLib)
          def applyFilter(f: (Lib, Scope) => Boolean): Unit = {
            allow &&= f(d.fromLib, d.fromScope)
            allow &&= f(d.toLib, d.toScope)
          }
          if (!opts.contains(Option.Optional))
            allow &&= !d.optional
          if (!opts.contains(Option.Dev))
            applyFilter((_, s) => s match {
              case Scope.Main => true
              case Scope.Test => false
            })

          def addEdge(scope: Scope): Unit = {
            libs += d.fromLib
            libs += d.toLib
            dot += s"${d.fromLib.id} -> ${d.toLib.id}"
            if (d.optional)
              dot += "[style=dashed color=purple1]"
            scope match {
              case Scope.Main =>
              case Scope.Test => dot += "[color=green4]"
            }
            dot += '\n'
          }

          if (allow)
            addEdge(d.fromScope & d.toScope)
        }
        (dot, libs)
      }

      val (edgeDot, libs) = renderEdges
      val nodeDot = renderNodes(libs)

      s"""
         |digraph G {
         |  node[style=filled fillcolor=darkolivegreen2]
         |  $nodeDot
         |  $edgeDot
         |}
         |""".stripMargin.trim
    }

    def render(ss: Props): VdomElement =
      <.div(
        renderHeader,
        renderOptions(ss.zoomStateL(State.opts)),
        <.div(*.graphContainer, GraphvizReact(renderToDot(ss.value.opts))),
      )
  }

  val Component = ScalaComponent.builder[Props]
    .renderBackend[Backend]
    .build
}
