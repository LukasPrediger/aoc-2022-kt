package io.github.lukasprediger.aoc.days.day10

import com.github.h0tk3y.betterParse.lexer.TokenMatchesSequence
import com.github.h0tk3y.betterParse.parser.parse
import io.github.lukasprediger.aoc.common.parseString
import io.github.lukasprediger.aoc.common.shouldBe
import org.junit.jupiter.api.Test

class Day10KtTest {

    private fun tokenize(input: String): TokenMatchesSequence = CpuParser.tokenizer.tokenize(input)

    @Test
    fun `Parse negative number`() {
        CpuParser.INT.parse(tokenize("-1")) shouldBe -1
    }

    @Test
    fun `Parse add`() {
        CpuParser.ADDX.parse(tokenize("addx -1")) shouldBe listOf(CpuInstruction.AddPreload, CpuInstruction.Add(-1))
    }

    @Test
    fun `Parse noop`() {
        CpuParser.NOOP.parse(tokenize("noop")) shouldBe listOf(CpuInstruction.Noop)
    }

    @Test
    fun `Test example`() {
        val input = Day10KtTest::class.java.getResource("/Day10TestInput.txt")!!.readText()
        part1(CpuParser.parseString(input), 20..220 step 40) shouldBe 13140
    }

    @Test
    fun `Test example 2`() {
        val input = Day10KtTest::class.java.getResource("/Day10TestInput.txt")!!.readText()
        part2(CpuParser.parseString(input), 0..220 step 40).also(::println)
    }
}