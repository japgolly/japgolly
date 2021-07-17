package japgolly.webapp.libs

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.webapp.facades.GraphvizReact
import scalacss.ScalaCssReact._

object LibrariesComponent {

  type Props = StateSnapshot[State]

  final case class State()

  object State {
    def init = apply()
  }

  final class Backend($: BackendScope[Props, Unit]) {

    val * = Styles.get()

    private def renderToDot: String = {
      var dot = "digraph G {\n"
      dot += "node[style=filled fillcolor=chartreuse3]\n"
      for (l <- Manifest.scalaLibraries.libs) {
        dot += s"${l.id} [label=\"${l.repoName}\"]\n"
      }
      for (e <- Manifest.scalaLibraries.deps) {
        dot += s"${e.fromLib.id} -> ${e.toLib.id}\n"
      }
      dot += "}"
      dot
    }

    def render(ss: Props): VdomElement = {
      val o = GraphvizReact.Options()
      // o.fit = true
      // o.width = 1000
      <.div(*.graphContainer, GraphvizReact(renderToDot, o))
    }
  }

  val Component = ScalaComponent.builder[Props]
    .renderBackend[Backend]
    .build
}
