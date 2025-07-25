package com.example.exerlog.core

import android.content.Context
import androidx.annotation.StringRes
import com.example.exerlog.R

object TipoSet {
    @StringRes
    val WARMUP = R.string.tipo_warmup
    @StringRes
    val EASY = R.string.tipo_easy
    @StringRes
    val NORMAL = R.string.tipo_normal
    @StringRes
    val HARD = R.string.tipo_hard
    @StringRes
    val DROP = R.string.tipo_drop


    private val order = listOf(WARMUP, EASY, NORMAL, HARD, DROP)

    /** Devuelve el siguiente tipo como string traducido */
    fun nextLabel(context: Context, currentLabel: String): String {
        val currentIndex = order.indexOfFirst { context.getString(it).equals(currentLabel, ignoreCase = true) }
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % order.size
        return context.getString(order[nextIndex])
    }

    /** Devuelve el siguiente tipo como recurso ID */
    @StringRes
    fun nextRes(@StringRes current: Int): Int {
        val currentIndex = order.indexOf(current)
        val nextIndex = (currentIndex + 1) % order.size
        return order[nextIndex]
    }

    /** Devuelve todos los tipos como lista de Strings traducidos */
    fun allLabels(context: Context): List<String> = order.map { context.getString(it) }
}