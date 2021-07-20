package japgolly.webapp.util

import org.scalajs.dom._

object DomUtil {

  val SvgNS = "http://www.w3.org/2000/svg"
  val XlinkNS = "http://www.w3.org/1999/xlink"

  @inline implicit class NodeListExt(private val n: NodeList) extends AnyVal {

    def iterator: Iterator[Node] =
      (0 until n.length).iterator.map(n.apply)

    def reverseIterator: Iterator[Node] =
      (0 until n.length).reverse.iterator.map(n.apply)
  }

}
