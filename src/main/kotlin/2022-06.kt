import java.io.File

private fun getMarker(input: String, length: Int, start: Int = 0): Int {
	input.forEachIndexed { idx, _ ->
		if ((idx >= start) && (idx < (input.length - length)) && input.substring(idx, idx + length).toList().distinct().size == length)
			return idx + length
	}
	return -1
}

fun main(args: Array<String>) {
	// Get the list file
	val file = try {
		File(args[0])
	} catch (e: Exception) {
		println("There was an issue opening the file!")
		return
	}

	// Parse Lines
	val pktMarkerLen = 4
	val msgMarkerLen = 14
	var lineNum = 0
	file.forEachLine { line ->
		val pktMarkerIdx = getMarker(line, pktMarkerLen)
		val pktMarker = line.substring(pktMarkerIdx - pktMarkerLen, pktMarkerIdx)
		val msgMarkerIdx = getMarker(line, msgMarkerLen, pktMarkerIdx)
		val msgMarker = line.substring(msgMarkerIdx - msgMarkerLen, msgMarkerIdx)

		println("${lineNum++}:\n" +
				"- Packet Index: $pktMarkerIdx, Packet Marker: $pktMarker\n" +
				"- Message Index: $msgMarkerIdx, Message Marker: $msgMarker")
	}
}