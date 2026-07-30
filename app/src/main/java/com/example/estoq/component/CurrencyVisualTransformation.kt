package com.example.estoq.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CurrencyVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }

        if (digits.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val trimmed = digits.trimStart('0').ifEmpty { "0" }

        val formatted = when {
            trimmed.length <= 2 -> trimmed
            else -> {
                val cents = trimmed.takeLast(2)
                val reais = trimmed.dropLast(2)
                val formattedReais = reais.reversed().chunked(3).joinToString(".").reversed()
                "$formattedReais,$cents"
            }
        }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = CurrencyOffsetMapping(digits, formatted)
        )
    }
}

class CurrencyOffsetMapping(
    private val rawDigits: String,
    private val formatted: String
) : OffsetMapping {

    private val rawToFormatted: List<Int> = buildList {
        var rawIndex = 0
        for ((formattedIndex, ch) in formatted.withIndex()) {
            if (rawIndex < rawDigits.length && ch == rawDigits[rawIndex]) {
                add(formattedIndex)
                rawIndex++
            }
        }
    }

    private val formattedToRaw: List<Int> = buildList {
        var rawIndex = 0
        for ((formattedIndex, ch) in formatted.withIndex()) {
            if (rawIndex < rawDigits.length && ch == rawDigits[rawIndex]) {
                add(rawIndex)
                rawIndex++
            } else if (ch.isDigit()) {
                if (rawIndex < rawDigits.length) {
                    add(rawIndex)
                    rawIndex++
                }
            }
        }
    }

    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 0) return 0
        if (offset >= rawDigits.length) return formatted.length
        return rawToFormatted.getOrElse(offset) { formatted.length }
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 0) return 0
        if (offset >= formatted.length) return rawDigits.length
        return formattedToRaw.getOrElse(offset - 1) { rawDigits.length }
    }
}
