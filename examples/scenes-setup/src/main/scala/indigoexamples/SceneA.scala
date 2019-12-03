package indigoexamples

import indigo._
import indigoexts.lenses._
import indigoexts.scenes._
import indigoexts.subsystems.SubSystem

// There is no relevant entry in the ViewModel for either scene, so we've just left it as Unit.
object SceneA extends Scene[GameModel, Unit] {
  type SceneModel     = MessageA
  type SceneViewModel = Unit

  val name: SceneName = SceneName("A")

  val sceneModelLens: Lens[GameModel, MessageA] =
    Lens(
      model => model.sceneA,
      (model, newMessage) => model.copy(sceneA = newMessage)
    )

  // Nothing to do
  val sceneViewModelLens: Lens[Unit, Unit] =
    Lens.fixed(())

  val sceneSubSystems: Set[SubSystem] =
    Set(
      HelloSubSystem("Scene SubSystem A", FontStuff.fontKey)
    )

  // Nothing to do
  def updateSceneModel(gameTime: GameTime, sceneModel: MessageA, dice: Dice): GlobalEvent => Outcome[MessageA] =
    _ => Outcome(sceneModel)

  // Nothing to do
  def updateSceneViewModel(gameTime: GameTime, sceneModel: MessageA, sceneViewModel: Unit, frameInputEvents: FrameInputEvents, dice: Dice): Outcome[Unit] = Outcome(())

  // Show some text
  // When the user clicks anywhere in the screen, trigger an event to jump to the other scene.
  def updateSceneView(gameTime: GameTime, sceneModel: MessageA, sceneViewModel: Unit, frameInputEvents: FrameInputEvents): SceneUpdateFragment = {
    val events: List[GlobalEvent] =
      if (frameInputEvents.wasMouseClickedWithin(Rectangle(0, 0, 550, 400))) List(SceneEvent.JumpTo(SceneB.name))
      else Nil

    val text: Text = Text(sceneModel.value, 20, 20, 1, FontStuff.fontKey)

    SceneUpdateFragment.empty
      .addGameLayerNodes(text)
      .addGlobalEvents(events)
  }

}

final case class MessageA(value: String)