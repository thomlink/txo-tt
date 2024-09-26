package collatz

import cats.{Applicative, Functor, MonadThrow}
import cats.effect.implicits.genSpawnOps
import cats.effect.kernel.{Fiber, Ref, Temporal}
import cats.syntax.all._
import collatz.CollatzMachineControllerError.MachineNotFound
import fs2.Stream
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.DurationInt

case class RunningMachine[F[_]](machine: CollatzMachine[F], fiber: Fiber[F, Throwable, Unit])



/*
Given a machineId we need to be able to
- start a machine
- destroy a machine
- update a machine
 */


class CollatzMachineController[F[_] : Temporal : MonadThrow : Logger](
                                                                       machines: MachineStorage[F]
                                                                     ) {

  def getMachineState(id: MachineId): F[Option[CollatzMachineState]] = {
//    machines.get.flatMap(_.get(id).traverse(_.machine.state.get))
    for {
      state <- machines.get(id).flatMap(_.traverse(_.machine.state.get))
    } yield state
  }


  def createAndStartMachine(id: MachineId, startNumber: StartNumber): F[Unit] = {
    for {
      newMachine <- CollatzMachine.create(id, startNumber)
      fiber <- CollatzMachine.start(newMachine).start
      _ <- Logger[F].info("Created machine and started fiber")
      runningMachine = RunningMachine(newMachine, fiber)
      _ <- machines.add(id, runningMachine)
    } yield ()
  }

  def destroyMachine(id: MachineId): F[Unit] = for {
    currentMachines <- machines.getAllMachines
    runningMachine <- MonadThrow[F].fromOption(
      currentMachines.get(id), MachineNotFound(id)
    )
    _ <- runningMachine.fiber.cancel
    _ <- Logger[F].info("Cancelled fiber")
    _ <- machines.remove(id)
  } yield ()

  def incrementMachine(id: MachineId, amount: IncrementAmount): F[Unit] =
    for {
      maybeMachine <- machines.get(id)
      runningMachine <- MonadThrow[F].fromOption(
        maybeMachine, MachineNotFound(id)
      )
      _ <- runningMachine.machine.state.update(state => state.copy(current = CurrentValue(state.current.value + amount.value)))
      _ <- Logger[F].info("Incremented machine")
    } yield ()

  def getAllMachineStates: F[List[CollatzMachineState]] =
    for {
      currentMachines <- machines.getAllMachines
      states <- currentMachines.values.toList.traverse(_.machine.state.get)
    } yield states


}



