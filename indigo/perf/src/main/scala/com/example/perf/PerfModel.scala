package com.example.perf

import indigo._

object PerfModel {

  def initialModel(startupData: MyStartupData): MyGameModel =
    MyGameModel(
      DudeModel(startupData.dude, DudeIdle)
    )

  def updateModel(state: MyGameModel): GlobalEvent => Outcome[MyGameModel] = {
    case FrameTick =>
      Outcome(state)

    case KeyboardEvent.KeyDown(Keys.LEFT_ARROW) =>
      Outcome(state.copy(dude = state.dude.walkLeft))

    case KeyboardEvent.KeyDown(Keys.RIGHT_ARROW) =>
      Outcome(state.copy(dude = state.dude.walkRight))

    case KeyboardEvent.KeyDown(Keys.UP_ARROW) =>
      Outcome(state.copy(dude = state.dude.walkUp))

    case KeyboardEvent.KeyDown(Keys.DOWN_ARROW) =>
      Outcome(state.copy(dude = state.dude.walkDown))

    case KeyboardEvent.KeyUp(_) =>
      Outcome(state.copy(dude = state.dude.idle))

    case _ =>
      //Logger.info(e)
      Outcome(state)
  }

}

final case class MyGameModel(dude: DudeModel)

final case class DudeModel(dude: Dude, walkDirection: DudeDirection) {
  def idle: DudeModel      = this.copy(walkDirection = DudeIdle)
  def walkLeft: DudeModel  = this.copy(walkDirection = DudeLeft)
  def walkRight: DudeModel = this.copy(walkDirection = DudeRight)
  def walkUp: DudeModel    = this.copy(walkDirection = DudeUp)
  def walkDown: DudeModel  = this.copy(walkDirection = DudeDown)
}

sealed trait DudeDirection {
  val cycleName: CycleLabel
}
case object DudeIdle  extends DudeDirection { val cycleName: CycleLabel = CycleLabel("blink")      }
case object DudeLeft  extends DudeDirection { val cycleName: CycleLabel = CycleLabel("walk left")  }
case object DudeRight extends DudeDirection { val cycleName: CycleLabel = CycleLabel("walk right") }
case object DudeUp    extends DudeDirection { val cycleName: CycleLabel = CycleLabel("walk up")    }
case object DudeDown  extends DudeDirection { val cycleName: CycleLabel = CycleLabel("walk down")  }
