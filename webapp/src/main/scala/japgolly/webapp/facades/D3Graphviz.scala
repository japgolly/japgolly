package japgolly.webapp.facades

import scala.scalajs.js
import scala.scalajs.js.annotation._

object D3Graphviz {

  @JSGlobal("Deps.D3G")
  @js.native
  val Start: js.Function1[String, API] = js.native

  @js.native
  trait API extends js.Object {
    def renderDot(dot: String, callback: js.UndefOr[js.Function0[Any]] = js.native): API = js.native
  }

  /*
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
  */
}
