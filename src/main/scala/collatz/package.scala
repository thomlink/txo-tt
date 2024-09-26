package object collatz {
  sealed trait CollatzMachineControllerError extends Throwable {
    def display: String
  }

  object CollatzMachineControllerError {
    case class MachineNotFound(id: MachineId) extends CollatzMachineControllerError {
      override def display: String = s"Machine with id: ${id.value} not found"
    }
  }
}
