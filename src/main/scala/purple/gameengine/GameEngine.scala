package purple.gameengine

import org.scalajs.dom
import purple.renderer._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.JSApp

case class GameTime(running: Double, delta: Double)

/*
There are some questions over time.
Events are collected during a frame and then all processed sequentially at
the beginning of the next frame so:
1. Do you want the real time that the event happened?
2. The time since the beginning of the frame (since they are sequential a
long update could have a knock on effect for later events?
3. The frame time - which is what you currently get.
 */
//case class UpdateEvent(event: GameEvent, time: GameTime)

trait GameEngine[GameModel] extends JSApp {

  def config: GameConfig

  def imageAssets: Set[ImageAsset]

  def initialModel: GameModel

  def updateModel(gameTime: GameTime, state: GameModel): GameEvent => GameModel

  def updateView(currentState: GameModel): SceneGraphNode

  private var state: Option[GameModel] = None

  def main(): Unit = {

    AssetManager.loadAssets(imageAssets.toList).foreach { loadedImageAssets =>

      val canvas = Renderer.createCanvas(config.viewport.width, config.viewport.height)

      WorldEvents(canvas)

      val renderer: Renderer = Renderer(
        RendererConfig(
          viewport = Viewport(config.viewport.width, config.viewport.height),
          clearColor = config.clearColor,
          magnification = config.magnification
        ),
        loadedImageAssets,
        canvas
      )

      dom.window.requestAnimationFrame(loop(renderer, 0))
    }

  }

  private def processUpdateEvents(previousState: GameModel, gameTime: GameTime, remaining: List[GameEvent]): GameModel = {
    remaining match {
      case Nil =>
        updateModel(gameTime, previousState)(FrameTick)

      case x :: xs =>
        processUpdateEvents(updateModel(gameTime, previousState)(x), gameTime, xs)
    }
  }

  private def loop(renderer: Renderer, lastUpdateTime: Double)(time: Double): Unit = {
    val timeDelta = time - lastUpdateTime

    if(timeDelta > config.frameRateDeltaMillis) {
      val model = state match {
        case None =>
          initialModel

        case Some(previousState) =>
          processUpdateEvents(previousState, GameTime(time, timeDelta), GlobalEventStream.collect)
      }

      state = Some(model)

      drawScene(renderer, model, updateView)

      dom.window.requestAnimationFrame(loop(renderer, time))
    } else {
      dom.window.requestAnimationFrame(loop(renderer, lastUpdateTime))
    }
  }

  private val leafToDisplayObject: SceneGraphNodeLeaf => DisplayObject = {
      case graphic: Graphic =>
        DisplayObject(
          x = graphic.bounds.position.x,
          y = graphic.bounds.position.y,
          z = -graphic.depth.zIndex,
          width = graphic.bounds.size.x,
          height = graphic.bounds.size.y,
          imageRef = graphic.imageAssetRef,
          alpha = graphic.effects.alpha,
          tintR = graphic.effects.tint.r,
          tintG = graphic.effects.tint.g,
          tintB = graphic.effects.tint.b,
          flipHorizontal = graphic.effects.flip.horizontal,
          flipVertical = graphic.effects.flip.vertical,
          frame = SpriteSheetFrame.defaultOffset
        )

      case sprite: Sprite =>
        DisplayObject(
          x = sprite.bounds.position.x,
          y = sprite.bounds.position.y,
          z = -sprite.depth.zIndex,
          width = sprite.bounds.size.x,
          height = sprite.bounds.size.y,
          imageRef = sprite.imageAssetRef,
          alpha = sprite.effects.alpha,
          tintR = sprite.effects.tint.r,
          tintG = sprite.effects.tint.g,
          tintB = sprite.effects.tint.b,
          flipHorizontal = sprite.effects.flip.horizontal,
          flipVertical = sprite.effects.flip.vertical,
          frame = SpriteSheetFrame.calculateFrameOffset(
            imageSize = Vector2(sprite.animations.spriteSheetSize.x, sprite.animations.spriteSheetSize.y),
            frameSize = Vector2(sprite.animations.currentFrame.bounds.size.x, sprite.animations.currentFrame.bounds.size.y),
            framePosition = Vector2(sprite.animations.currentFrame.bounds.position.x, sprite.animations.currentFrame.bounds.position.y)
          )
        )
    }

  private def drawScene(renderer: Renderer, gameModel: GameModel, update: GameModel => SceneGraphNode): Unit = {
    val sceneGraph: SceneGraphNode = update(gameModel)

    val displayObjects: List[DisplayObject] =
      sceneGraph
        .flatten(Nil)
        .map(leafToDisplayObject)
        .sortBy(d => d.imageRef)

    renderer.drawScene(displayObjects)
  }

}

case class GameConfig(viewport: GameViewport, frameRate: Int, clearColor: ClearColor, magnification: Int) {
  val frameRateDeltaMillis: Int = 1000 / frameRate
}

case class GameViewport(width: Int, height: Int)


