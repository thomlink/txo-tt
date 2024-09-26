package http

import cats.MonadThrow
import cats.effect.kernel.Temporal
import cats.syntax.all._
import collatz.{CollatzMachineController, IncrementAmount, MachineId, StartNumber}
import fs2.Stream
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, ServerSentEvent}

import scala.concurrent.duration.DurationInt


object Routes {
  def routes[F[_]:  Temporal : MonadThrow](controller: CollatzMachineController[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._

    HttpRoutes.of[F] {
      case POST -> Root / "create" / id / IntVar(startNumber) =>
        controller.createAndStartMachine(MachineId(id), StartNumber(startNumber))
          .flatMap(_ => Created(s"Machine with id $id created."))
      case POST -> Root / "destroy" / id =>
        controller.destroyMachine(MachineId(id))
          .flatMap(_ => Ok(s"Machine with id $id destroyed."))
      case POST -> Root / "increment" / id / IntVar(increment) =>
        controller.incrementMachine(MachineId(id), IncrementAmount(increment))
          .flatMap(_ => Ok(s"Machine with id $id incremented by $increment."))
      case GET -> Root / "messages"   => {
        val messages = Stream.awakeEvery[F](1.second)
          .evalMap(_ => controller.getAllMachineStates)
          .flatMap(Stream.emits(_))
          .map(state => ServerSentEvent(Option(state.toString)))
        Ok(messages)
      }
    }
  }
}



