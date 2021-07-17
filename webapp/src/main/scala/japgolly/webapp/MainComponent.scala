package japgolly.webapp

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object MainComponent {

  type Props = Unit

  final case class State()

  object State {
    def init = apply(
    )
  }

  final class Backend($: BackendScope[Props, State]) {
    def render(s: State): VdomElement =
      <.div("Cool! state = " + s)
  }

  val Component = ScalaComponent.builder[Props]
    .initialState(State.init)
    .renderBackend[Backend]
    .build
}