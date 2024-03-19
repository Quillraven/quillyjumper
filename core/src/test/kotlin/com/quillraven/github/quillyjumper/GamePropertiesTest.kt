package com.quillraven.github.quillyjumper

import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.set
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

class GamePropertiesTest {

    @Test
    fun testGameProperties() {
        val testMap = ObjectMap<String, String>().apply {
            set("soundVolume", "0.7")
            set("musicVolume", "0")
            set("debugPhysic", "true")
            set("enableProfiling", "false")
            set("logLevel", "3")
        }

        val gameProperties = testMap.toGameProperties()

        assertAll(
            // verify access of keys which are present
            { assertEquals(0.7f, gameProperties.soundVolume) },
            { assertEquals(0f, gameProperties.musicVolume) },
            { assertEquals(true, gameProperties.debugPhysic) },
            { assertEquals(false, gameProperties.enableProfiling) },
            { assertEquals(3, gameProperties.logLevel) },
        )
    }

}
