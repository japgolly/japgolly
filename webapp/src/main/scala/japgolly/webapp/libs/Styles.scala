package japgolly.webapp.libs

import japgolly.webapp.CssSettings._

class Styles extends StyleSheet.Inline {
  import dsl._

  val options = style(
    width(100 %%),
    textAlign.center,
  )

  val option = style(
    display.inlineBlock,
    margin(v = 4 ex, h = 0.7 em),
    unsafeChild("input")(
      marginRight(0.6 ex),
    )
  )

  val graphContainer = style(
    unsafeExt(_ + " svg")(width(100.%%)),
  )
}

object Styles {
  @inline def get() = scalacss.internal.mutable.GlobalRegistry[Styles].get
}