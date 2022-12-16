import java.io.File

/**
 * Generates the data of a ruck sack based on its Raw Contents ([rawContents]).
 */
class Rucksack(val rawContents: String) {
	val contents: Pair<String, String>
	val sharedItem: Char
	val sharedItemVal: Int
	init {
		contents = rawContents.chunked(rawContents.length / 2).zipWithNext()[0]
		sharedItem = contents.first.firstOrNull { it in contents.second } ?: '`'
		sharedItemVal = getItemVal(sharedItem)
	}

	override fun toString(): String {
		return "Item: $sharedItem, Val: $sharedItemVal, Cont: $contents"
	}
}

/**
 * Groups of 3 elves
 */
class ElfGroup(elves: List<Rucksack>) {
	val badge: Char
	val badgeVal: Int
	init {
		val allContents = buildList<String> { elves.forEach { add(it.rawContents) } }
		badge = allContents[0].firstOrNull { it in allContents[1] && it in allContents[2] } ?: '`'
		badgeVal = getItemVal(badge)
	}

	override fun toString(): String {
		return "Badge: $badge, Val: $badgeVal"
	}
}

/**
 * Return the value of an [item].
 * - a-z = 1-26
 * - A-Z = 27-52
 */
fun getItemVal(item: Char): Int {
	return when {
		item.isUpperCase() -> item.code - 38
		else -> item.code - 96
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

	// Parse each rucksack
	println("Parsing each ruck...")
	val rucks = mutableListOf<Rucksack>()
	file.forEachLine { line ->
		val ruck = Rucksack(line)
		rucks.add(ruck)
		println("- Ruck ${rucks.size}: $ruck")
	}
	println("Parsed all ${rucks.size} rucks!")

	// Get Elf Groups
	println("Creating Elf Groups...")
	val elfGroups = buildList<ElfGroup> { rucks.chunked(3).forEach { add(ElfGroup(it)) } }
	elfGroups.forEachIndexed { index, elfGroup -> println("- Group ${index + 1}: $elfGroup.") }
	println("Created ${elfGroups.size} Elf Groups!")

	// Stats
	// - Get Total Shared Values
	val totalSharedVal = rucks.sumOf { it.sharedItemVal }
	println("The Total of all ${rucks.size} Shared Item Values is $totalSharedVal!")
	// - Get Total Group Badge Values
	val totalGroupVal = elfGroups.sumOf { it.badgeVal }
	println("The Total of all ${elfGroups.size} Badge Values is $totalGroupVal!")
}

