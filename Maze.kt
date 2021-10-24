import java.awt.Point
import java.lang.System.currentTimeMillis
import java.util.*
import kotlin.random.Random

class Player (pos: Point){
    var step = 0
        private set
    var bonus = 0
        private set
    var pos: Point = pos
        private set

    fun getInfo(): PlayerInfo {
        return PlayerInfo(pos, step, bonus)
    }

    fun addStep() {
        step += 1
    }

    fun updatePos(newPos: Point) {
        pos.setLocation(newPos.x, newPos.y)
    }

    fun getBonus(extra: Int) {
        bonus += extra
    }
}

class PlayerInfo (
    var pos: Point,
    var step: Int,
    var bonus: Int
)

class Maze (
    private var width: Int = 24,
    private var height: Int = 11,
    val map: String = """
        |======================|
        | |  *       |     *   |
        | | |======| |===== |  |
        |  *   |*  | |      |==|
        |==== ==== |    |    * |
        |          | |  |======|
        |  |=======| |    * |  |
        |  |         |===   |  |
        |= ========| | *|      |
        |  * * *|    |    E |  |
        |======================|
    """.trimIndent()
){

    private var player: Player = Player(Point(1, 1))
    private val eatenBonus: MutableList<Int> = mutableListOf()
    private val bonusList: MutableList<Int> = generateBonusList()
    var gameOver = false
        private set

    // console color
    private val ANSI_YELLOW = "\u001B[33m"
    private val ANSI_RESET = "\u001B[0m"
    private val ANSI_BLUE = "\u001B[34m"
    private val ANSI_GREEN = "\u001B[32m"

    private fun generateBonusList(): MutableList<Int> {
        val defaultToken = listOf('|', ' ', '=', 'E', '\n')
        val bonusList = mutableListOf<Int>()
        var position = 0
        for (p in map) {
            position += 1
            if (!defaultToken.contains(p)) {
                bonusList.add(position)
            }
        }
        return bonusList
    }

    private fun positionOnMap(pos: Point): Int {
        return (pos.y) * (width + 1) + (pos.x) + 1
    }

    private fun indexOnMap(pos: Point): Int {
        return positionOnMap(pos) - 1
    }

    fun getBonusPosition(): List<Point> {
        return bonusList.map {
            Point(it % (width + 1) - 1, it / (width + 1))
        }
    }

    fun getPlayer(): PlayerInfo {
        return player.getInfo()
    }

    fun gameOverMessage() {
        println("""
            
            # # # Game Over # # #
            #                   #
            #   $ANSI_BLUE Steps: ${player.step.toString().padStart(4, ' ')}$ANSI_RESET     #
            #   $ANSI_BLUE bonus: ${player.bonus.toString().padStart(4, ' ')}$ANSI_RESET     # 
            #                   #
            # # # # # # # # # # #
        """.trimIndent())
    }

    fun printMap() {
        var counter = 0
        val playerPos = positionOnMap(player.pos)
        for (a in map) {
            counter += 1
            when {
                counter == playerPos -> print(ANSI_YELLOW + "X" + ANSI_RESET)
                eatenBonus.contains(counter) -> print(" ")
                a == 'E' -> print(ANSI_GREEN + 'E' + ANSI_RESET)
                else -> print(a)
            }
        }
        println("-- Steps: $ANSI_YELLOW${player.step}$ANSI_RESET, Bonus: $ANSI_YELLOW${player.bonus}$ANSI_RESET --")
    }

    fun move(direction: Direction): Boolean {
        player.addStep()
        val newPos = direction.transfer(player.pos)
        try {
            validation(newPos)
            updatePos(newPos)
        } catch (e: java.lang.IllegalStateException) {
            println("Invalid Move")
            return false
        }
        return true
    }

    private fun updatePos(newPos: Point) {
        when (val token = map[indexOnMap(newPos)]) {
            'E' -> gameOver = true
            '*' -> {
                player.getBonus(getRandomValue())
                bonusList.remove(positionOnMap(newPos))
                eatenBonus.add(positionOnMap(newPos))
            }
            else -> if (token != ' ') invalidPositionException(newPos)
        }
        player.updatePos(newPos)
    }

    private fun validation(pos: Point) {
        if ((pos.x) > width || pos.x < 0 || pos.y > height || pos.y < 0) {
            invalidPositionException(pos)
        }
    }

    private fun invalidPositionException(pos: Point): Nothing {
        throw IllegalStateException("pos: ($pos.x, $pos.y) is invalid")
    }

    private fun getRandomValue(max: Int = 30): Int {
        return Random(currentTimeMillis()).nextInt(5, max)
    }

}

enum class Direction {
    UP, DOWN, RIGHT, LEFT, DUMMY;

    fun transfer(pos: Point): Point {
        return when (this) {
            UP -> Point(pos.x, pos.y - 1)
            DOWN -> Point(pos.x, pos.y + 1)
            RIGHT -> Point(pos.x + 1, pos.y)
            LEFT -> Point(pos.x - 1, pos.y)
            DUMMY -> pos
        }
    }
}


fun main() {
    val maze = Maze()
    maze.printMap()

    /* start the game */
    while (!maze.gameOver) {
        /* Write your code here */
    }

    /* game over message */
    maze.gameOverMessage()
}
