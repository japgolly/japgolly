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
    var options: js.UndefOr[Options] = js.native
    var className: js.UndefOr[String] = js.native
  }

  // https://www.npmjs.com/package/d3-graphviz#creating-a-graphviz-renderer
  @js.native
  trait Options extends js.Object {

    var convertEqualSidedPolygons: js.UndefOr[Boolean] = js.native
  //var engine                   : js.UndefOr[String]  = js.native
    var fade                     : js.UndefOr[Boolean] = js.native
    var fit                      : js.UndefOr[Boolean] = js.native
    var growEnteringEdges        : js.UndefOr[Boolean] = js.native
    var height                   : js.UndefOr[Int]     = js.native
  //var keyMode                  : js.UndefOr[xxxxx]   = js.native // title'
    var scale                    : js.UndefOr[Double]  = js.native
    var tweenPaths               : js.UndefOr[Boolean] = js.native
    var tweenPrecision           : js.UndefOr[Double]  = js.native
    var tweenShapes              : js.UndefOr[Boolean] = js.native
    var useWorker                : js.UndefOr[Boolean] = js.native
    var useSharedWorker          : js.UndefOr[Boolean] = js.native
    var width                    : js.UndefOr[Int]     = js.native
    var zoom                     : js.UndefOr[Boolean] = js.native
  //var zoomScaleExtent          : js.UndefOr[xxxxx]   = js.native // 0.1, 10]
  //var zoomTranslateExtent      : js.UndefOr[xxxxx]   = js.native // [[-∞, -∞], [+∞, +∞]]
  }

  def Options(): Options =
    (new js.Object).asInstanceOf[Options]

  val Component = JsComponent[Props, Children.None, Null](RawComponent)

  def apply(dot      : String,
            options  : js.UndefOr[Options] = js.undefined,
            className: js.UndefOr[String]          = js.undefined,
           ): VdomElement = {
    val p = (new js.Object).asInstanceOf[Props]
    p.dot       = dot
    p.options   = options
    p.className = className
    Component(p)
  }

}
