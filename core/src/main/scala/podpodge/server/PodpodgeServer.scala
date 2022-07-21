package podpodge.server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import podpodge.config.Config
import podpodge.http.Sttp
import podpodge.types.EpisodeId
import podpodge.{CreateEpisodeRequest, DownloadWorker, Env, StaticConfig, config}
import zio._

import java.io.File
import javax.sql.DataSource

object PodpodgeServer {
  def make: ZIO[Scope with Env, Throwable, Http.ServerBinding] = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "podpodge-system")

    for {
      _                   <- StaticConfig.ensureDirectoriesExist
      config              <- config.get
      downloadQueue       <- Queue.unbounded[CreateEpisodeRequest]
      _                   <- DownloadWorker.make(downloadQueue).forkDaemon
      episodesDownloading <- Ref.Synchronized.make(Map.empty[EpisodeId, Promise[Throwable, File]])
      runtime <- ZIO.runtime[Env]
      server              <- ZIO.acquireRelease(
                               ZIO.fromFuture { _ =>
                                 Http()
                                   .newServerAt(config.serverHost.unwrap, config.serverPort.unwrap)
                                   .bind(Routes.make(downloadQueue, episodesDownloading)(runtime))
                               } <* ZIO.logInfo(s"Starting server at ${config.baseUri}")
                             )(server =>
                               (for {
                                 _ <- ZIO.logInfo("Shutting down server")
                                 _ <- ZIO.fromFuture(_ => server.unbind())
                                 _ <- ZIO.attempt(system.terminate())
                                 _ <- ZIO.fromFuture(_ => system.whenTerminated)
                               } yield ()).orDie
                             )
    } yield server
  }
}
