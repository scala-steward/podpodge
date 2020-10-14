package podpodge.server

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ PathMatcher1, Route }
import podpodge.controllers.{ EpisodeController, PodcastController }
import podpodge.http.AkkaHttp._
import podpodge.types.{ EpisodeId, PodcastId }

// Routes that are using plain akka-http rather than through tapir's interface.
object RawRoutes {

  val PodcastIdPart: PathMatcher1[PodcastId] = LongNumber.map(PodcastId(_))
  val EpisodeIdPart: PathMatcher1[EpisodeId] = LongNumber.map(EpisodeId(_))

  val all: Route =
    pathSingleSlash {
      redirect("/docs", StatusCodes.TemporaryRedirect)
    } ~
      path("episode" / EpisodeIdPart / "file") { id =>
        get {
          withRangeSupport {
            EpisodeController.getEpisodeFile(id)
          }
        }
      } ~
      path("cover" / PodcastIdPart) { id =>
        get {
          PodcastController.getPodcastCover(id)
        }
      } ~
      path("thumbnail" / EpisodeIdPart) { id =>
        get {
          EpisodeController.getThumbnail(id)
        }
      }

}
