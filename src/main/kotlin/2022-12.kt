import java.io.File
import kotlin.system.exitProcess

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

private fun abs(pair: Pair<Int, Int>): Pair<Int, Int> {
	return Pair(kotlin.math.abs(pair.first),
				kotlin.math.abs(pair.second))
}

private val dirs = mapOf<Char, Pair<Int, Int>>(
	'L' to Pair(-1, 0),
	'R' to Pair(1, 0),
	'U' to Pair(0, -1),
	'D' to Pair(0, 1)
)

private data class ANode(val pos: Pair<Int, Int>, val height: Char,
						 val isStart: Boolean = false, val isEnd: Boolean = false) {
	var parentANode: ANode? = null
	var symbol = height

	var h = 0
	var g = 0
	val f: Int
		get() { return h + g }
}

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	} catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	fun pathfind(setPoints: (Int, Int, Char) -> ANode): List<List<ANode>> {
		// Create Grid
		println("Creating Grid...")
		val grid = buildList cols@{
			file.readLines().forEachIndexed { y, line ->
				add(buildList rows@{
					print("| ")
					line.forEachIndexed { x, char ->
						add(
							setPoints(x, y, char)
						)
						print(char)
					}
					println(" |")
				})
			}
		}
		val allNodes = buildList { grid.forEach { row -> addAll(row) } }
		val startNodes: List<ANode> = allNodes.filter { it.isStart }
		val endNode: ANode = allNodes.firstOrNull { it.isEnd } ?: ANode(Pair(0, 0), 'A')
		allNodes.forEach { node -> node.h = abs(node.pos - endNode.pos).toList().sum() }
		println("Created Grid!")

		return buildList {
			startNodes.forEach { startNode ->
				val searchGrid = buildList { grid.forEach { row -> add(buildList { row.forEach { add(it.copy()) } }) } }
				val searchNodes = buildList { searchGrid.forEach { row -> addAll(row) } }
				val searchStartNode = searchNodes.first { it.pos == startNode.pos }
				val searchEndNode = searchNodes.first { it.pos == endNode.pos }
				// Run A* Pathfinding
				println("Running A* Pathfinding...")
				val closedList: MutableList<ANode> = mutableListOf()
				val openList: MutableList<ANode> = mutableListOf(searchStartNode)
				// var round = 0
				while (openList.isNotEmpty() && searchEndNode.parentANode == null) {
					// println("Rd ${round++}:")
					openList.sortBy { it.f }
					for (o in 0 until openList.size) {
						val node = openList[0]
						// println("- Node: ${node.pos}, F: ${node.f}, G: ${node.g}, H: ${node.h}")
						val neighbors = buildList {
							dirs.values.forEach { dir ->
								val neighbor = searchNodes.firstOrNull { it.pos == node.pos + dir }
								if (neighbor != null) add(neighbor)
							}
						}
						run neighbors@{
							neighbors.forEach { neighbor ->
								val heightDiff = neighbor.height - node.height
								if (heightDiff <= 1) {
									val oldG = neighbor.g
									val oldF = neighbor.f
									neighbor.g = node.g + 1 + kotlin.math.abs(heightDiff)
									when {
										neighbor == searchEndNode -> {
											neighbor.parentANode = node
											return@neighbors
										}

										(oldF <= neighbor.f && (neighbor in buildList {
											addAll(openList); addAll(
											closedList
										)
										})) -> neighbor.g =
											oldG

										else -> {
											neighbor.parentANode = node
											openList.add(neighbor)
										}
									}
								}
							}
						}
						closedList.add(openList.removeFirst())
					}
				}
				println("Completed A* Pathfinding!")

				if (searchEndNode.parentANode == null) {
					println("Couldn't find a path!")
					add(emptyList())
				}

				println("Found a Path!")
				val path = buildList {
					var node = searchEndNode
					while (node != searchStartNode) {
						/*run parent@{
							val parent = node.parentANode ?: return@parent
							parent.symbol = when (node.pos - parent.pos) {
								dirs['L'] -> '<'
								dirs['R'] -> '>'
								dirs['U'] -> '^'
								dirs['D'] -> 'v'
								else -> '.'
							}
						}*/
						add(node)
						node = node.parentANode ?: break
					}
					reversed()
				}
				/*searchGrid.forEach { row ->
					print("| ")
					row.forEach {
						print(
							when {
								it.symbol in listOf('<', '>', '^', 'v') -> it.symbol
								it.isEnd -> 'E'
								else -> '.'
							}
						)
					}
					println(" |")
				}*/
				add(path)
			}
		}
	}

	val path1 = pathfind { x: Int, y: Int, char: Char ->
		when (char) {
			'S' -> ANode(Pair(x, y), 'a', isStart = true)
			'E' -> ANode(Pair(x, y), 'z', isEnd = true)
			else -> ANode(Pair(x, y), char)
		}
	}[0]
	
	val path2 = pathfind { x: Int, y: Int, char: Char ->
		when (char) {
			'S', 'a' -> ANode(Pair(x, y), 'a', isStart = true)
			'E' -> ANode(Pair(x, y), 'z', isEnd = true)
			else -> ANode(Pair(x, y), char)
		}
	}.filter { it.isNotEmpty() && it.size >= 3 }.minBy { it.size }

	//STATS
	println("\nSTATS:")
	// Path 1 Length
	val path1Length = path1.size
	println("- Path 2 Length: $path1Length")
	// Path 2 Length
	val path2Length = path2.size
	println("- Path 2 Length: $path2Length")
	
}

