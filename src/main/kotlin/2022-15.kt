import java.io.File

private class Vec2D_15(var x: Int, var y: Int) {
	enum class Dirs(val dir: Vec2D_15) {
		L(Vec2D_15(-1, 0)),
		R(Vec2D_15(1, 0)),
		U(Vec2D_15(0, -1)),
		D(Vec2D_15(0, 1)),
		UL(Vec2D_15(-1, -1)),
		UR(Vec2D_15(1, -1)),
		DL(Vec2D_15(-1, 1)),
		DR(Vec2D_15(1, 1))
	}

	companion object {
		fun of(input: String): Vec2D_15 {
			val pair = input.split(',').map { Regex("[^0-9-]").replace(it ,"").toInt() }
			return Vec2D_15(pair[0], pair[1])
		}
	}

	override operator fun equals(other: Any?): Boolean {
		return when (other!!::class) {
			Vec2D_15::class -> return (x == (other as Vec2D_15).x) && (y == other.y)
			else -> false
		}
	}
	operator fun plus(other: Vec2D_15): Vec2D_15 { return Vec2D_15(x + other.x, y + other.y) }
	operator fun minus(other: Vec2D_15): Vec2D_15 { return Vec2D_15(x - other.x, y - other.y) }
	operator fun times(other: Vec2D_15): Vec2D_15 { return Vec2D_15(x * other.x, y * other.y) }
	operator fun div(other: Vec2D_15): Vec2D_15 {
		val quotX = if (other.x == 0) 0 else x / other.x
		val quotY = if (other.y == 0) 0 else y / other.y
		return Vec2D_15(quotX, quotY)
	}
	operator fun get(at: Dirs): Vec2D_15 { return this + at.dir }
	fun abs(): Vec2D_15 { return Vec2D_15(kotlin.math.abs(x), kotlin.math.abs(y)) }

	val sum: Int get() { return x + y }
	val neighbors: List<Vec2D_15> get() { return buildList { Dirs.values().forEach { add(this@Vec2D_15 + it.dir) } } }

	override fun toString(): String {
		return "x: $x, y: $y"
	}
}

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	}
	catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	// Find Sensor/Beacon/Distance Triples
	println("Finding Sensor/Beacon/Distance Triples...")
	val triples = buildList {
		file.forEachLine { line ->
			val rawPair = Regex("x=(-)?[0-9]+, y=(-)?[0-9]+").findAll(line).toList().map { it.value }
			add(Triple(
				Vec2D_15.of(rawPair[0]),
				Vec2D_15.of(rawPair[1]),
				(Vec2D_15.of(rawPair[0]) - Vec2D_15.of(rawPair[1])).abs().sum
			))
		}
	}
	triples.forEach { println(it) }
	println("Found ${triples.size} Sensor/Beacon/Distance Triples!")

	val xPad = triples.maxOf { it.third }
	val minX = triples.minOf { listOf(it.first, it.second).minOf { pos -> pos.x } } - xPad
	val maxX = triples.maxOf { listOf(it.first, it.second).maxOf { pos -> pos.x } } + xPad

	// Pt 1: Find Open tiles on the specified row
	val y1 = 2_000_000
	println("Finding closed tiles on Row $y1...")
	//println("-- Sensor: ${triple.first}, Dist: ${(triple.first - curPos).abs().sum}, Range: ${triple.third.sum}")
	val pt1 =  (minX..maxX).count { x ->
		val curPos = Vec2D_15(x, y1)
		triples.any { tri -> (tri.first - curPos).abs().sum <= tri.third } &&
		curPos !in triples.map { it.first } &&
		curPos !in triples.map { it.second }
	}
	println("Found $pt1 closed tiles on Row $y1!")

	// Pt 2: Find the Beacon
	println("Finding the Beacon and Calculating its Frequency...")
	println("- Finding Border Points...")
	val borders = buildList {
		triples.forEach { tri ->
			val rng = tri.third + 1
			val vecRange = Vec2D_15(rng, rng)
			// Border Corners
			val up = tri.first + Vec2D_15.Dirs.U.dir * vecRange
			val down = tri.first + Vec2D_15.Dirs.D.dir * vecRange
			val left = tri.first + Vec2D_15.Dirs.L.dir * vecRange
			val right = tri.first + Vec2D_15.Dirs.R.dir * vecRange
			addAll(listOf(up, down, left, right))

			// Border Edges
			var curVec = up + Vec2D_15.Dirs.DL.dir
			while (curVec != left) {
				add(curVec)
				curVec += Vec2D_15.Dirs.DL.dir
			}
			curVec = up + Vec2D_15.Dirs.DR.dir
			while (curVec != right) {
				add(curVec)
				curVec += Vec2D_15.Dirs.DR.dir
			}
			curVec = down + Vec2D_15.Dirs.UL.dir
			while (curVec != left) {
				add(curVec)
				curVec += Vec2D_15.Dirs.UL.dir
			}
			curVec = down + Vec2D_15.Dirs.UR.dir
			while (curVec != right) {
				add(curVec)
				curVec += Vec2D_15.Dirs.UR.dir
			}

		}
	}
	println("- Found ${borders.size} Border Pts!")
	val bRng = 0..4_000_000
	val bPos = borders.filter { it.x in bRng && it.y in bRng }.firstOrNull { pt ->
		triples.none { sensor -> (sensor.first - pt).abs().sum <= sensor.third }
	}
	val bFreq: Long = ((bPos?.x ?: 0) * 4_000_000L) + (bPos?.y ?: 0)
	println("Found the Beacon at ($bPos) w/ Freq: $bFreq")
}

