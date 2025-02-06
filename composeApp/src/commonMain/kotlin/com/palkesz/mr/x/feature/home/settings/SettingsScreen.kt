package com.palkesz.mr.x.feature.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.ui.helpers.bold
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.cancel_button_label
import mrx.composeapp.generated.resources.confirm_button_label
import mrx.composeapp.generated.resources.delete_account_dialog_message
import mrx.composeapp.generated.resources.delete_account_dialog_title
import mrx.composeapp.generated.resources.delete_account_setting_label
import mrx.composeapp.generated.resources.ic_delete
import mrx.composeapp.generated.resources.settings_screen_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel<SettingsViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    SettingsScreenContent(
        viewState = viewState,
        onDeleteAccountClicked = viewModel::onDeleteAccountClicked,
        onDeleteAccountConfirmClicked = viewModel::onDeleteAccountConfirmClicked,
        onRetry = viewModel::onRetry,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun SettingsScreenContent(
    viewState: ViewState<SettingsViewState>,
    onDeleteAccountClicked: () -> Unit,
    onDeleteAccountConfirmClicked: () -> Unit,
    onRetry: () -> Unit,
    onEventHandled: () -> Unit,
) {
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.settings_screen_title),
        )
    )
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        var isDeleteAccountDialogOpen by remember { mutableStateOf(value = false) }
        HandleEvent(
            event = state.event,
            showDeleteAccountDialog = { isDeleteAccountDialogOpen = true },
            onEventHandled = onEventHandled,
        )
        if (isDeleteAccountDialogOpen) {
            DeleteAccountDialog(
                onConfirmClicked = onDeleteAccountConfirmClicked,
                onCancelClicked = { isDeleteAccountDialogOpen = false }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SettingsCard(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                onClick = onDeleteAccountClicked,
            ) {
                Row(
                    modifier = Modifier.padding(all = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        imageVector = vectorResource(Res.drawable.ic_delete),
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(Res.string.delete_account_setting_label),
                        style = MaterialTheme.typography.titleMedium.bold(),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth().widthIn(max = 488.dp),
        onClick = onClick,
        shape = RoundedCornerShape(size = 20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        content = content,
    )
}

@Composable
private fun DeleteAccountDialog(onConfirmClicked: () -> Unit, onCancelClicked: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onCancelClicked) {
        Column(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(size = 24.dp)
            ).padding(all = 24.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(Res.string.delete_account_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = stringResource(Res.string.delete_account_dialog_message),
                style = MaterialTheme.typography.labelLarge,
            )
            Row(modifier = Modifier.align(alignment = Alignment.End)) {
                TextButton(onClick = onCancelClicked) {
                    Text(text = stringResource(Res.string.cancel_button_label))
                }
                TextButton(onClick = onConfirmClicked) {
                    Text(
                        text = stringResource(Res.string.confirm_button_label),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun HandleEvent(
    event: SettingsEvent?,
    showDeleteAccountDialog: () -> Unit,
    onEventHandled: () -> Unit,
) {
    HandleEventEffect(event) { settingsEvent, _, _, _ ->
        when (settingsEvent) {
            is SettingsEvent.ShowDeleteAccountDialog -> {
                showDeleteAccountDialog()
            }
        }
        onEventHandled()
    }
}
