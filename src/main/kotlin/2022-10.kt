import java.io.File

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	} catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}


	// Parse CPU Instructions
	println("Parsing CPU Instructions...")
	val cycles = buildList {
		var reg = 1
		file.forEachLine { line ->
			val inst = line.split(" ")
			when(inst[0]) {
				"noop" -> add(reg)
				else -> {
					add(reg)
					add(reg)
					reg += inst[1].toInt()
				}
			}
		}
	}
	println("Parsed CPU Instructions across ${cycles.size} Cycles!")

	// Build CRT Image
	println("Buidling CRT Image...")
	val cycleRows = cycles.chunked(40)
	val crtRows = buildList<String> {
		cycleRows.forEachIndexed { idx, cycleRow ->
			println("- Row $idx:")
			add(buildString {
				cycleRow.forEachIndexed { cycle, reg ->
					print("$reg | ")
					val sprite = listOf(reg - 1, reg, reg + 1)
					if (cycle in sprite) append("#")
					else append(".")
				}
			})
			println()
		}
	}
	println("Built CRT Image!")

	// STATS
	println("\nSTATS: ")
	//- Signal Strength
	val sigStrengths = buildList<Int> {
		for (s in 19..cycles.size step 20) {
			add(cycles[s] * (s + 1))
			println("-- Cycle ${s + 1}: ${cycles[s] * (s + 1)}")
		}
	}
	val sigStrengthSum = sigStrengths.foldIndexed(0) { idx, sum, acc ->
		if(listOf(20, 60, 100, 140, 180, 220).contains(((idx + 1) * 20))) sum + acc
		else sum
	}
	println("- Signal Strength Sum: $sigStrengthSum")

	//- CRT Image
	println("- CRT Image: ")
	crtRows.forEach { println(it) }
}