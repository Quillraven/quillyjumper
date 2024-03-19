package com.quillraven.github.quillyjumper.util

import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.set
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionUtilsTest {

    @Test
    fun testGetProperty() {
        val testMap = ObjectMap<String, String>().apply {
            set("intKey1", "1")
            set("intKey2", "-1")
            set("floatKey1", "1.1")
            set("floatKey2", "-1.1")
            set("booleanKey1", "true")
            set("booleanKey2", "false")
            set("strKey", "string")
        }

        assertAll(
            // verify access of keys which are present
            { assertEquals(1, testMap.getOrDefault("intKey1", 0)) },
            { assertEquals(-1, testMap.getOrDefault("intKey2", 0)) },
            { assertEquals(1.1f, testMap.getOrDefault("floatKey1", 0f)) },
            { assertEquals(-1.1f, testMap.getOrDefault("floatKey2", 0f)) },
            { assertEquals(true, testMap.getOrDefault("booleanKey1", false)) },
            { assertEquals(false, testMap.getOrDefault("booleanKey2", true)) },
            { assertEquals("string", testMap.getOrDefault("strKey", "")) },
            // verify access of key which is not present
            { assertEquals("defaultValue", testMap.getOrDefault("invalidKey", "defaultValue")) }
        )
    }

}
