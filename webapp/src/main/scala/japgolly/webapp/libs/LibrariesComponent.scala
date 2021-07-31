package japgolly.webapp.libs

import japgolly.scalajs.react.ReactMonocle._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.webapp.util.DomUtil._
import monocle.macros.GenLens
import org.scalajs.dom.document
import org.scalajs.dom.raw.{Element, HTMLDivElement, SVGGElement}
import scalacss.ScalaCssReact._

object LibrariesComponent {

  type Props = StateSnapshot[State]

  final case class State(opts: Set[Opt], meta: Option[ScalaLibraries.Metadata])

  object State {
    def init = apply(
      opts = Set(),
      meta = None,
    )

    val opts = GenLens[State](_.opts)
  }

  sealed trait Opt
  object Opt {
    case object Optional  extends Opt
    case object Dev       extends Opt
    case object Apps      extends Opt
    case object ScalaVers extends Opt

    val mutallyExclusive = Set[Set[Opt]](
      Set(Dev, Optional),
    )
  }

  // ===================================================================================================================

  final class Backend($: BackendScope[Props, Unit]) {

    val * = Styles.get()

    val loadMetadata: AsyncCallback[Unit] =
      for {
        m <- Manifest.metadata
        p <- $.props.asAsyncCallback
        _ <- p.modStateAsync(_.copy(meta = Some(m)))
      } yield ()

    private val renderHeader =
      <.h2(
        *.header,
        "@japgolly Scala libraries")

    private def renderOpts(ss: StateSnapshot[Set[Opt]]): VdomNode = {
      def renderOpt(o: Opt, label: VdomNode) = {
        val on = ss.value.contains(o)
        val toggle =
          if (on)
            ss.modState(_ - o)
          else
            ss.modState { s0 =>
              var s = s0
              for (e <- Opt.mutallyExclusive)
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
        renderOpt(Opt.Optional,  "Show optional deps"),
        renderOpt(Opt.Dev,       "Show dev deps"),
        renderOpt(Opt.Apps,      "Show apps (as well as libraries)"),
        renderOpt(Opt.ScalaVers, "Show Scala versions"),
      )
    }

    private def renderToDot(opts: Set[Opt], meta: Option[ScalaLibraries.Metadata]): String = {
      import ScalaLibraries._

      def allowLib(l: Lib): Boolean =
        (opts.contains(Opt.Apps) || !l(Tag.App))

      def renderNodes(libs: Set[Lib]): String = {
        val _ = libs // unused for now
        var dot = ""
        for (l <- Manifest.scalaLibraries.libs)
          if (allowLib(l)) {
            dot += l.id.toString
            var name = l.displayName

            for {
              m <- meta
              v <- m(l).latestVer
            } name += s" v$v"

            if (opts.contains(Opt.ScalaVers)) {
              name += "\\nScala vers: " + l.verStrs.mkString(", ")
              if (!l.scala3)
                dot += "[fillcolor=gray]"
            }

            if (l.tags.contains(Tag.App)) {
              name = s"[app]\\n$name"
              dot += "[fillcolor=lightblue1]"
            }

            dot += s"[id=${l.id} label=\"${name}\"]"
            dot += '\n'
          }
        dot
      }

      def renderEdges: (String, Set[Lib]) = {
        var dot = ""
        var libs = Set.empty[Lib]
        var deps = Manifest.scalaLibraries.deps

        if (opts.contains(Opt.Dev)) {
          deps = deps.map(_.copy(fromScope = Scope.Main, toScope = Scope.Main, optional = false))
        }

        for (d <- deps) {

          var allow = allowLib(d.fromLib) && allowLib(d.toLib)
          def applyFilter(f: (Lib, Scope) => Boolean): Unit = {
            allow &&= f(d.fromLib, d.fromScope)
            allow &&= f(d.toLib, d.toScope)
          }
          if (!opts.contains(Opt.Optional))
            allow &&= !d.optional
          if (!opts.contains(Opt.Dev))
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

    val graphRef = Ref[HTMLDivElement]

    def renderGraph(s: State) =
      <.div.withRef(graphRef)(
        *.graphContainer,
        GraphComponent.Props(renderToDot(s.opts, s.meta), enrichGraph).render)

    lazy val enrichGraph: Callback =
      for {
        div <- graphRef.get.asCBO
        p   <- $.props.toCBO
      } yield {
        val meta = p.value.meta

        for (node <- div.querySelectorAll("g.node").iterator.map(_.domCast[SVGGElement])) {

          val id = node.id.toInt
          val lib = Manifest.scalaLibraries.libById(id)

          // Set title
          val titleEl =
            node.childNodes.iterator.flatMap {
              case e: Element if e.tagName.toLowerCase == "title" => e :: Nil
              case _ => Nil
            }.next()
          titleEl.textContent = meta.fold("desc loading")(_(lib).desc.getOrElse(lib.repoName))

          // Make link
          val parent = node.parentNode
          if (parent.nodeName.toUpperCase != "A") {
            val a = document.createElementNS(SvgNS, "a")
            a.setAttributeNS(XlinkNS, "xlink:href", lib.url)
            a.setAttribute("opener", null)
            a.setAttribute("target", "_blank")
            parent.replaceChild(a, node)
            a.appendChild(node)
          }
        }
      }

    def render(ss: Props): VdomElement =
      <.div(
        renderHeader,
        renderOpts(ss.zoomStateL(State.opts)),
        renderGraph(ss.value),
      )
  }

  val Component = ScalaComponent.builder[Props]
    .renderBackend[Backend]
    .componentDidMount(_.backend.loadMetadata)
    .build
}
