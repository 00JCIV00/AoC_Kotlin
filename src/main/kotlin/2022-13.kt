import java.io.File

private fun findEnd(string: String, start: Int = 0, startChar: Char = '[', endChar: Char = ']'): Int {
	var stack = 1
	return run search@ {
		string.forEachIndexed { idx, char ->
			if (idx > start) {
				when (char) {
					startChar -> stack++
					endChar -> stack--
					else -> {}
				}
				if (stack == 0) return@search idx
			}
		}
		-1
	}
}
private data class PacketData(val value: Int? = null, val list: List<PacketData>? = null): Comparable<PacketData> { // This could probably be better done with Generics in the future
	override fun compareTo(other: PacketData): Int {
		//println("L: $other\nvs\nR: $this\n")
		return when {
			// Int to Int
			value != null && other.value != null -> value.compareTo(other.value)
			// Int to List
			value != null && other.list != null -> PacketData(list = listOf(this)).compareTo(other)
			// List to Int
			list != null && other.value != null -> this.compareTo(PacketData(list = listOf(other)))
			// List to List
			else -> {
				val oList = other.list
				list!!.forEachIndexed { idx, item ->
					if (idx > oList!!.lastIndex) return 1
					val compare = item.compareTo(oList[idx])
					if (compare != 0) return compare
				}
				list.size.compareTo(oList!!.size)
			}
		}
	}

	override fun toString(): String {
		return when {
			value != null -> value.toString()
			list != null -> buildString {
				append('[')
				list.forEachIndexed { idx, data ->
					append(data)
					if (idx != list.lastIndex) append(",")
				}
				append(']')
			}
			else -> "Empty PacketData"
		}
	}
}
private class Packet(val input: String): Comparable<Packet> {
 	companion object {
		fun getData(input: String): List<PacketData> {
			return buildList {
				if (input.isEmpty()) add(PacketData(list = emptyList()))
				else {
					var next = -1
					var nextVal = ""
					input.forEachIndexed { idx, char ->
						if (idx <= next) return@forEachIndexed
						when {
							char == '[' -> {
								next = findEnd(input, idx)
								val subInput = input.substring(idx + 1, next)
								if (subInput.isNotEmpty()) add(PacketData(list = getData(subInput)))
								else add(PacketData(list = emptyList()))
							}
							char.isDigit() -> nextVal += char
							nextVal.isNotBlank() ->	{
								add(PacketData(value = nextVal.toInt()))
								nextVal = ""
							}
						}
					}
					if (nextVal.isNotBlank()) add(PacketData(value = nextVal.toInt()))
				}
			}
		}
	}
	val data = getData(input)
	override fun compareTo(other: Packet): Int {
		return data[0].compareTo(other.data[0])
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

	// Generate Packets and Packet Pairs
	println("Generating Packets and Packet Pairs...")
	val packets = buildList {
		file.readLines().filter { it != "" }.forEach { rawPacket -> add(Packet(rawPacket)) }
	}
	val packetPairs = packets.chunked(2).map { Pair(it[0], it[1]) }
	println("Generated ${packets.size} Packets and ${packetPairs.size} Packet Pairs!")
	packetPairs.forEachIndexed { idx, pair ->
		println(
			"- Pair: ${idx + 1} \n" +
			"-- Order: ${pair.second > pair.first}\n" +
			"-- L: ${pair.first.data[0]}\n" +
			"-- R: ${pair.second.data[0]}\n"
		)
	}

	// Sort all Packets
	println("Sorting Packets...")
	val sortedPackets = buildList {
		addAll(packets)
		add(Packet("[[2]]"))
		add(Packet("[[6]]"))
	}.sorted()
	sortedPackets.forEachIndexed { idx, pkt -> println("- ${(idx + 1).toString().padStart(3, '0')}: ${pkt.data}") }
	println("Sorted Packets!")

	// STATS
	println("\nSTATS:")
	//- Sum of Properly Ordered Indexes
	val sumProperIdx = packetPairs.foldIndexed(0) { idx, sum, pair -> if (pair.second > pair.first) sum + idx + 1 else sum }
	println("- Sum of Properly Ordered Indexes: $sumProperIdx")
	//- Product of Divider Indexes
	val prodDivs = (sortedPackets.indexOfFirst { it.input == "[[2]]" } + 1) * (sortedPackets.indexOfFirst { it.input == "[[6]]" } + 1)
	println("- Product of Divider Indexes: $prodDivs")
}