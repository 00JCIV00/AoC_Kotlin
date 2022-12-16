import java.io.File

val hands = mapOf(
	"Rock" to "Scissors",
	"Paper" to "Rock",
	"Scissors" to "Paper"
)

val handScore = mapOf(
	"Rock" to 1,
	"Paper" to 2,
	"Scissors" to 3
)

val outcomes = mapOf(
	"X" to "Lose",
	"Y" to "Draw",
	"Z" to "Win"
)

val outcomeScore = mapOf(
	"Lose" to 0,
	"Draw" to 3,
	"Win" to 6
)

data class Round(
	val roundNum: Int,
	val opp: String,
	val you: String
) {
	val outcome = getOutcome(opp, you)
	val hScore = handScore[you] ?:0
	val oScore = outcomeScore[outcome] ?: 0
	val score = hScore + oScore

	override fun toString(): String {
		return "Rd $roundNum: $opp vs $you. $outcome, Score: $hScore + $oScore = $score"
	}
}

/**
 * Returns the actual hand of a [symbol].
 */
fun getHand(symbol: String): String {
	return when(symbol) {
		"A", "X" -> "Rock"
		"B", "Y" -> "Paper"
		"C", "Z" -> "Scissors"
		else -> throw Exception("How did you even?!")
	}
}

/**
 * Returns the outcome of a round based on opponents hand ([opp]) and your hand ([you]).
 */
fun getOutcome(opp: String, you: String): String {
	return when {
		opp == hands[you] -> "Win"
		you == hands[opp] -> "Lose"
		else -> "Draw"
	}
}

/**
 * Returns the hand you should play based on the opponent's hand ([opp]) and the desired [outcome].
 */
fun fixOutcome(opp: String, outcome: String): String {
	return when(outcome) {
		"Lose" -> hands[opp] ?: ""
		"Win" -> hands.filterValues { it == opp }.keys.toList()[0]
		else -> opp
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

	// Parse each round
	println("Parsing each round...")
	val roundsPt1 = mutableListOf<Round>()
	val roundsPt2 = mutableListOf<Round>()
	var roundNum = 1
	file.forEachLine { line ->
		run {
			val (opp, you) = line.split(" ").map { getHand(it) }.zipWithNext()[0]
			val round = Round(roundNum, opp, you)
			roundsPt1.add(round)
			println("- $round!")
		}
		run {
			val symbols = line.split(" ").zipWithNext()[0]
			val opp = getHand(symbols.first)
			val you = fixOutcome(opp, outcomes[symbols.second] ?: "Draw")
			val round = Round(roundNum++, opp, you)
			roundsPt2.add(round)
			println("- $round!")

		}
	}
	println("Parsed each round!")

	// Get stats
	val totalScorePt1 = roundsPt1.sumOf { round -> round.score }
	println("The total score of all ${roundsPt1.size} Part 1 rounds is $totalScorePt1!")
	val totalScorePt2 = roundsPt2.sumOf { round -> round.score }
	println("The total score of all ${roundsPt2.size} Part 2 rounds is $totalScorePt2!")

}

