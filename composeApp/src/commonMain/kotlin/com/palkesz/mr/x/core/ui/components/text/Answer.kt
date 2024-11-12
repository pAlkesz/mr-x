package com.palkesz.mr.x.core.ui.components.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.palkesz.mr.x.core.util.extensions.capitalizeWords
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.host_answer_suffix
import mrx.composeapp.generated.resources.player_answer_suffix
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
fun buildCorrectHostAnswer(
    answer: String,
    highlightColor: Color = MaterialTheme.colorScheme.tertiary,
) = buildAnnotatedString {
    append("- ")
    withStyle(
        style = SpanStyle(
            color = highlightColor,
            textDecoration = TextDecoration.Underline,
        )
    ) {
        append(answer.capitalizeWords())
    }
    append(stringResource(Res.string.host_answer_suffix))
}

@Composable
fun buildWrongPlayerAnswer(answer: String, owner: String) = buildAnnotatedString {
    append("- ")
    withStyle(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.error,
            textDecoration = TextDecoration.LineThrough,
        )
    ) {
        append(answer.capitalizeWords())
    }
    append(stringResource(Res.string.player_answer_suffix, owner))
}

@Composable
fun buildCorrectPlayerAnswer(
    answer: String,
    owner: String,
    highlightColor: Color = MaterialTheme.colorScheme.tertiary
) = buildAnnotatedString {
    append("- ")
    withStyle(
        style = SpanStyle(
            color = highlightColor,
            textDecoration = TextDecoration.Underline,
        )
    ) {
        append(answer.capitalizeWords())
    }
    append(stringResource(Res.string.player_answer_suffix, owner))
}

@Composable
fun buildCorrectAnswer(
    answer: String,
    highlightColor: Color = MaterialTheme.colorScheme.tertiary,
) = buildAnnotatedString {
    append("- ")
    withStyle(
        style = SpanStyle(
            color = highlightColor,
            textDecoration = TextDecoration.Underline,
        )
    ) {
        append(answer.capitalizeWords())
    }
}
