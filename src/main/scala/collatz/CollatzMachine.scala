package collatz

import cats.effect.kernel.{Ref, Temporal}
import cats.syntax.all._

import scala.concurrent.duration.DurationInt

case class CollatzMachine[F[_]](state: Ref[F, CollatzMachineState])

object CollatzMachine {
  def start[F[_] : Temporal](machine: CollatzMachine[F]): F[Unit] =
    Temporal[F].foreverM(updateState(machine))

  private def updateState[F[_] : Temporal](machine: CollatzMachine[F]): F[Unit] =
    for {
      _ <- Temporal[F].sleep(1.second)
      _ <- machine.state.update(CollatzMachineState.getNextState)
    } yield ()

  def create[F[_] : Temporal](id: MachineId, startNumber: StartNumber): F[CollatzMachine[F]] =
    Ref.of[F, CollatzMachineState](
      CollatzMachineState(id, CurrentValue(startNumber.value), SequenceValue(0), startNumber)
    ).map(CollatzMachine(_))

}