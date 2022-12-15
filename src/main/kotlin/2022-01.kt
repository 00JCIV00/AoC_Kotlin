import java.io.File

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	}
	catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	// Find out how many calories each elf is carrying
	println("Finding elves and how many calories they each have...")
	val elves = mutableMapOf<Int, Int>()
	run {
		var curElf = 1
		var curElfCals = 0
		file.forEachLine { line ->
			if (line.isBlank()) {
				elves[curElf++] = curElfCals
				println("- Found elf ${elves.size} with $curElfCals calories.")
				curElfCals = 0
			} else curElfCals += line.toInt()
		}
		if (curElfCals > 0) elves[curElf] = curElfCals
	}
	val totalCals = elves.values.sum()
	println("Found ${elves.size} elves with a total of $totalCals calories!")

	// Get required stats
	println("Getting Elf Food stats...")
	val sortedElves = elves.toList().sortedBy { (_,value) -> value }.reversed().toMap()
	var top3Total = 0
	for (i in 0..2) {
		val curElf = sortedElves.keys.toIntArray()[i]
		val curElfCals = sortedElves[curElf]
		println("- Most Food #${i + 1}: Elf $curElf is carrying $curElfCals calories.")
		top3Total += curElfCals ?: 0
	}
	println("- The top 3 elves are carrying a total of $top3Total calories!")
	println("Finished Elf Food stats!")
}

