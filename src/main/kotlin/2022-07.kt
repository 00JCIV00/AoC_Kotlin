import java.io.File

private class Node(val parent: Node?,
				   val name: String,
				   val size: Int = 0) {
	val totalSize: Int
		get() = size + children.sumOf { it.totalSize }
	val isRoot: Boolean = parent == null
	val children = mutableListOf<Node>()
	val nodeType: String
		get() = if (children.isEmpty()) "file" else "dir"

	operator fun get(i: String): Node? {
		return children.firstOrNull { it.name == i }
	}
	operator fun set(i: String, b: Node) {
		if (this[i] == null) {
			children.add(b)
			return
		}
		children[children.indexOfFirst { it.name == i }] = b
	}

	fun filter(predicate: (Node) -> Boolean): List<Node> {
		return buildList {
			if (predicate(this@Node)) add(this@Node)
			children.forEach { child -> addAll(child.filter { subChild -> predicate(subChild) }) }
		}
	}

	fun toString(depth: Int): String {
		return buildString {
			if (depth > 0) for(i in 1..depth) append("\t")
			append(" > $name ($nodeType, size=$totalSize)\n")
			children.forEach { append(it.toString(depth + 1)) }
		}
	}
	override fun toString(): String {
		return toString(0)
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

	val rootNode = Node(null, "/")
	var curNode: Node = rootNode

	println("Parsing Instructions...")
	file.forEachLine { line ->
		when(line.first()) {
			// Instruction
			'$' -> {
				val inst = line.substring(2, line.length).split(" ")
				when (inst[0]) {
					"cd" -> {
						when (inst[1]) {
							".." -> curNode = curNode.parent ?: rootNode
							"/" -> curNode = rootNode
							else -> {
								curNode[inst[1]] = Node(curNode, inst[1])
								curNode = curNode[inst[1]] ?: curNode
							}
						}
					}
					else -> ""
				}
			}
			// Node
			else -> {
				val node = line.split(" ")
				when (node[0]) {
					"dir" -> curNode[node[1]] = Node(curNode, node[1])
					else -> curNode[node[1]] = Node(curNode, node[1], node[0].toInt())
				}
			}
		}
	}
	println("Parsed Instructions!")
	println("Instructions Result:\n $rootNode")

	// STATS
	println("/nSTATS:")
	// - Sub 100K Directories
	val sub100K = rootNode.filter { child -> child.nodeType == "dir" && child.totalSize <= 100000 }
	val sub100KSum = sub100K.sumOf { it.totalSize }
	println("- Sub 100K Directories:${buildString { sub100K.forEach { append(" ${it.name} |") } } }, Sum: $sub100KSum")
	// - Smallest deletable Directory to fit System Update
	val diskSize = 70000000
	val updSize = 30000000
	val minDir = rootNode.filter { child -> child.nodeType=="dir" && diskSize - (rootNode.totalSize - child.totalSize) >= updSize }.minBy { it.totalSize }
	println("- The smallest deletable directory to fit the System Update is: Name: ${minDir.name}, Size ${minDir.totalSize}")

}