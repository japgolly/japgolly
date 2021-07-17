package japgolly.webapp

import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.document

object Main {

  @JSExportTopLevel("main")
  def main(): Unit = {
    val root = document.getElementById("root")
    MainComponent.Component().renderIntoDOM(root)
    ()
  }

}
