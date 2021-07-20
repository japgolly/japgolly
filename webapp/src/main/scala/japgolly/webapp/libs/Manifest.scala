package japgolly.webapp.libs

object Manifest {

  val scalaLibraries: ScalaLibraries = {
    val mutable = new ScalaLibraries.Mutable
    import mutable._
    import ScalaLibraries.Tag._

    val clearConfig      = lib("clear-config",      "12,13"  , Scalaz)
    val japgolly         = lib("japgolly",             "13"  , App)
    val microlibs        = lib("microlibs",         "12,13,3", Scalaz)
    val mrBoilerplate    = lib("mr.boilerplate",    "12,13"  , App)
    val nyaya            = lib("nyaya",             "12,13,3", Scalaz)
    val scalacss         = lib("scalacss",          "12,13"  , Scalaz)
    val scalaGraal       = lib("scala-graal",       "12,13"  , Scalaz)
    val scalajsBenchmark = lib("scalajs-benchmark", "12,13"  , Scalaz)
    val scalajsReact     = lib("scalajs-react",        "13,3")
    val testState        = lib("test-state",        "12,13"  , Scalaz)
    val tla2json         = lib("tla2json",             "13"  , App)
    val univeq           = lib("univeq",            "12,13,3", Scalaz)
    val webappUtil       = lib("webapp-util",          "13"  , Scalaz)

    clearConfig       <-- (microlibs)
    microlibs         <-? (univeq)
    microlibs.test    <-- (nyaya)
    mrBoilerplate     <-- (microlibs & scalajsReact & univeq)
    japgolly          <-- (scalacss & scalajsReact & univeq)
    scalacss          <-- (univeq)
    scalacss          <-? (scalajsReact)
    scalacss.test     <-- (microlibs & nyaya)
    scalaGraal.test   <-- (microlibs & nyaya)
    scalajsBenchmark  <-- (microlibs & scalacss & scalajsReact)
    scalajsReact      <-- (microlibs)
    scalajsReact.test <-- (nyaya & univeq)
    testState         <-- (univeq)
    testState         <-? (nyaya & scalajsReact)
    testState.test    <-- (microlibs)
    tla2json          <-- (microlibs)
    webappUtil        <-- (univeq & scalajsReact)
    webappUtil        <-? (microlibs & nyaya)

    result()
  }

  val metadata =
    ScalaLibraries.fetchMetadata(scalaLibraries).memo()
}
