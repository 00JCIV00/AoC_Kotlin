import java.io.File

private data class VNode(val name: String,
						 val flowRate: Int,
						 val neighbors: List<String>,
						 var open: Boolean = false)

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	}
	catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	// Gather Valve Nodes
	println("Gathering Valve Nodes...")
	val vNodes = buildMap {
		file.forEachLine { line ->
			val name = line.split(' ')[1]
			val flowRate = Regex("(?<=rate=)[0-9]*(?=;)").find(line)!!.value.toInt()
			val neighbors = line.replace("to valves", "to valve").substringAfter("to valve ").split(", ")
			put(name, VNode(name, flowRate, neighbors))
		}
	}
	println("Gathered ${vNodes.size} Valve Nodes!")

	// Find Max Releasable Pressure
	/*fun findNextBest(node: VNode): VNode {
		return vNodes.values.filter { vNode -> vNode.name in node.neighbors && !vNode.open && vNode.flowRate > 0 }.maxByOrNull { it.flowRate } ?:
		vNodes.values.filter { vNode -> vNode.name in node.neighbors }.minBy { it.visits }
	}*/

	fun calcDist(start: VNode, end: VNode, visited: MutableSet<String> = mutableSetOf(start.name)): Int {
		visited += start.name
		return when {
			start == end -> 1
			(end in vNodes.values.filter { it.name in start.neighbors }) -> 1
			else -> (buildList {
				start.neighbors.filter { it !in visited }
					.forEach { add(calcDist(vNodes[it]!!, end, visited)) }
			}.minOrNull() ?: vNodes.size) + 1
		}
	}

	println("Finding Max Releasable Pressure...")
	var min = 30
	var curNode = vNodes["AA"] ?: run { println("Start Valve 'AA' not found!"); return@main }
	var pressRel = 0
	val factor = 30 //(1..30).sum()
	while (min >= 0) {
		val newNode = vNodes.values.filter { vNode -> !vNode.open }
								   .maxBy { vNode -> (vNode.flowRate * factor) / (vNode.flowRate * (min - 1 - calcDist(curNode, vNode)) + 1)}
		min -= calcDist(curNode, newNode)
		if (min < 0) {
			println("- Time ran out during travel or waiting.")
			break
		}
		curNode = newNode
		if (!curNode.open && curNode.flowRate > 0) {
			curNode.open = true
			min--
			pressRel += min * curNode.flowRate
		}
		println("- Min: ${min.toString().padStart(3, '0')}, Valve: ${curNode.name}, FR: (${curNode.flowRate} -> ${curNode.flowRate * min}), PR: $pressRel")
	}
	println("The Max Releasable Pressure is $pressRel")
}

