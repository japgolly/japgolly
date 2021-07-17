package japgolly.webapp

import japgolly.scalajs.react.ReactMonocle._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.webapp.libs.LibrariesComponent
import monocle.macros.GenLens

object MainComponent {

  type Props = Unit

  final case class State(libs: LibrariesComponent.State)

  object State {
    def init = apply(
      LibrariesComponent.State.init,
    )

    val libs = GenLens[State](_.libs)
  }

  final class Backend($: BackendScope[Props, State]) {
    def render(s: State): VdomElement = {
      val ssLibs = StateSnapshot.zoomL(State.libs)(s).setStateVia($)
      LibrariesComponent.Component(ssLibs)
    }
  }

  val Component = ScalaComponent.builder[Props]
    .initialState(State.init)
    .renderBackend[Backend]
    .build
}
