import java.io.File

private class Monkey(val idx: Int,
	  				 val op: (Long) -> Long,
					 val test: Long,
					 val trueTo: Int,
					 val falseTo: Int,
					 initItems: List<Long> = listOf(0)) {
	val items = initItems.toMutableList()
	var inspections = 0
		private set
	val nextTo: Int
		get() { return if (items[0] % test == 0L) trueTo else falseTo }

	/** Needed Help for the first time here on Pt 2. Thank you, [Todd Ginsberg](https://todd.ginsberg.com/post/advent-of-code/2022/day11/) */
	fun inspect(changeWorry: (Long) -> Long = { it / 3L }) {
		items[0] = changeWorry(op(items[0]))
		inspections++
	}
	fun catch(item: Long) {
		items.add(item)
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

	// Create Monkeys
	println("Creating Monkeys...")
	val monkeys = buildList {
		file.readLines().chunked(7).forEachIndexed {idx, info ->
			// Initial Items
			val initItems = info[1].split(": ")[1].split(", ").map { it.toLong() }
			// Operation
			val op = run op@ {
				val calcs = info[2].split("= ")[1].split(" ")
				when (calcs[1]) {
					"+" -> return@op { old: Long -> old + if(calcs[2] == "old") old else calcs[2].toLong() }
					"-" -> return@op { old: Long -> old - if(calcs[2] == "old") old else calcs[2].toLong() }
					"*" -> return@op { old: Long -> old * if(calcs[2] == "old") old else calcs[2].toLong() }
					else -> return@op { old: Long -> old / if(calcs[2] == "old") old else calcs[2].toLong() }
				}
			}
			// Test
			val test = info[3].split("by ")[1].toLong()
			// Throws
			val trueTo = info[4].split("monkey ")[1].toInt()
			val falseTo = info[5].split("monkey ")[1].toInt()

			add(Monkey(idx, op, test, trueTo, falseTo, initItems))
		}
	}
	val monkeysPt2 = buildList {
		monkeys.forEach { monkey ->
			add(Monkey(monkey.idx, monkey.op, monkey.test, monkey.trueTo, monkey.falseTo, monkey.items.toList()))
		}
	}
	println("Created ${monkeys.size} Monkeys!")

	// Conduct 20 Rounds
	println("Playing 20 rounds of Pt 1 Monkey in the Middle...")
	println("- Start:")
	monkeys.forEach {monkey ->
		println("-- M${monkey.idx}: ${monkey.items}")
	}
	for (i in 1..20) {
		println("- Rd $i:")
		monkeys.forEach { monkey ->
			for (j in 1..monkey.items.size) {
				monkey.inspect()
				val to = monkey.nextTo
				val item = monkey.items.removeFirst()
				monkeys[to].catch(item)
			}
		}
		monkeys.forEach {monkey ->
			println("-- M${monkey.idx}: ${monkey.items}")
		}
	}
	println("Played 20 rounds of Pt 1 Monkey in the Middle!")

	// Conduct 10K Rounds
	println("Playing 10K rounds of Pt 2 Monkey in the Middle...")
	println("- Start:")
	monkeysPt2.forEach {monkey ->
		println("-- M${monkey.idx}: ${monkey.items}")
	}
	for (i in 1..10_000) {
		if (i % 1000 == 0) println("- Rd $i:")
		monkeysPt2.forEach { monkey ->
			for (j in 1..monkey.items.size) {
				monkey.inspect { it % monkeys.map { mon -> mon.test }.reduce { prod, acc -> prod * acc } }
				val to = monkey.nextTo
				val item = monkey.items.removeFirst()
				monkeysPt2[to].catch(item)
			}
		}
		monkeysPt2.forEach { monkey ->
			if (i % 1000 == 0) println("-- M${monkey.idx}: ${monkey.items}")
		}
	}
	println("Played 10K rounds of Pt 2 Monkey in the Middle!")

	// STATS
	//- Top Two Monkeys by Inspection Pt1
	println("\nSTATS")
	val topTwoPt1 = monkeys.sortedBy { it.inspections }.asReversed().subList(0, 2)
	println("- Pt1 > Top Two Monkeys by Inspect: ${topTwoPt1.map{ it.idx }} : ${topTwoPt1.map { it.inspections }}")
	//- Monkey Business Pt1
	val monkeyBizPt1 = topTwoPt1.map{ it.inspections }.fold(1) { prod, acc -> prod * acc }
	println("- Pt1 > Monkey Business: $monkeyBizPt1")

	//- Top Two Monkeys by Inspection
	val topTwoPt2 = monkeysPt2.sortedBy { it.inspections }.asReversed().subList(0, 2)
	println("- Pt2 > Top Two Monkeys by Inspect: ${topTwoPt2.map{ it.idx }} : ${topTwoPt2.map { it.inspections }}")
	//- Monkey Business
	val monkeyBizPt2: Long = topTwoPt2.map{ it.inspections.toLong() }.fold(1) { prod, acc -> prod * acc }
	println("- Pt2 > Monkey Business: $monkeyBizPt2")
}