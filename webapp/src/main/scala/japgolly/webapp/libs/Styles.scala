package japgolly.webapp.libs

import japgolly.webapp.CssSettings._

class Styles extends StyleSheet.Inline {
  import dsl._

  val graphContainer = style(
    unsafeExt(_ + " svg")(width(100.%%)),
  )
}

object Styles {
  @inline def get() = scalacss.internal.mutable.GlobalRegistry[Styles].get
}