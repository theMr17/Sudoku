package com.mr_17.sudoku.domain

import java.io.Serializable

/**
 * A Node in a sudoku puzzle is denoted by:
 * - A value or color, which is an integer denoted by the set of allowed numbers in the sudoku
 * - A list of relative x and y value where:
 *   - top left = x(0), y(0) (assuming 0 based indexing)
 *   - bottom right = x(n-1), y(n-1), where n is the largest integer in the set of allowed numbers
 */

data class SudokuNode(
    val x: Int,
    val y: Int,
    var color: Int = 0,
    var readOnly: Boolean = true
): Serializable {
    override fun hashCode(): Int {
        return getHash(x, y)
    }
}

internal fun getHash(x: Int, y: Int): Int {
    val newX = x * 100
    return "$newX$y".toInt()
}
