package indigoexamples

import indigo._
import indigoextras.subsystems._

import scala.scalajs.js.annotation._

@JSExportTopLevel("IndigoGame")
object InputMappingExample extends IndigoDemo[Int, Int, GameModel, Int] {

  import FontStuff._

  def boot(flags: Map[String, String]): BootResult[Int] = {
    val config =
      defaultGameConfig
        .withClearColor(ClearColor.fromHexString("0xAA3399"))
        .withMagnification(1)

    BootResult(
      config,
      config.viewport.height
    ).withAssets(AssetType.Image(fontName, AssetPath("assets/boxy_font.png")))
      .withFonts(fontInfo)
      .withSubSystems(
        InputMapper.subsystem(
          KeyboardEvent.KeyDown(Keys.UP_ARROW) -> List(Up)
        )
      )
  }

  def setup(bootData: Int, assetCollection: AssetCollection, dice: Dice): Startup[StartupErrors, Int] =
    Startup.Success(bootData)

  def initialModel(startupData: Int): GameModel =
    GameModel(None)

  def initialViewModel(startupData: Int, model: GameModel): Int =
    startupData

  def updateModel(context: FrameContext, model: GameModel): GlobalEvent => Outcome[GameModel] = {
    case KeyboardEvent.KeyDown(Keys.KEY_A) =>
      println("Added Down Arrow input mapping")
      Outcome(model).addGlobalEvents(
        InputMapperEvent.AddMappings(KeyboardEvent.KeyDown(Keys.DOWN_ARROW) -> List(Down))
      )

    case KeyboardEvent.KeyDown(Keys.KEY_D) =>
      println("Removed Down Arrow input mapping")
      Outcome(model).addGlobalEvents(
        InputMapperEvent.RemoveMappings(KeyboardEvent.KeyDown(Keys.DOWN_ARROW))
      )

    case Up =>
      println("Up action")
      Outcome(GameModel(Some(Up)))

    case Down =>
      println("Down action")
      Outcome(GameModel(Some(Down)))

    case _ =>
      Outcome(model)
  }

  def updateViewModel(context: FrameContext, model: GameModel, viewModel: Int): Outcome[Int] =
    Outcome(viewModel)

  def present(context: FrameContext, model: GameModel, viewModel: Int): SceneUpdateFragment =
    SceneUpdateFragment.empty
      .addGameLayerNodes(
        Text("Press up arrow, or\npress a to add or\nd to remove a mapping\nfor down, then you can\npress the down arrow.", 10, 20, 1, fontKey),
        Text("Action is " + model.action.map(_.asString).getOrElse("None"), 10, viewModel - 30, 1, fontKey)
      )

}

final case class GameModel(action: Option[Action])

sealed trait Action extends GlobalEvent {
  def asString: String =
    this match {
      case Up   => "Up"
      case Down => "Down"
    }
}
final case object Up   extends Action
final case object Down extends Action

object FontStuff {

  val fontName: AssetName = AssetName("My boxy font")

  def fontKey: FontKey = FontKey("My Font")

  def fontInfo: FontInfo =
    FontInfo(fontKey, Material.Textured(fontName), 320, 230, FontChar("?", 93, 52, 23, 23))
      .addChar(FontChar("A", 3, 78, 23, 23))
      .addChar(FontChar("B", 26, 78, 23, 23))
      .addChar(FontChar("C", 50, 78, 23, 23))
      .addChar(FontChar("D", 73, 78, 23, 23))
      .addChar(FontChar("E", 96, 78, 23, 23))
      .addChar(FontChar("F", 119, 78, 23, 23))
      .addChar(FontChar("G", 142, 78, 23, 23))
      .addChar(FontChar("H", 165, 78, 23, 23))
      .addChar(FontChar("I", 188, 78, 15, 23))
      .addChar(FontChar("J", 202, 78, 23, 23))
      .addChar(FontChar("K", 225, 78, 23, 23))
      .addChar(FontChar("L", 248, 78, 23, 23))
      .addChar(FontChar("M", 271, 78, 23, 23))
      .addChar(FontChar("N", 3, 104, 23, 23))
      .addChar(FontChar("O", 29, 104, 23, 23))
      .addChar(FontChar("P", 54, 104, 23, 23))
      .addChar(FontChar("Q", 75, 104, 23, 23))
      .addChar(FontChar("R", 101, 104, 23, 23))
      .addChar(FontChar("S", 124, 104, 23, 23))
      .addChar(FontChar("T", 148, 104, 23, 23))
      .addChar(FontChar("U", 173, 104, 23, 23))
      .addChar(FontChar("V", 197, 104, 23, 23))
      .addChar(FontChar("W", 220, 104, 23, 23))
      .addChar(FontChar("X", 248, 104, 23, 23))
      .addChar(FontChar("Y", 271, 104, 23, 23))
      .addChar(FontChar("Z", 297, 104, 23, 23))
      .addChar(FontChar("0", 3, 26, 23, 23))
      .addChar(FontChar("1", 26, 26, 15, 23))
      .addChar(FontChar("2", 41, 26, 23, 23))
      .addChar(FontChar("3", 64, 26, 23, 23))
      .addChar(FontChar("4", 87, 26, 23, 23))
      .addChar(FontChar("5", 110, 26, 23, 23))
      .addChar(FontChar("6", 133, 26, 23, 23))
      .addChar(FontChar("7", 156, 26, 23, 23))
      .addChar(FontChar("8", 179, 26, 23, 23))
      .addChar(FontChar("9", 202, 26, 23, 23))
      .addChar(FontChar("?", 93, 52, 23, 23))
      .addChar(FontChar("!", 3, 0, 15, 23))
      .addChar(FontChar(".", 286, 0, 15, 23))
      .addChar(FontChar(",", 248, 0, 15, 23))
      .addChar(FontChar(" ", 145, 52, 23, 23))
}
