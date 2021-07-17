package japgolly.webapp.facades

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomElement
import scala.scalajs.js
import scala.scalajs.js.annotation._

object GraphvizReact {

  @JSGlobal("Deps.Graphviz")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props extends js.Object {
    var dot: String = js.native
    var options: js.UndefOr[GraphvizOptions] = js.native
    var className: js.UndefOr[String] = js.native
  }

  // https://www.npmjs.com/package/d3-graphviz#creating-a-graphviz-renderer
  @js.native
  trait GraphvizOptions extends js.Object {
  }

  def GraphvizOptions(): GraphvizOptions = {
    val o = (new js.Object).asInstanceOf[GraphvizOptions]
    o
  }

  val Component = JsComponent[Props, Children.None, Null](RawComponent)

  def apply(dot      : String,
            options  : js.UndefOr[GraphvizOptions] = js.undefined,
            className: js.UndefOr[String]          = js.undefined,
           ): VdomElement = {
    val p = (new js.Object).asInstanceOf[Props]
    p.dot       = dot
    p.options   = options
    p.className = className
    Component(p)
  }

}
