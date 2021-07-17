package japgolly.webapp.libs

import ScalaLibraries._

final case class ScalaLibraries(libs: Set[Lib], deps: Set[Dep])

object ScalaLibraries {

  final case class Lib(repoName: String, scalaVersions: Set[String], tags: Set[Tag])

  sealed trait Scope
  object Scope {
    case object Main extends Scope
    case object Test extends Scope
  }

  sealed trait Tag
  object Tag {
    case object App extends Tag
    case object Scalaz extends Tag
  }

  final case class Dep(fromLib: Lib, fromScope: Scope, toLib: Lib, toScope: Scope, optional: Boolean)

  // ===================================================================================================================

  final class Mutable {
    import Scope._

    private object state {
      var libs = List.empty[Lib]
      var deps = List.empty[Dep]

      def addDep(f: MScope, t: MScope, optional: Boolean): Unit =
        for {
          (fl, fs) <- f.modules
          (tl, ts) <- t.modules
        } deps ::= Dep(fl, fs, tl, ts, optional)
    }

    trait MScope {
      private[Mutable] def modules: Seq[(Lib, Scope)]
      protected def addDep(to: MScope, optional: Boolean): Unit

      final def -->(to  : MScope): to  .type = { addDep(to, false); to }
      final def ?->(to  : MScope): to  .type = { addDep(to, true ); to }
      final def <--(from: MScope): from.type = { from --> this; from }
      final def <-?(from: MScope): from.type = { from ?-> this; from }
    }

    trait MLib extends MScope {
      def withScope(s: Scope): MLib
      def &(l: MLib): MLib
      final def test = withScope(Test)
    }

    def lib(repoName: String, scalaVersions: String, tags: Tag*): MLib = {
      val scalaVerSet = scalaVersions.split(',').map {
        case "12" => "2.12"
        case "13" => "2.13"
        case "3"  => "3.0"
      }.toSet
      val l = Lib(repoName, scalaVerSet, tags.toSet)
      def mkWithScope(s: Scope): MLib =
        new MLib {
          override private[Mutable] val modules = Seq((l, s))
          override protected def addDep(to: MScope, optional: Boolean) =
            state.addDep(this, to, optional)
          override def withScope(s: Scope) =
            mkWithScope(s)
          override def &(l: MLib) =
            libs(Seq(this, l))
        }
      mkWithScope(Main)
    }

    private def libs(ls: Seq[MLib]): MLib =
      new MLib {
        override private[Mutable] val modules = ls.flatMap(_.modules)
          override protected def addDep(to: MScope, optional: Boolean) =
            ls.foreach(state.addDep(_, to, optional))
        override def withScope(s: Scope) =
          libs(ls.map(_.withScope(s)))
        override def &(l: MLib) =
          libs(ls :+ l)
      }

    def result() = ScalaLibraries(
      libs = state.libs.toSet,
      deps = state.deps.toSet,
    )
  }
}
