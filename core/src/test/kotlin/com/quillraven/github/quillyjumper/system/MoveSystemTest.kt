package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.component.Move
import com.quillraven.github.quillyjumper.component.MoveDirection
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class MoveSystemTest {

    private fun testWorld() = configureWorld {
        systems {
            add(MoveSystem())
        }
    }

    @Test
    fun testMoveUpdate() {
        val testCases = mapOf(
            Move(direction = MoveDirection.NONE, max = 3f, timeToMax = 1f) to 0f,
            Move(direction = MoveDirection.RIGHT, max = 3f, timeToMax = 1f) to 3f,
            Move(direction = MoveDirection.LEFT, max = 3f, timeToMax = 1f) to -3f,
        )

        testCases.forEach { (moveCmp, expectedSpeed) ->
            val world = testWorld()
            val testEntity = world.entity {
                it += moveCmp
            }

            with(world) {
                update(1f)
                assertEquals(expectedSpeed, testEntity[Move].current)
            }
        }
    }

    @Test
    fun testMoveStop() {
        val world = testWorld()
        val testEntity = world.entity {
            it += Move(direction = MoveDirection.NONE, current = 3f, max = 3f, timeToMax = 1f)
        }

        with(world) {
            update(1f)
            assertEquals(0f, testEntity[Move].current)
        }
    }

    @Test
    fun testMoveDirectionChange() {
        val world = testWorld()
        val entityLeftToRight = world.entity {
            it += Move(direction = MoveDirection.RIGHT, current = -3f, max = 3f, timeToMax = 1f)
        }
        val entityRightToLeftHandler = world.entity {
            it += Move(direction = MoveDirection.LEFT, current = 3f, max = 3f, timeToMax = 1f)
        }
        with(world) {
            update(1f)
            assertEquals(0f, entityLeftToRight[Move].current)
            assertEquals(0f, entityRightToLeftHandler[Move].current)
        }
    }

}
