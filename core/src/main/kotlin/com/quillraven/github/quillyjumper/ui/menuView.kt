package com.quillraven.github.quillyjumper.ui

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.quillraven.github.quillyjumper.MapAsset
import ktx.actors.onClick
import ktx.scene2d.*

class MenuView(
    model: MenuModel,
    skin: Skin
) : Table(skin), KTable {

    init {
        padTop(10.0f)
        padBottom(10.0f)
        setFillParent(true)

        menuButton(MapAsset.TUTORIAL, model)
        menuButton(MapAsset.MAP_1, model)
        menuButton(MapAsset.MAP_2, model)
    }

    private fun menuButton(relatedMapAsset: MapAsset, model: MenuModel) {
        textButton(relatedMapAsset.displayName, "menu-btn", skin) { cell ->
            val isMapUnlocked = model.isUnlocked(relatedMapAsset)
            this.isDisabled = !isMapUnlocked
            this.touchable = if (isMapUnlocked) Touchable.enabled else Touchable.disabled
            cell.expandY().fillX().row()
        }.onClick { model.startGame(relatedMapAsset) }
    }

}

@Scene2dDsl
fun <S> KWidget<S>.menuView(
    model: MenuModel,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: MenuView.(S) -> Unit = {}
): MenuView = actor(MenuView(model, skin), init)
