package com.quillraven.github.quillyjumper.ui

import com.quillraven.github.quillyjumper.GamePreferences
import com.quillraven.github.quillyjumper.MapAsset
import com.quillraven.github.quillyjumper.Quillyjumper
import com.quillraven.github.quillyjumper.screen.GameScreen

class MenuModel(private val game: Quillyjumper, prefs: GamePreferences) {

    private val unlockedMaps = prefs.loadUnlockedMaps()

    fun startGame(asset: MapAsset) {
        // set screen first to register event listeners
        game.setScreen<GameScreen>()
        // set map afterwards
        game.getScreen<GameScreen>().loadMap(asset)
    }

    fun isUnlocked(asset: MapAsset): Boolean {
        return asset in unlockedMaps
    }

}
