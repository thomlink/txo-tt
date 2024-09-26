package collatz


case class MachineId(value: String)
case class StartNumber(value: Int)
case class IncrementAmount(value: Int)
case class CurrentValue(value: Int)
case class SequenceValue(value: Int)


case class CollatzMachineState(
                                id: MachineId,
                                current: CurrentValue,
                                sequence: SequenceValue,
                                start: StartNumber
                              )

object CollatzMachineState {
  def getNextState: CollatzMachineState => CollatzMachineState = { state =>
    CollatzMachineState(
      id = state.id,
      current = CurrentValue(step(state.current.value)),
      sequence = SequenceValue(state.sequence.value + 1),
      start = state.start
    )
  }

  def step: Int => Int = { n =>
    if (n % 2 == 0) // Is even
      n / 2
    else
      3 * n + 1
  }
}