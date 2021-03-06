package indigoextras.formats

import indigo.shared.events.GlobalEvent
import indigo.shared.scenegraph._
import indigo.shared.animation._
import indigo.shared.datatypes._
import indigo.shared.IndigoLogger
import indigo.shared.collections.NonEmptyList
import indigo.shared.AsString
import indigo.shared.formats.{Aseprite, AsepriteFrameTag, AsepriteFrame}
import indigo.shared.assets.AssetName
import indigo.shared.time.Millis
import indigo.shared.dice.Dice

final case class SpriteAndAnimations(sprite: Sprite, animations: Animation)
object AsepriteConverter {

  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  def toSpriteAndAnimations(dice: Dice, aseprite: Aseprite, depth: Depth, assetName: AssetName): Option[SpriteAndAnimations] =
    extractCycles(aseprite) match {
      case Nil =>
        IndigoLogger.info("No animation frames found in Aseprite")
        None
      case x :: xs =>
        val animations: Animation =
          Animation(
            animationKey = AnimationKey.fromDice(dice),
            material = Material.Textured(assetName),
            currentCycleLabel = x.label,
            cycles = NonEmptyList.pure(x, xs)
          )
        Option(
          SpriteAndAnimations(
            Sprite(
              bindingKey = BindingKey.fromDice(dice),
              position = Point(0, 0),
              depth = depth,
              rotation = Radians.zero,
              scale = Vector2.one,
              animationKey = animations.animationKey,
              ref = Point(0, 0),
              effects = Effects.default,
              eventHandler = (_: (Rectangle, GlobalEvent)) => Nil
            ),
            animations
          )
        )
    }

  def extractCycles(aseprite: Aseprite): List[Cycle] =
    aseprite.meta.frameTags
      .map { frameTag =>
        extractFrames(frameTag, aseprite.frames) match {
          case Nil =>
            IndigoLogger.info("Failed to extract cycle with frameTag: " + implicitly[AsString[AsepriteFrameTag]].show(frameTag))
            None
          case x :: xs =>
            Option(
              Cycle.create(frameTag.name, NonEmptyList.pure(x, xs))
            )
        }
      }
      .collect { case Some(s) => s }

  private def extractFrames(frameTag: AsepriteFrameTag, asepriteFrames: List[AsepriteFrame]): List[Frame] =
    asepriteFrames.slice(frameTag.from, frameTag.to + 1).map { aseFrame =>
      Frame(
        crop = Rectangle(
          position = Point(aseFrame.frame.x, aseFrame.frame.y),
          size = Point(aseFrame.frame.w, aseFrame.frame.h)
        ),
        duration = Millis(aseFrame.duration.toLong)
      )
    }

}
