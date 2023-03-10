import java.io.File

/**
 * Elf Pairs
 */
private class ElfPair(private val sections: String) {
	private val elves: Pair<IntRange, IntRange> = buildList<IntRange>{
		sections.split(",").forEach { range ->
			val lowHigh = range.split("-").map { it.toInt() }
			add(lowHigh[0]..lowHigh[1])
		}
	}.zipWithNext()[0]
	val overlap: Boolean = elves.first.contains(elves.second) || elves.second.contains(elves.first)
	val partial: Boolean = elves.first.contains(elves.second, true) || elves.second.contains(elves.first, true)
	override fun toString(): String {
		return "Elves: $elves, Overlap: $overlap, Part: $partial"
	}
}

private fun IntRange.contains(range: IntRange, partial: Boolean = false): Boolean {
	return if (!partial) this.min() <= range.min() && this.max() >= range.max()
	else this.contains(range) ||
		(this.min() >= range.min() && this.min() <= range.max()) ||
		(this.max() >= range.min() && this.max() <= range.max())
}

private fun Boolean.toInt(): Int {
	return this.compareTo(false)
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

	// Get Elf Pairs
	println("Getting Elf Pairs...")
	val elfPairs = buildList<ElfPair> {
		var index = 1
		file.forEachLine { line ->
			val elfPair = ElfPair(line)
			add(elfPair)
			println("- EP ${index++}: $elfPair.")
		}
	}
	println("Got ${elfPairs.size} Elf Pairs!")

	// Get stats
	//- Total Number of Overlapping Pairs
	val totalOverlaps = elfPairs.count { it.overlap }
	println("Total Overlapping Pairs: $totalOverlaps")
	//- Total Number of Partially Overlapping Pairs
	val totalPartials = elfPairs.count { it.partial }
	println("Total Partially Overlapping Pairs: $totalPartials")
}

