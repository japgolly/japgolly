package japgolly.webapp.libs

object Manifest {

  val scalaLibraries: ScalaLibraries = {
    val mutable = new ScalaLibraries.Mutable
    import mutable._
    import ScalaLibraries.Tag._
    import ScalaLibraries.ScalaJsDomVer._

    val clearConfig      = lib("clear-config",      "clear-config",         "13,3", v1xy)
    val japgolly         = lib("japgolly",          "japgolly",             "13"  , v1xy, App)
    val microlibs        = lib("microlibs-scala",   "microlibs",            "13,3", v200)
    val mrBoilerplate    = lib("mr.boilerplate",    "mr.boilerplate",    "12,13"  , v1xy, App)
    val nyaya            = lib("nyaya",             "nyaya",                "13,3", v1xy)
    val scalacss         = lib("scalacss",          "scalacss",             "13,3", v1xy)
    val scalaGraal       = lib("scala-graal",       "scala-graal",          "13,3", v1xy)
    val scalajsBenchmark = lib("scalajs-benchmark", "scalajs-benchmark",    "13,3", v1xy)
    val scalajsReact     = lib("scalajs-react",     "scalajs-react",        "13,3", v1xy)
    val testState        = lib("test-state",        "test-state",           "13,3", v1xy)
    val tla2json         = lib("tla2json",          "tla2json",             "13"  , none, App)
    val univeq           = lib("univeq",            "univeq",               "13,3", v200)
    val webappUtil       = lib("webapp-util",       "webapp-util",          "13,3", v1xy)

    clearConfig       <-- (microlibs)
    japgolly          <-- (scalacss & scalajsReact & univeq)
    microlibs         <-? (univeq)
    mrBoilerplate     <-- (microlibs & scalajsReact & univeq)
    nyaya             <-- microlibs
    scalacss          <-- (univeq)
    scalacss          <-? (scalajsReact)
    scalacss.test     <-- (microlibs & nyaya)
    scalaGraal.test   <-- (microlibs & nyaya)
    scalajsBenchmark  <-- (microlibs & scalacss & scalajsReact)
    scalajsReact      <-- (microlibs)
    scalajsReact.test <-- (nyaya & univeq)
    testState         <-- (univeq & microlibs)
    testState         <-? (nyaya & scalajsReact)
    tla2json          <-- (microlibs)
    webappUtil        <-- (univeq & scalajsReact)
    webappUtil        <-? (microlibs & nyaya)

    result()
  }

  val metadata =
    ScalaLibraries.fetchMetadata(scalaLibraries)
      .map(_.modify(scalaLibraries("japgolly"))(_.copy(desc = Some("This little webapp you're looking at right now"))))
      .memo()
}
