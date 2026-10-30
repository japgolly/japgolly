package japgolly.webapp.libs

object Manifest {

  val scalaLibraries: ScalaLibraries = {
    val mutable = new ScalaLibraries.Mutable
    import mutable._
    import ScalaLibraries.Tag._
    import ScalaLibraries.ScalaJsDomVer._

    val clearConfig      = lib("clear-config",      "clear-config",         "13,3", v200, Set.empty)
    val japgolly         = lib("japgolly",          "japgolly",             "13"  , v260, Set(App))
    val microlibs        = lib("microlibs-scala",   "microlibs",            "13,3", v200, Set.empty)
    val mrBoilerplate    = lib("mr.boilerplate",    "mr.boilerplate",    "12,13"  , v1xy, Set(App))
    val nyaya            = lib("nyaya",             "nyaya",                "13,3", v200, Set.empty)
    val scalacss         = lib("scalacss",          "scalacss",             "13,3", v200, Set.empty)
    val scalaGraal       = lib("scala-graal",       "scala-graal",          "13,3", none, Set.empty)
    val scalajsBenchmark = lib("scalajs-benchmark", "scalajs-benchmark",    "13,3", v200, Set.empty)
    val scalajsReact     = lib("scalajs-react",     "scalajs-react",        "13,3", v200, Set.empty)
    val shipreq          = lib("shipreq",           "shipreq",              "13"  , v281, Set(App), "shipreq")
    val testState        = lib("test-state",        "test-state",           "13,3", v230, Set.empty)
    val tla2json         = lib("tla2json",          "tla2json",             "13"  , none, Set(App))
    val univeq           = lib("univeq",            "univeq",               "13,3", v200, Set.empty)
    val webappUtil       = lib("webapp-util",       "webapp-util",          "13,3", v220, Set.empty)

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
    shipreq           <-- (clearConfig & microlibs & nyaya & scalacss & scalaGraal & scalajsBenchmark & scalajsReact & testState)
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
