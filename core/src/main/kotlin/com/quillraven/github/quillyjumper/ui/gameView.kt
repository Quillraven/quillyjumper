package com.quillraven.github.quillyjumper.ui

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.plusAssign
import ktx.actors.then
import ktx.scene2d.*

class GameView(
    model: GameModel,
    skin: Skin
) : Table(skin), KTable {

    init {
        setFillParent(true)

        val mapNamePopup = textButton("", skin = skin) { cell ->
            this.color.a = 0f
            cell.padTop(10f).expand().align(Align.top).minWidth(150f).minHeight(40f).row()
        }
        val lifeImage = image("health_4", skin) { cell ->
            cell.padLeft(4f).padBottom(4f).expand().align(Align.bottomLeft)
        }

        model.onPropertyChange(GameModel::mapName) { mapName ->
            mapNamePopup.run {
                clearActions()
                this += alpha(0f) then fadeIn(1f) then delay(3f) then fadeOut(1f)
                setText(mapName)
            }
        }
        model.onPropertyChange(GameModel::playerLife) { life ->
            lifeImage.drawable = skin.getDrawable("health_$life")
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameModel,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model, skin), init)
