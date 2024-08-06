package io.github.lukasprediger.aoc.days.day10

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import io.github.lukasprediger.aoc.common.parseString
import io.github.lukasprediger.aoc.common.readInputRaw
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun main() {
    val input = readInputRaw(10)
    val commands = CpuParser.parseString(input)

    println(part1(commands, 20..220 step 40))
    println(part2(commands, 0..240 step 40))
}

internal fun part1(input: List<CpuInstruction>, saveInterval: IntProgression): Int {
    return execute(input, saveInterval).first
}

class ComputedProperty<T>(private val computer: () -> T) : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = computer()
}

private fun execute(
    input: List<CpuInstruction>,
    saveInterval: IntProgression
): Pair<Int, String> {
    var x = 1

    val spritePosition: IntRange by ComputedProperty { x - 1..x + 1 }

    val saves = mutableListOf<Pair<Int, Int>>()
    val screen = mutableListOf<String>()
    var line = mutableListOf<Char>()

    input.forEachIndexed { cycle, instruction ->
        if (cycle + 1 in saveInterval) {
            saves.add(cycle + 1 to x)
            screen.add(String(line.toCharArray()))
            line.clear()
        }
        line.add (if (cycle/40 in spritePosition) '#' else '.')

        if (instruction is CpuInstruction.Add) {
            x += instruction.value
        }
//        println("${cycle + 1}: $instruction: $x")

    }

    return saves.sumOf { it.first * it.second } to screen.joinToString("\n")
}

internal fun part2(input: List<CpuInstruction>, saveInterval: IntProgression): String {
    return execute(input, saveInterval).second
}


internal sealed class CpuInstruction {
    object Noop : CpuInstruction() {
        override fun toString(): String = "noop"
    }

    data class Add(val value: Int) : CpuInstruction()
    object AddPreload : CpuInstruction() {
        override fun toString(): String = "AddPre"
    }
}

internal object CpuParser : Grammar<List<CpuInstruction>>() {
    val DIGITS by regexToken("-?\\d+")
    val INT by DIGITS use { text.toInt() }

    val NEWLINE by literalToken("\n")
    val WHITESPACE by regexToken("\\s+", ignore = true)

    val ADDX_TOKEN by literalToken("addx")
    val ADDX by skip(ADDX_TOKEN) and INT map { listOf(CpuInstruction.AddPreload, CpuInstruction.Add(it)) }

    val NOOP_TOKEN by literalToken("noop")
    val NOOP by NOOP_TOKEN map { listOf(CpuInstruction.Noop) }

    val commands by separatedTerms(ADDX or NOOP, NEWLINE)

    override val rootParser: Parser<List<CpuInstruction>> = commands.map { it.flatten() }

}