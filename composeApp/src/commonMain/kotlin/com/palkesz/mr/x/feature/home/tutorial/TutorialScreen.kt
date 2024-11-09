package com.palkesz.mr.x.feature.home.tutorial

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkesz.mr.x.core.ui.components.titlebar.CenteredTitleBar
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_flag_check
import mrx.composeapp.generated.resources.ic_mystery
import mrx.composeapp.generated.resources.ic_question_lifecycle
import mrx.composeapp.generated.resources.ic_quiz
import mrx.composeapp.generated.resources.ic_raised_hand
import mrx.composeapp.generated.resources.tutorial_fifth_point_description
import mrx.composeapp.generated.resources.tutorial_fifth_point_title
import mrx.composeapp.generated.resources.tutorial_first_point_description
import mrx.composeapp.generated.resources.tutorial_first_point_title
import mrx.composeapp.generated.resources.tutorial_fourth_point_description
import mrx.composeapp.generated.resources.tutorial_fourth_point_title
import mrx.composeapp.generated.resources.tutorial_second_point_description
import mrx.composeapp.generated.resources.tutorial_second_point_title
import mrx.composeapp.generated.resources.tutorial_third_point_description
import mrx.composeapp.generated.resources.tutorial_third_point_title
import mrx.composeapp.generated.resources.tutorial_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TutorialScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenteredTitleBar(title = stringResource(Res.string.tutorial_title))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TutorialItem(
                title = stringResource(Res.string.tutorial_first_point_title),
                description = stringResource(Res.string.tutorial_first_point_description),
                imageVector = vectorResource(Res.drawable.ic_flag_check),
            )
            TutorialItem(
                title = stringResource(Res.string.tutorial_second_point_title),
                description = stringResource(Res.string.tutorial_second_point_description),
                imageVector = vectorResource(Res.drawable.ic_quiz),
            )
            TutorialItem(
                title = stringResource(Res.string.tutorial_third_point_title),
                description = stringResource(Res.string.tutorial_third_point_description),
                imageVector = vectorResource(Res.drawable.ic_question_lifecycle),
            )
            TutorialItem(
                title = stringResource(Res.string.tutorial_fourth_point_title),
                description = stringResource(Res.string.tutorial_fourth_point_description),
                imageVector = vectorResource(Res.drawable.ic_raised_hand),
            )
            TutorialItem(
                title = stringResource(Res.string.tutorial_fifth_point_title),
                description = stringResource(Res.string.tutorial_fifth_point_description),
                imageVector = vectorResource(Res.drawable.ic_mystery),
            )
        }
    }
}

@Composable
private fun TutorialItem(title: String, description: String, imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        tint = MaterialTheme.colorScheme.secondary,
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
        text = title,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    )
    Text(
        text = description,
        modifier = Modifier.padding(bottom = 12.dp),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}
