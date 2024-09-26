import cats.effect._
import cats.effect.kernel.Ref
import collatz._
import com.comcast.ip4s.IpLiteralSyntax
import http.{HttpErrorMiddleware, Routes}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats._
import org.typelevel.log4cats.slf4j.Slf4jFactory
object main extends IOApp {


  override def run(args: List[String]): IO[ExitCode] = {
    implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]
    implicit val logger: SelfAwareStructuredLogger[IO] = LoggerFactory[IO].getLogger

    for {
      machinesRef <- Ref.of[IO, Map[MachineId, RunningMachine[IO]]](Map.empty)
      machineStorage = new RefMachineStorage[IO](machinesRef)
      controller = new CollatzMachineController[IO](machineStorage)
      routes = Routes.routes(controller)

      _ <- Server.serve(routes, HttpErrorMiddleware.handle[IO])
        .useForever

    } yield ExitCode.Success

  }

}





object Server {

  def serve[F[_]: Async: LoggerFactory](
                                         routes: HttpRoutes[F],
                                         errorHandler: PartialFunction[Throwable, F[Response[F]]]
                                       ): Resource[F, Server] =
    EmberServerBuilder.default
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withErrorHandler(errorHandler)
      .withHttpApp(routes.orNotFound)
      .build


}
