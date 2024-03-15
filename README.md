[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-red.svg)](http://kotlinlang.org/)
[![Fleks](https://img.shields.io/badge/Fleks-2.7--SNAPSHOT-purple.svg)](http://kotlinlang.org/)

[![LibGDX](https://img.shields.io/badge/LibGDX-1.12.1-green.svg)](http://kotlinlang.org/)
[![LibKTX](https://img.shields.io/badge/LibKTX-1.12.1--rc1-blue.svg)](http://kotlinlang.org/)


# Quilly Jumper

TBD

### Credits

- [Pixel Adventure assets](https://pixelfrog-assets.itch.io/pixel-adventure-1)
- [Tile extruder](https://github.com/sporadic-labs/tile-extruder): to add padding to tileset without changing the order
  of tiles

  ```tile-extruder --tileWidth 16 --tileHeight 16 --extrusion 2 --input ./TILESET.png --output ./TILESET_EXRUDED.png```
- [ImageMagick](https://imagemagick.org/index.php): to split a sprite sheet into separate sprites

   ```magick '.\SHEET.png' -crop 32x32 'FRAME_%02d.png'```
- [LibGDX](https://github.com/libgdx/libgdx)
- [LibKTX](https://github.com/libktx/ktx): Kotlin extensions for LibGDX
