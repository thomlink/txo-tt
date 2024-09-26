package it


import cats.effect.{IO, Ref}
import collatz.CollatzMachineControllerError.MachineNotFound
import collatz._
import munit.CatsEffectSuite
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.{LoggerFactory, SelfAwareStructuredLogger}

import scala.concurrent.duration.DurationInt


class CollatzMachineControllerSpec extends CatsEffectSuite {

  // Helper to create an instance of MachineManagerImpl for each test
  def createController: IO[CollatzMachineController[IO]] = {
    implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]
    implicit val logger: SelfAwareStructuredLogger[IO] = LoggerFactory[IO].getLogger

    for {
      machinesRef <- Ref.of[IO, Map[MachineId, RunningMachine[IO]]](Map.empty)
      machineStorage = new RefMachineStorage[IO](machinesRef)
    } yield new CollatzMachineController[IO](machineStorage)
  }

  val machineId = MachineId("test-machine")
  val startNumber = StartNumber(12)


  test("create a machine and observe state updates") {
    createController.flatMap { controller =>
      for {
        _ <- controller.createAndStartMachine(machineId, startNumber) // Start at 12
        _ <- IO.sleep(2.seconds) // Let the machine update its state at least twice
        currentValue <- controller.getMachineState(machineId).map(_.map(_.current))
      } yield assertEquals(currentValue, Some(CurrentValue(3))) // The Collatz of 12 -> 6
    }
  }

  test("increment a machine's state and observe new values") {
    val incrementAmount = IncrementAmount(10)

    createController.flatMap { controller =>
      for {
        _ <- controller.createAndStartMachine(machineId, startNumber) // Start at 12
        _ <- IO.sleep(1.second) // Let it run for one cycle
        _ <- controller.incrementMachine(machineId, IncrementAmount(10)) // Increment by 10, should be 12 + 10 = 22
        _ <- IO.sleep(1.second) // Let the state update again
        currentValue <- controller.getMachineState(machineId).map(_.map(_.current))
      } yield assertEquals(currentValue, Some(CurrentValue(8)))
    }
  }

  test("destroy a machine and ensure it stops updating") {
    createController.flatMap { controller =>
      for {
        _ <-  controller.createAndStartMachine(machineId, startNumber) // Start at 12
        _ <- IO.sleep(1.second) // Allow it to run once
        _ <- controller.destroyMachine(machineId) // Destroy the machine
        _ <- IO.sleep(2.seconds) // Wait longer than usual, check if it's still running
        currentState <- controller.getMachineState(machineId)
      } yield assertEquals(currentState, None) // State should not exist anymore
    }
  }

  test("destroy a machine fails if machine doesn't exist") {
    createController.flatMap { controller =>
     for {
        result <- controller.destroyMachine(machineId).attempt // Destroy the machine
      } yield (assertEquals(result, Left(MachineNotFound(machineId))))
    }
  }



  test("concurrent creation and destruction of machines") {
    createController.flatMap { controller =>
      for {
        _ <- controller.createAndStartMachine(MachineId("machine-1"), StartNumber(5))
        _ <- controller.createAndStartMachine(MachineId("machine-2"), StartNumber(10))
        _ <- controller.destroyMachine(MachineId("machine-1"))


        state1 <- controller.getMachineState(MachineId("machine-1"))
        state2 <- controller.getMachineState(MachineId("machine-2"))
      } yield {
        assert(state1.isEmpty) // Machine 1 should be destroyed
        assert(state2.isDefined) // Machine 2 should still be running
      }
    }
  }

  test("get all machine states") {
    val expectedStates = List(
      CollatzMachineState(MachineId("machine-1"), current = CurrentValue(2), sequence = SequenceValue(4), start = StartNumber(5)),
      CollatzMachineState(MachineId("machine-2"), current = CurrentValue(2), sequence = SequenceValue(4), start = StartNumber(5))
    )

    createController.flatMap { controller =>
      for {
        _ <- controller.createAndStartMachine(MachineId("machine-1"), StartNumber(5))
        _ <- controller.createAndStartMachine(MachineId("machine-2"), StartNumber(5))
        _ <- IO.sleep(5.seconds)

        states <- controller.getAllMachineStates
      } yield {
        assertEquals(expectedStates, states)
      }
    }

  }

}
