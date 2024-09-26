package collatz

import cats.MonadThrow
import cats.effect.kernel.Ref
import cats.syntax.all._

sealed trait MachineStorage[F[_]] {
  def get(machineId: MachineId): F[Option[RunningMachine[F]]]
  def add(machineId: MachineId, machine: RunningMachine[F]): F[Unit]
  def remove(machineId: MachineId): F[Unit]
  def getAllMachines: F[Map[MachineId, RunningMachine[F]]]
}

class RefMachineStorage[F[_]:MonadThrow](machines: Ref[F, Map[MachineId, RunningMachine[F]]]) extends MachineStorage[F] {
  override def get(machineId: MachineId): F[Option[RunningMachine[F]]] = {
    machines.get.map(_.get(machineId))
  }

  override def add(machineId: MachineId, machine: RunningMachine[F]): F[Unit] =
    machines.update(_ + (machineId -> machine))

  override def getAllMachines: F[Map[MachineId, RunningMachine[F]]] = machines.get

  override def remove(machineId: MachineId): F[Unit] =
    machines.update(_.removed(machineId))
}
