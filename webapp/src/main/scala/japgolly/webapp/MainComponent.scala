package japgolly.webapp

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.webapp.facades.GraphvizReact

object MainComponent {

  type Props = Unit

  final case class State()

  object State {
    def init = apply(
    )
  }

  final class Backend($: BackendScope[Props, State]) {

    val dot = """
graph {
  grandparent -- "parent A";
  child;
  "parent B" -- child;
  grandparent --  "parent B";
}
    """

    def render(s: State): VdomElement =
      <.div(
        <.div("Cool! state = " + s),
        GraphvizReact(dot)
      )
  }

  val Component = ScalaComponent.builder[Props]
    .initialState(State.init)
    .renderBackend[Backend]
    .build
}