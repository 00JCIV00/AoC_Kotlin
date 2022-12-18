import java.io.File

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	} catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	// Setup Variables
	val lines = file.readLines()
	val split = lines.indexOfFirst { it.contains(Regex("^ 1")) }
	val numStacks = lines[split].filterNot { it.isWhitespace() }.last().digitToInt()
	val rawStacks = lines.subList(0, split)
	val instructions = lines.subList(split + 2, lines.size)

	// Create Stacks Lists
	println("Creating Crate Stacks...")
	val stacksPt1: List<MutableList<Char>> = buildList {
		for (i in 1..numStacks) add(mutableListOf<Char>())
		rawStacks.asReversed().forEach { rawRow ->
			val row = rawRow.replace("    ", "-").filterNot { it.isWhitespace() || it in listOf('[', ']') }
			row.forEachIndexed { idx, crate ->
				if (crate != '-') this[idx].add(crate)
			}
		}
	}
	val stacksPt2 = buildList {
		stacksPt1.forEach { add(it.toMutableList()) }
	}
	println("Created ${stacksPt1.size} Crate Stacks!")

	// Rearrange Crate Stacks
	println("Rearranging Pt 1 Crate Stacks...")
	println("- Current Stacks: ")
	stacksPt1.forEach { println("\t$it")}
	println("================")
	instructions.forEach { rawInst ->
		val inst = buildList<Int> {
			rawInst.split(" ")
				   .filter { (it.toIntOrNull() ?: -1) >= 0 }
				   .forEach { add(it.toInt() - 1) }
		}
		println("""
				- Instruction:
				-- Raw: $rawInst
				-- Parse: $inst
				""".trimIndent().trim())
		// Pt 1
		for (i in 0..inst[0]) {
			stacksPt1[inst[2]].add(stacksPt1[inst[1]].removeLast())
		}

		// Pt 2
		val pt2Range = (stacksPt2[inst[1]].lastIndex - inst[0])..(stacksPt2[inst[1]].size)
		stacksPt2[inst[2]].addAll(stacksPt2[inst[1]].subList(pt2Range.first, pt2Range.last))
		stacksPt2[inst[1]].subList(pt2Range.first, pt2Range.last).clear()

		println("""
- Current Stacks:
-- Pt 1
${buildString { stacksPt1.forEachIndexed { idx, crate -> append("\t${idx + 1}: $crate\n") } } }
=====
-- Pt 2
${buildString { stacksPt2.forEachIndexed { idx, crate -> append("\t${idx + 1}: $crate\n") } } }
================
				""".trim())
	}
	println("Rearranged Crate Stacks via ${instructions.size} Instructions!")

	// Stats
	println("\nSTATS: ")
	val topCratesPt1 = buildString {
		stacksPt1.forEach { append(it.last()) }
	}
	println("- The Top Crates for all Part 1 Stacks are: $topCratesPt1")
	val topCratesPt2 = buildString {
		stacksPt2.forEach { append(it.last()) }
	}
	println("- The Top Crates for all Part 2 Stacks are: $topCratesPt2")

}