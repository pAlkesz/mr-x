package com.palkesz.mr.x.core.ui.helpers

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object QuestionMarkTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = text.text + "?"
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset.coerceIn(0, transformedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset.coerceIn(0, text.length)
            }
        }
        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }
}
