package com.quillraven.github.quillyjumper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import ktx.collections.GdxArray
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set

class GamePreferences(private val preferences: Preferences) {

    fun storeUnlockedMap(mapAsset: MapAsset) {
        val unlockedMaps = loadUnlockedMaps()
        if (mapAsset !in unlockedMaps) {
            unlockedMaps.add(mapAsset)
            preferences.flush { this[KEY_UNLOCKED_MAPS] = unlockedMaps }
        }
    }

    fun loadUnlockedMaps(): GdxArray<MapAsset> {
        val storedValue = preferences.get<GdxArray<MapAsset>>(KEY_UNLOCKED_MAPS, GdxArray())
        return storedValue
    }

    companion object {
        private const val KEY_UNLOCKED_MAPS = "unlocked-maps"

        fun forApp(appName: String): GamePreferences = GamePreferences(Gdx.app.getPreferences(appName))
    }
}
