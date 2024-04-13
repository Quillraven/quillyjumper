package com.quillraven.github.quillyjumper.tiled

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.XmlReader

class TiledLoader(fileHandleResolver: FileHandleResolver) : TmxMapLoader(fileHandleResolver) {

    // since LibGDX does not load the "text" information of a Text object in Tiled, we need to overrule
    // LibGDX's loadObject method and parse the text info ourselves and add it to the properties of the mapObject
    override fun loadObject(map: TiledMap, objects: MapObjects, element: XmlReader.Element, heightInPixels: Float) {
        super.loadObject(map, objects, element, heightInPixels)
        val lastObject = objects.last()
        element.getChildByName("text")?.text?.let { objTxt ->
            lastObject.properties.put("text", objTxt)
        }
    }

}
