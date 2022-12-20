import java.io.File

private class Tree(val height: Int) {
	var visUp = false
	var visDown = false
	var visLeft = false
	var visRight = false
	
	var sceneUp = 0
	var sceneDown = 0
	var sceneLeft = 0
	var sceneRight = 0

	val visible: Boolean
		get() = listOf(visUp, visDown, visLeft, visRight).contains(true)
	val scenicScore: Int
		get() = listOf(sceneUp, sceneDown, sceneLeft, sceneRight).fold(1) { product, scene -> product * scene }
}

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	} catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	// Create Forest 2D List
	println("Creating Forest...")
	val forest = buildList<List<Tree>> Y@ {
		file.forEachLine { line ->
			this@Y.add(buildList<Tree> X@ {
				line.forEach { treeHeight -> this@X.add(Tree(treeHeight.digitToInt())) }
			})
		}
	}
	val fWidth = forest[0].size
	val fHeight = forest.size
	println("Created a ${fWidth}Wx${fHeight}H Forest!")

	// Determine the Visibility of each Tree
	println("Determining the Visibility of each Tree...")
	forest.forEachIndexed { y, row ->
		row.forEachIndexed { x, tree ->
			println("- Tree: ${x + 1}, ${y + 1}, H:${tree.height}")
			// Row 
			// - Vis Check
			if (x == 0 || x == row.indexOfFirst { it.height >= tree.height }) tree.visLeft = true
			if (x == fWidth - 1 || (fWidth - 1) - x == row.asReversed().indexOfFirst { it.height >= tree.height }) tree.visRight = true
			// - Scenic
			tree.sceneLeft = if (x == 0) 0
			else {
				run left@{
					var count = 0
					row.subList(0, x).asReversed().forEach {
						count++
						if (it.height >= tree.height) return@left count
					}
					count
				}
			}
			tree.sceneRight = if (x == row.lastIndex) 0
			else {
				run right@{
					var count = 0
					row.subList(x + 1, row.size).forEach {
						count++
						if (it.height >= tree.height) return@right count
					}
					count
				}
			}
			

			// Col Check
			val col = buildList { forest.forEach { add(it[x]) } }
			if (y == 0 || y == col.indexOfFirst { it.height >= tree.height }) tree.visUp = true
			if (y == fHeight - 1 || (fHeight - 1) - y == col.asReversed().indexOfFirst { it.height >= tree.height }) tree.visDown = true
			// - Scenic
			tree.sceneUp = if (y == 0) 0
			else {
				run up@{
					var count = 0
					col.subList(0, y).asReversed().forEach {
						count++
						if (it.height >= tree.height) return@up count
					}
					count
				}
			}
			tree.sceneDown = if (y == col.lastIndex) 0
			else {
				run down@{
					var count = 0
					col.subList(y + 1, col.size).forEach {
						count++
						if (it.height >= tree.height) return@down count
					}
					count
				}
			}
			println("-- Vis(${tree.visible}): L(${tree.visLeft}), R(${tree.visRight}), U(${tree.visUp}), D(${tree.visDown})")
			println("-- Scene(${tree.scenicScore}): L(${tree.sceneLeft}), R(${tree.sceneRight}), U(${tree.sceneUp}), D(${tree.sceneDown})")
			println("=====")
		}
	}
	println("Determined the Visibility of each Tree!")

	// STATS
	println("\nSTATS:")
	// - Total Visible Tree
	val totalVis = forest.sumOf { row -> row.count { tree -> tree.visible } }
	println("- Total # of Visible Trees: $totalVis")
	// - Highest Scenic Score
	val highestScenic = forest.maxOf { row -> row.maxOf { tree -> tree.scenicScore } }
	println("- Highest Scenic Score: $highestScenic")
}