package indigo.json

import indigo.shared.formats._
import utest._
import indigo.shared.datatypes.FontChar

object JsonTests extends TestSuite {

  val tests: Tests =
    Tests {
      "Create an Aseprite asset" - {

        "should be able to parse the json definition" - {
          Json.asepriteFromJson(AsepriteSampleData.json) ==> AsepriteSampleData.aseprite
        }

        "should fail to parse a bad json definition" - {
          Json.asepriteFromJson("nonsense") ==> None
        }

      }

      "Fonts" - {
        "should be able to parse the json definition" - {
          val actual = Json.readFontToolJson(FontToolSampleData.json).get

          val expected = FontToolSampleData.sample

          actual.length ==> expected.length
          actual.forall(c => expected.contains(c)) ==> true
        }

        "should fail to parse a bad json definition" - {
          Json.readFontToolJson("nonsense") ==> None
        }
      }
    }

}

object FontToolSampleData {

  val json: String =
    """
{
    "name": "fontname",
    "size": 16,
    "padding": 1,
    "glyphs": [
        {
            "unicode": 100,
            "char": "d",
            "x": 1,
            "y": 1,
            "w": 10,
            "h": 25
        },
        {
            "unicode": 98,
            "char": "b",
            "x": 13,
            "y": 1,
            "w": 10,
            "h": 25
        },
        {
            "unicode": 99,
            "char": "c",
            "x": 25,
            "y": 1,
            "w": 9,
            "h": 25
        },
        {
            "unicode": 97,
            "char": "a",
            "x": 36,
            "y": 1,
            "w": 10,
            "h": 25
        }
    ]
}
    """

  val sample: List[FontChar] =
    List(
      FontChar("a", 36, 1, 10, 25),
      FontChar("b", 13, 1, 10, 25),
      FontChar("c", 25, 1, 9, 25),
      FontChar("d", 1, 1, 10, 25)
    )

}

object AsepriteSampleData {

  val json: String =
    """
      |{ "frames": [
      |   {
      |    "filename": "trafficlights 0.ase",
      |    "frame": { "x": 0, "y": 0, "w": 64, "h": 64 },
      |    "rotated": false,
      |    "trimmed": false,
      |    "spriteSourceSize": { "x": 0, "y": 0, "w": 64, "h": 64 },
      |    "sourceSize": { "w": 64, "h": 64 },
      |    "duration": 100
      |   },
      |   {
      |    "filename": "trafficlights 1.ase",
      |    "frame": { "x": 64, "y": 0, "w": 64, "h": 64 },
      |    "rotated": false,
      |    "trimmed": false,
      |    "spriteSourceSize": { "x": 0, "y": 0, "w": 64, "h": 64 },
      |    "sourceSize": { "w": 64, "h": 64 },
      |    "duration": 100
      |   },
      |   {
      |    "filename": "trafficlights 2.ase",
      |    "frame": { "x": 0, "y": 64, "w": 64, "h": 64 },
      |    "rotated": false,
      |    "trimmed": false,
      |    "spriteSourceSize": { "x": 0, "y": 0, "w": 64, "h": 64 },
      |    "sourceSize": { "w": 64, "h": 64 },
      |    "duration": 100
      |   }
      | ],
      | "meta": {
      |  "app": "http://www.aseprite.org/",
      |  "version": "1.1.13",
      |  "image": "/Users/dave/repos/indigo/sandbox/trafficlights.png",
      |  "format": "RGBA8888",
      |  "size": { "w": 128, "h": 128 },
      |  "scale": "1",
      |  "frameTags": [
      |   { "name": "lights", "from": 0, "to": 2, "direction": "forward" }
      |  ]
      | }
      |}
    """.stripMargin

  val aseprite: Option[Aseprite] = Option {
    Aseprite(
      frames = List(
        AsepriteFrame(
          filename = "trafficlights 0.ase",
          frame = AsepriteRectangle(0, 0, 64, 64),
          rotated = false,
          trimmed = false,
          spriteSourceSize = AsepriteRectangle(0, 0, 64, 64),
          sourceSize = AsepriteSize(64, 64),
          duration = 100
        ),
        AsepriteFrame(
          filename = "trafficlights 1.ase",
          frame = AsepriteRectangle(64, 0, 64, 64),
          rotated = false,
          trimmed = false,
          spriteSourceSize = AsepriteRectangle(0, 0, 64, 64),
          sourceSize = AsepriteSize(64, 64),
          duration = 100
        ),
        AsepriteFrame(
          filename = "trafficlights 2.ase",
          frame = AsepriteRectangle(0, 64, 64, 64),
          rotated = false,
          trimmed = false,
          spriteSourceSize = AsepriteRectangle(0, 0, 64, 64),
          sourceSize = AsepriteSize(64, 64),
          duration = 100
        )
      ),
      meta = AsepriteMeta(
        app = "http://www.aseprite.org/",
        version = "1.1.13",
        image = "/Users/dave/repos/indigo/sandbox/trafficlights.png",
        format = "RGBA8888",
        size = AsepriteSize(128, 128),
        scale = "1",
        frameTags = List(
          AsepriteFrameTag(
            name = "lights",
            from = 0,
            to = 2,
            direction = "forward"
          )
        )
      )
    )
  }

}
