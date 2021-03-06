package indigo.shared.animation

import indigo.shared.time.GameTime
import indigo.shared.animation.AnimationAction._
import indigo.shared.collections.NonEmptyList
import indigo.shared.EqualTo
import indigo.shared.EqualTo._
import indigo.shared.AsString
import indigo.shared.AsString._
import indigo.shared.temporal.Signal
import indigo.shared.time.Millis

final class Cycle(val label: CycleLabel, val frames: NonEmptyList[Frame], val playheadPosition: Int, val lastFrameAdvance: Millis) {

  def addFrame(newFrame: Frame): Cycle =
    Cycle(label, frames :+ newFrame, playheadPosition, lastFrameAdvance)

  val frameCount: Int =
    frames.length

  def currentFrame: Frame =
    Cycle.currentFrame(this)

  def saveMemento: CycleMemento =
    Cycle.saveMemento(this)

  def applyMemento(memento: CycleMemento): Cycle =
    Cycle.applyMemento(this, memento)

  def runActions(gameTime: GameTime, actions: List[AnimationAction]): Cycle =
    Cycle.runActions(this, gameTime, actions)

  def updatePlayheadAndLastAdvance(playheadPosition: Int, lastFrameAdvance: Millis): Cycle =
    Cycle.updatePlayheadAndLastAdvance(this, playheadPosition, lastFrameAdvance)

}

object Cycle {

  implicit val cycleEqualTo: EqualTo[Cycle] =
    EqualTo.create { (a, b) =>
      a.label === b.label &&
      a.frames === b.frames &&
      a.playheadPosition === b.playheadPosition &&
      a.lastFrameAdvance === b.lastFrameAdvance
    }

  implicit val cycleAsString: AsString[Cycle] = {
    AsString.create { c =>
      s"Cycle(${c.label.show}, ${c.frames.show}, ${c.playheadPosition.show}, ${c.lastFrameAdvance.show})"
    }
  }

  def apply(label: CycleLabel, frames: NonEmptyList[Frame], playheadPosition: Int, lastFrameAdvance: Millis): Cycle =
    new Cycle(label, frames, playheadPosition, lastFrameAdvance)

  def create(label: String, frames: NonEmptyList[Frame]): Cycle =
    Cycle(CycleLabel(label), frames, 0, Millis.zero)

  def currentFrame(cycle: Cycle): Frame =
    cycle.frames.toList(cycle.playheadPosition % cycle.frameCount)

  def saveMemento(cycle: Cycle): CycleMemento =
    new CycleMemento(cycle.playheadPosition, cycle.lastFrameAdvance)

  def updatePlayheadAndLastAdvance(cycle: Cycle, playheadPosition: Int, lastFrameAdvance: Millis): Cycle =
    Cycle(cycle.label, cycle.frames, playheadPosition, lastFrameAdvance)

  def applyMemento(cycle: Cycle, memento: CycleMemento): Cycle =
    updatePlayheadAndLastAdvance(cycle, memento.playheadPosition, memento.lastFrameAdvance)

  def calculateNextPlayheadPosition(currentPosition: Int, frameDuration: Millis, frameCount: Int, lastFrameAdvance: Millis): Signal[CycleMemento] =
    Signal { t =>
      if (t.toMillis >= lastFrameAdvance + frameDuration) {
        val framestoAdvance = ((t.toMillis.value - lastFrameAdvance.value) / frameDuration.value).toInt
        CycleMemento((currentPosition + framestoAdvance) % frameCount, t.toMillis)
      } else {
        CycleMemento(currentPosition, lastFrameAdvance)
      }
    }

  def runActions(cycle: Cycle, gameTime: GameTime, actions: List[AnimationAction]): Cycle =
    actions.foldLeft(cycle) { (cycle, action) =>
      action match {
        case Play =>
          cycle.applyMemento(
            calculateNextPlayheadPosition(
              cycle.playheadPosition,
              cycle.currentFrame.duration,
              cycle.frameCount,
              cycle.lastFrameAdvance
            ).at(gameTime.running)
          )

        case ChangeCycle(_) =>
          cycle // No op, done at animation level.

        case JumpToFirstFrame =>
          updatePlayheadAndLastAdvance(cycle, 0, cycle.lastFrameAdvance)

        case JumpToLastFrame =>
          updatePlayheadAndLastAdvance(cycle, cycle.frameCount - 1, cycle.lastFrameAdvance)

        case JumpToFrame(number) =>
          updatePlayheadAndLastAdvance(cycle, if (number > cycle.frameCount - 1) cycle.frameCount - 1 else number, cycle.lastFrameAdvance)

      }
    }
}

final class CycleLabel(val value: String) extends AnyVal {
  override def toString(): String =
    s"CycleLabel($value)"
}
object CycleLabel {

  implicit val cycleLabelEqualTo: EqualTo[CycleLabel] =
    EqualTo.create { (a, b) =>
      a.value === b.value
    }

  implicit val cycleLabelAsString: AsString[CycleLabel] =
    AsString.create(l => s"CycleLabel(${l.value})")

  def apply(value: String): CycleLabel =
    new CycleLabel(value)
}

final class CycleMemento(val playheadPosition: Int, val lastFrameAdvance: Millis) {

  def asString: String =
    implicitly[AsString[CycleMemento]].show(this)

  override def toString: String =
    asString

  def ===(other: CycleMemento): Boolean =
    implicitly[EqualTo[CycleMemento]].equal(this, other)

  @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf", "org.wartremover.warts.AsInstanceOf"))
  override def equals(obj: Any): Boolean =
    if (obj.isInstanceOf[CycleMemento]) {
      this === obj.asInstanceOf[CycleMemento]
    } else false

}
object CycleMemento {

  implicit val cycleMementoEqualTo: EqualTo[CycleMemento] =
    EqualTo.create { (a, b) =>
      a.playheadPosition === b.playheadPosition && a.lastFrameAdvance === b.lastFrameAdvance
    }

  implicit val cycleMementoAsString: AsString[CycleMemento] =
    AsString.create(m => s"CycleMemento(${m.playheadPosition.show}, ${m.lastFrameAdvance.show})")

  def apply(playheadPosition: Int, lastFrameAdvance: Millis): CycleMemento =
    new CycleMemento(playheadPosition, lastFrameAdvance)
}
