package japgolly.webapp.libs

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.webapp.facades.D3Graphviz

object GraphComponent {

  final case class Props(dot: String, callback: Callback = Callback.empty) {
    def render: VdomNode = Component(this)
  }

  private var prevId = 0

  final class Backend($: BackendScope[Props, String]) {

    def render(id: String) =
      <.div(^.id := id)

    val renderGraph: Callback =
      for {
        p  <- $.props
        id <- $.state
      } yield {
        D3Graphviz.Start("#" + id).renderDot(p.dot, p.callback.toJsFn)
        ()
      }
  }

  val Component = ScalaComponent.builder[Props]
    .initialState { prevId += 1; s"gv_$prevId" }
    .renderBackend[Backend]
    .componentDidMount(_.backend.renderGraph)
    .componentDidUpdate(_.backend.renderGraph)
    .build
}
