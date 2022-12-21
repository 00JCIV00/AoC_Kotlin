import java.io.File


private operator fun Pair<Int, Int>.plus(pair: Pair<Int, Int>): Pair<Int, Int> {
	return Pair(this.first + pair.first,
				this.second + pair.second)
}
private operator fun Pair<Int, Int>.minus(pair: Pair<Int, Int>): Pair<Int, Int> {
	return Pair(this.first - pair.first,
				this.second - pair.second)
}
private operator fun Pair<Int, Int>.times(num: Int): Pair<Int, Int> {
	return Pair(this.first * num,
		 		this.second * num)
}
private operator fun Pair<Int, Int>.div(pair: Pair<Int, Int>): Pair<Int, Int> {
	return Pair(if (pair.first == 0) 0 else this.first / pair.first,
				if (pair.second == 0) 0 else this.second / pair.second)
}

fun abs(pair: Pair<Int, Int>): Pair<Int, Int> {
	return Pair(kotlin.math.abs(pair.first),
				kotlin.math.abs(pair.second))
}

fun updatePos(lead: Pair<Int, Int>, follow: Pair<Int, Int>): Pair<Int, Int> {
	val diff = lead - follow
	return if (abs(diff).toList().any { it > 1 }) follow + (diff / abs(diff))
	else follow
}
fun buildVisMap(tailVisits: List<Triple<Int, Int, Int>>): List<List<Char>> {
	return buildList row@{
		for (y in tailVisits.minOf { it.second }..tailVisits.maxOf { it.second }) {
			add(buildList col@{
				for (x in tailVisits.minOf { it.first }..tailVisits.maxOf { it.first } + 1) {
					val count = tailVisits.count { it.first == x && it.second == y }
					this@col.add(
						if (count > 0) count.toString()[0]
						else '_'
					)
				}
			})
		}
		reverse()
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

	val insts = mapOf<Char, Pair<Int, Int>>(
		'L' to Pair(-1, 0),
		'R' to Pair(1, 0),
		'D' to Pair(0, -1),
		'U' to Pair(0, 1)
	)

	var head = Pair(0, 0)
	val tails = buildList { for(i in 0 until 9) { add(Pair(0, 0)) } }.toMutableList()
	val tailVisits = buildList<Triple<Int, Int, Int>> {
		tails.forEachIndexed { idx, tail -> add(Triple(tail.first, tail.second, idx)) }
		var idx = 1
		file.forEachLine { line ->
			// Head
			val dir = line[0]
			val hInst = insts[dir] ?: Pair(0, 0)
			val mag = line.split(" ")[1].toInt()
			println("${idx++.toString().padStart(4, '0')} Dir: $dir ${insts[dir]}, Mag: $mag")
			for (m in 0 until mag) {
				head += hInst
				tails.subList(0, tails.size).forEachIndexed { idx, tail ->
					val updPos = if (idx == 0) updatePos(head, tail)
								 else updatePos(tails[idx -1], tail)
					if (tail != updPos) {
						tails[idx] = updPos
						add(Triple(updPos.first, updPos.second, idx))
						if (idx == 0) println("-- Tail1 Upd: $updPos")
					}
				}
				println("- H: $head | T1: ${tails[0]} | T2: ${tails[1]} | T9: ${tails[8]}")
			}
		}
	}

	// STATS
	println("STATS:")
	// - Maps
	val vis1Map = buildVisMap(tailVisits.filter { it.third == 0 })
	println("- Tail 1 Visits Map:")
	vis1Map.forEach { println(it) }
	println("=================")
	val vis9Map = buildVisMap(tailVisits.filter { it.third == 8 })
	println("- Tail 9 Visits Map:")
	vis9Map.forEach { println(it) }
	println("=================")
	// - Distinct Tail Visits
	val distinctTail1Vis = tailVisits.filter { it.third == 0 }.distinct().size
	println("- Distinct Tail 1 Visits: $distinctTail1Vis")
	val distinctTail9Vis = tailVisits.filter { it.third == 8 }.distinct().size
	println("- Distinct Tail 9 Visits: $distinctTail9Vis")
}

