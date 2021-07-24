package japgolly.webapp.libs

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Ajax
import japgolly.webapp.libs.ScalaLibraries._
import japgolly.webapp.util.AbstractWebStorage
import scala.scalajs.js
import scala.scalajs.js.JSON

final case class ScalaLibraries(libs: Set[Lib], deps: Set[Dep]) {

  def apply(repoName: String): Lib =
    libs.find(_.repoName == repoName).get

  val libById: Int => Lib =
    libs.iterator.map(l => l.id -> l).toMap.apply
}

object ScalaLibraries {

  final case class Lib(id           : Int,
                       repoName     : String,
                       displayName  : String,
                       scalaVersions: Set[ScalaVer],
                       tags         : Set[Tag]) {
    val verStrs = scalaVersions.iterator.map(_.ver).toVector.sorted
    val scala3 = scalaVersions.exists(_.ver startsWith "3")
    def apply(t: Tag): Boolean = tags contains t
    val url = "https://github.com/japgolly/" + repoName
  }

  sealed trait Scope {
    import Scope._

    def &(s: Scope): Scope =
      (this, s) match {
        case (Main, Main) => Main
        case (Main, Test) => Test
        case (Test, Main) => Test
        case (Test, Test) => Test
      }
  }
  object Scope {
    case object Main extends Scope
    case object Test extends Scope
  }

  sealed trait Tag
  object Tag {
    case object App extends Tag
    case object Scalaz extends Tag
  }

  sealed abstract class ScalaVer(final val ver: String)
  object ScalaVer {
    case object v2_12 extends ScalaVer("2.12")
    case object v2_13 extends ScalaVer("2.13")
    case object v3_0 extends ScalaVer("3.0")
  }

  final case class Dep(fromLib: Lib, fromScope: Scope, toLib: Lib, toScope: Scope, optional: Boolean)

  // ===================================================================================================================

  final class Mutable {
    import Scope._

    private object state {
      var prevId = 0
      var libs   = List.empty[Lib]
      var deps   = List.empty[Dep]

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

    def lib(repoName: String, displayName: String, scalaVersions: String, tags: Tag*): MLib = {
      val scalaVerSet = scalaVersions.split(',').map[ScalaVer] {
        case "12" => ScalaVer.v2_12
        case "13" => ScalaVer.v2_13
        case "3"  => ScalaVer.v3_0
      }.toSet
      state.prevId += 1
      val id = state.prevId
      val l = Lib(id, repoName, displayName, scalaVerSet, tags.toSet)
      state.libs ::= l
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

  // ===================================================================================================================

  final class Metadata(meta: Map[Int, LibMeta]) {
    def apply(lib: Lib) = meta(lib.id)
    def modify(lib: Lib)(f: LibMeta => LibMeta) = new Metadata(meta.updated(lib.id, f(apply(lib))))
  }

  final case class LibMeta(desc: Option[String], latestVer: Option[String])

  def fetchMetadata(manifest: ScalaLibraries): AsyncCallback[Metadata] = {
    import AbstractWebStorage.{Key, Value}

    val ws         = AbstractWebStorage.localOrEmpty()
    def now()      = System.currentTimeMillis()
    val hour       = 3_600_000L
    val cutoff     = now() - hour
    val releaseFmt = """^\d+(\.\d+)+(-(RC|M)?\d+)?$""".r

    def fetch(lib: Lib): AsyncCallback[LibMeta] = {

      trait RepoResult extends js.Object {
        def description: String
      }

      trait TagResult extends js.Object {
        def name: String
      }

      type TagsResult = js.Array[TagResult]

      def parseTag(tag: TagResult): Option[String] = {
        var t = tag.name
        if (t.startsWith("v")) t = t.drop(1)
        Option.when(releaseFmt.matches(t))(t)
      }

      trait CachedRequest extends js.Object {
        val timeMs: String
        val response: String
      }
      object CachedRequest {
        def decode(s: Value): CachedRequest = JSON.parse(s.value).asInstanceOf[CachedRequest]
        def encode(r: CachedRequest): Value = Value(JSON.stringify(r))
      }

      def cachedAjax(url: String)(real: AsyncCallback[String]): AsyncCallback[String] = {
        val key = Key(url)
        ws.getItem(key).asAsyncCallback
          .map(_.map(CachedRequest.decode))
          .attempt
          .flatMap {
            case Right(Some(c)) if c.timeMs.toLong > cutoff =>
              AsyncCallback.pure(c.response)
            case _ =>
              for {
                s <- real
                _ <- ws.setItem(key, CachedRequest.encode(new CachedRequest {
                       override val timeMs = now().toString
                       override val response = s
                     })).asAsyncCallback
              } yield s
          }
      }

      def githubApi[A](url: String): AsyncCallback[A] =
        cachedAjax(url)(
          Ajax("GET", url)
            .setRequestContentTypeJsonUtf8
            .setRequestHeader("Accept", "application/vnd.github.v3+json")
            .setRequestHeader("User-Agent", "github.com/japgolly/japgolly")
            .send
            .validateStatusIs(200)(Callback.throwException(_))
            .asAsyncCallback
            .map(_.responseText)
        )
        .map(JSON.parse(_).asInstanceOf[A])

      val fetchRepo =
        githubApi[RepoResult](s"https://api.github.com/repos/japgolly/${lib.repoName}")

      val fetchTags: AsyncCallback[TagsResult] =
        if (lib.tags.contains(Tag.App))
          AsyncCallback.pure(new js.Array)
        else
          githubApi[TagsResult](s"https://api.github.com/repos/japgolly/${lib.repoName}/tags")

      (fetchRepo.attempt).zip(fetchTags.attempt).map { case (repo, tags) =>
        LibMeta(
          desc      = repo.toOption.flatMap(r => Option(r.description)),
          latestVer = tags.toOption.flatMap(_.iterator.flatMap(parseTag).nextOption()),
        )
      }
    }

    AsyncCallback.traverse(manifest.libs.toList)(l => fetch(l).map((l.id, _)))
      .map(m => new Metadata(m.toMap))
  }
}
