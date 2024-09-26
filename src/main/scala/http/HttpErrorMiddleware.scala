package http

import cats.Applicative
import collatz.CollatzMachineControllerError
import org.http4s.Response

object HttpErrorMiddleware {

  def handle[F[_]: Applicative]: PartialFunction[Throwable, F[Response[F]]] = { e =>
    object dsl extends org.http4s.dsl.Http4sDsl[F]
    import dsl._

    e match {
      case controllerError: CollatzMachineControllerError =>
        controllerError match {
          case notFound @ CollatzMachineControllerError.MachineNotFound(_) =>
            NotFound(notFound.display)
        }
      case unhandled => InternalServerError(unhandled.toString)
    }
  }
}
