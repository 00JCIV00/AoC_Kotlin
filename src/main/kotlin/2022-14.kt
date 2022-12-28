import java.io.File

private class Vector2D(var x: Int, var y: Int) {
	enum class Dirs(val dir: Vector2D) {
		L(Vector2D(-1, 0)),
		R(Vector2D(1, 0)),
		U(Vector2D(0, -1)),
		D(Vector2D(0, 1)),
		UL(Vector2D(-1, -1)),
		UR(Vector2D(1, -1)),
		DL(Vector2D(-1, 1)),
		DR(Vector2D(1, 1))
	}

	companion object {
		fun of(input: String): Vector2D {
			val pair = input.split(',').map { it.toInt() }
			return Vector2D(pair[0], pair[1])
		}
	}

	override operator fun equals(other: Any?): Boolean {
		return when (other!!::class) {
			Vector2D::class -> return (x == (other as Vector2D).x) && (y == other.y)
			else -> false
		}
	}
	operator fun plus(other: Vector2D): Vector2D { return Vector2D(x + other.x, y + other.y) }
	operator fun minus(other: Vector2D): Vector2D { return Vector2D(x - other.x, y - other.y) }
	operator fun times(other: Vector2D): Vector2D { return Vector2D(x * other.x, y * other.y) }
	operator fun div(other: Vector2D): Vector2D {
		val quotX = if (other.x == 0) 0 else x / other.x
		val quotY = if (other.y == 0) 0 else y / other.y
		return Vector2D(quotX, quotY)
	}
	operator fun get(at: Dirs): Vector2D { return this + at.dir }
	fun abs(): Vector2D { return Vector2D(kotlin.math.abs(x), kotlin.math.abs(y)) }

	val neighbors: List<Vector2D> get() { return buildList { Dirs.values().forEach { add(this@Vector2D + it.dir) } } }

	override fun toString(): String {
		return "x: $x, y: $y"
	}
}

private class Tile(val pos: Vector2D, var item: Item) {
	enum class Item(val symbol: Char) {
		AIR('.'),
		WALL('#'),
		SAND('o'),
		SRC('+')
	}
}

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	} catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	// Generate Grid
	println("Generating Grid...")
	println("- Finding Wall Tiles...")
	val walls = buildList {
		file.forEachLine { line ->
			val rawCorners = line.split(" -> ")
			val corners = buildList { rawCorners.forEach { corner -> add(Vector2D.of(corner)) } }
			corners.dropLast(1).forEachIndexed { idx, corner ->
				val nextCorner = corners[idx + 1]
				val dir = ((nextCorner - corner) / (nextCorner - corner).abs())
				println("-- From ($corner) going ($dir) to ($nextCorner)")
				var curTile = corner
				while (curTile != nextCorner) {
					add(curTile)
					println("--- Added $curTile")
					curTile += dir
				}
			}
			add(corners.last())
			println("--- Added ${corners.last()}")
		}
	}.toMutableList()
	println("- Found ${walls.size} Wall Tiles!")
	val src = Vector2D(500,0)
	walls += src
	val xBorder = 20
	val yBorder = 1
	val xMin = walls.minOf { it.x } - xBorder
	val xMax = walls.maxOf { it.x } + xBorder
	val yMin = walls.minOf { it.y } - yBorder
	val yMax = walls.maxOf { it.y } + yBorder
	val relXMax = xMax - xMin
	val relYMax = yMax - yMin
	println("- Scope:")
	println("-- X: (min:$xMin max:$xMax), Y: (min:$yMin max:$yMax)")
	println("-- Relative: Max X: $relXMax, Max Y: $relYMax")

	println("- Pouring Sand...")
	val sand = mutableListOf<Vector2D>()
	var pt1 = 0
	var pt2 = 0
	while (true) {
		val blocked = buildList { addAll(walls); addAll(sand) }
		var curPos = src
		val pt2Wall = walls.maxOf { it.x } + 2
		while (true) {
			curPos = when {
				curPos[Vector2D.Dirs.D] !in blocked && curPos[Vector2D.Dirs.D].y < pt2Wall -> curPos[Vector2D.Dirs.D]
				curPos[Vector2D.Dirs.DL] !in blocked && curPos[Vector2D.Dirs.DL].y < pt2Wall  -> curPos[Vector2D.Dirs.DL]
				curPos[Vector2D.Dirs.DR] !in blocked && curPos[Vector2D.Dirs.DR].y < pt2Wall  -> curPos[Vector2D.Dirs.DR]
				else -> break
			}
		}
		// Pt 1
		if (curPos.y >= yMax && pt1 == 0) pt1 = sand.size
		sand += curPos
		if (sand.size % 100 == 0) println("-- Poured: ${sand.size} to $curPos")
		// Pt 2
		if (curPos == src) {
			pt2 = sand.size
			break
		}
	}
	println("- Poured Sand!")

	println("- Building the Grid Layout...")
	val grid = buildList<MutableList<Tile?>> {
		for (y in 0..relYMax) {
			add(buildList {
				for (x in 0..relXMax) {
					val pos = Vector2D(x + xMin, y + yMin)
					add(when (pos) {
						src -> Tile(pos, Tile.Item.SRC)
						in walls -> Tile(pos, Tile.Item.WALL)
						in sand -> Tile(pos, Tile.Item.SAND)
						else -> null
					})
				}
			}.toMutableList())
		}
	}.toMutableList()
	println("- Built the Grid Layout!")

	println("- Final Layout:")
	// Column Headers
	println(buildString {
		append("${xMin - 1} |")
		val start = xMin.toString().last().digitToInt()
		for (i in 0..relXMax) { append((start + i) % 10) }
		append('|')
	})
	// Headers Separator
	println(buildString { for(c in 0..relXMax + 6) { append('-') } })
	// Rows
	grid.forEachIndexed { idx, row ->
		print("${(relYMax - (relYMax - idx + yBorder)).toString().padStart(3, '0')} |")
		print(row.map { tile -> tile?.item?.symbol ?: '.'}.joinToString(""))
		println('|')
	}

	//STATS
	//-Pt 1 Total Grains
	println("- Pt 1 Grains of Sand: $pt1")
	//-Pt 2
	println("- Pt 2 Grains of Sand: $pt2")
}