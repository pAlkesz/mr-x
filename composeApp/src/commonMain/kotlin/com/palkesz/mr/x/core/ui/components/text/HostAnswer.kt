package com.palkesz.mr.x.core.ui.components.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.palkesz.mr.x.core.util.extensions.capitalizeWords
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.host_answer_suffix
import org.jetbrains.compose.resources.stringResource

@Composable
fun buildWrongHostAnswer(answer: String) = buildAnnotatedString {
    append("- ")
    withStyle(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.error,
            textDecoration = TextDecoration.LineThrough,
        )
    ) {
        append(answer.capitalizeWords())
    }
    append(stringResource(Res.string.host_answer_suffix))
}

@Composable
fun buildCorrectHostAnswer(answer: String) = buildAnnotatedString {
    append("- ")
    withStyle(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.tertiary,
            textDecoration = TextDecoration.Underline,
        )
    ) {
        append(answer.capitalizeWords())
    }
    append(stringResource(Res.string.host_answer_suffix))
}
