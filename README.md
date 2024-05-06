[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-red.svg)](http://kotlinlang.org/)
[![Fleks](https://img.shields.io/badge/Fleks-2.7-purple.svg)](http://kotlinlang.org/)

[![LibGDX](https://img.shields.io/badge/LibGDX-1.12.1-green.svg)](http://kotlinlang.org/)
[![LibKTX](https://img.shields.io/badge/LibKTX-1.12.1--SNAPSHOT-blue.svg)](http://kotlinlang.org/)


# Quilly Jumper

Quilly Jumper is a game that was developed during a longer streaming session.
You can find the videos to all the different streams [here](https://www.youtube.com/watch?v=quIHNm4r1iI&list=PLTKHCDn5RKK8_Gjw8nh7RN7JVCISvV4sc).

It uses LibGDX with Kotlin and Box2D as physic engine. In addition, it was built around
Tiled map editor that serves as map editor but also as game object editor (properties and Box2D body/fixture definition).

The controls are:
- A / D: move left / right
- SPACE: jump

It contains one collectable - a cherry. When it is picked up then the player automatically performs a double jump.

It also contains two different enemies:
- a saw that can optionally follow a hidden track
- a rock head that detects the player vertically / horizontally and will attack him when he gets to close

You have four lives to complete a stage. If you fall down a pit or lose all your lives then you respawn at the beginning
of the stage with full life. In total there are three stages:

![image](https://github.com/Quillraven/quillyjumper/assets/93260/170ea3ec-4980-4401-95e8-b0bd309165ea)
![image](https://github.com/Quillraven/quillyjumper/assets/93260/5de8f3ce-d84a-4903-a38c-4078f641a066)
![image](https://github.com/Quillraven/quillyjumper/assets/93260/07ed4952-27a8-48f9-996e-2edb3d8f17f9)
![image](https://github.com/Quillraven/quillyjumper/assets/93260/ec9c6e38-ef17-4877-970e-d1a4d5d42797)


### Credits

- [Pixel Adventure assets](https://pixelfrog-assets.itch.io/pixel-adventure-1)
- [Complete UI essential pack](https://crusenho.itch.io/complete-ui-essential-pack)
- [Pixel health bar](https://bdragon1727.itch.io/basic-pixel-health-bar-and-scroll-bar)
- [Masaaki Font](https://www.1001freefonts.com/masaaki.font)
- [Tile extruder](https://github.com/sporadic-labs/tile-extruder): to add padding to tileset without changing the order
  of tiles

  ```tile-extruder --tileWidth 16 --tileHeight 16 --extrusion 2 --input ./TILESET.png --output ./TILESET_EXRUDED.png```
- [ImageMagick](https://imagemagick.org/index.php): to split a sprite sheet into separate sprites

   ```magick '.\SHEET.png' -crop 32x32 'FRAME_%02d.png'```
- [Skin composer](https://github.com/raeleus/skin-composer)
- [LibGDX](https://github.com/libgdx/libgdx)
- [LibKTX](https://github.com/libktx/ktx): Kotlin extensions for LibGDX
- [SFXR](https://sfxr.me/): SFX creation tool
- [Creator Pack](https://jonathan-so.itch.io/creatorpack)
- [Beat'em Up Soundtrack](https://wyver9.itch.io/8-bit-beatem-up-soundtrack)
