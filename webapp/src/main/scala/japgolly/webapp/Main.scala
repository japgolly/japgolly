package japgolly.webapp

import japgolly.webapp.CssSettings._
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import scalacss.internal.mutable.GlobalRegistry

object Main {

  @JSExportTopLevel("main")
  def main(): Unit = {

    // Register styles
    GlobalRegistry.addToDocumentOnRegistration()
    GlobalRegistry.register(new libs.Styles)

    // Start app
    val root = document.getElementById("root")
    MainComponent.Component().renderIntoDOM(root)
    ()
  }

}
