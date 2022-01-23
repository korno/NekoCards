package uk.me.mikemike.nekocards


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BitsPopupMenuItem(text: String, onClick: () -> Unit, enabled: Boolean = true){
    DropdownMenuItem(onClick = onClick, enabled = enabled) {
        Text(style=MaterialTheme.typography.button, text=text, color = MaterialTheme.colors.primary)
    }
}

@Composable
fun BitsPopupMenuItemWithIcon(text: String, onClick: () -> Unit, enabled: Boolean=true,
                                icon: ImageVector, iconContentDesc: String = String.Empty){
    DropdownMenuItem(onClick = onClick, enabled = enabled) {
        Icon(icon, modifier = Modifier.padding(end = 4.dp), contentDescription = iconContentDesc,   tint = MaterialTheme.colors.primary)
        Text(style=MaterialTheme.typography.button, text=text, color = MaterialTheme.colors.primary)
    }

}

@Composable
fun BitsIconButton(enabled: Boolean=true, icon: ImageVector, iconContentDesc: String = String.Empty, onClick: () -> Unit){
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(icon, iconContentDesc, tint=MaterialTheme.colors.primary)
    }
}

@Composable
fun BitsButtonWithText(onClick: () -> Unit, enabled: Boolean, text: String) {
    Button(onClick = onClick, enabled = enabled) {
        Text(text)
    }
}

@Composable
fun BitsOutlinedButton(onClick: () -> Unit, enabled: Boolean = true, text: String) {
    OutlinedButton(onClick = onClick, enabled = enabled) {
        Text(text)
    }
}

@Composable
fun BitsOutlinedButtonWithIcon(
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String,
    icon: ImageVector,
    iconContentDesc: String = String.Empty,
    modifier: Modifier? = null
) {
    OutlinedButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier ?: Modifier.wrapContentWidth()
    ) {
        Icon(icon, modifier = Modifier.padding(end = 4.dp), contentDescription = iconContentDesc)
        Text(text, style = MaterialTheme.typography.button)
    }
}




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BitsCardWithMenu(
    cardTitle: String,
    menuContent: @Composable (closeMenu: ()->Unit) -> Unit,
    cardContent: @Composable () -> Unit,
    cardClick: () -> Unit
) {
    var showMenu by remember {
        mutableStateOf(false)
    }
    Card(
        onClick = cardClick,
        elevation = 4.dp, modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        enabled = false
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 0.dp, 0.dp, 0.dp)
                /*.wrapContentSize(Alignment.TopEnd)*/
            ) {
                Text(
                    style = MaterialTheme.typography.h5, text = cardTitle, modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 8.dp)
                )
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.align(Alignment.TopEnd),

                    ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = stringResource(id = R.string.bits_popmenu_content_description),
                        tint = MaterialTheme.colors.primary
                    )
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        menuContent(){showMenu=false}
                    }


                }
            }
            cardContent()
        }
    }
}


@Composable
fun BitsUpdateButton(enabled: Boolean, onClick: () -> Unit) {
    BitsButtonWithText(onClick = onClick, enabled = enabled, text = stringResource(id = R.string.bits_update_item_button_label))
}

@Composable
fun BitsCreateButton(enabled: Boolean= true, onClick: () -> Unit) {
    BitsButtonWithText(onClick = onClick, enabled = enabled, text = stringResource(id = R.string.bits_create_item_button_text) )
}



@Composable
fun BitsDeleteButton(enabled: Boolean = true, onClick: () -> Unit) {
    BitsButtonWithText(
        onClick = onClick,
        enabled = enabled,
        text = stringResource(id = R.string.bits_delete_button_text)
    )
}


@Composable
fun BitsCancelButton(enabled: Boolean = true, onClick: () -> Unit) {
    BitsOutlinedButton(
        onClick = onClick,
        enabled = enabled,
        text = stringResource(id = R.string.bits_cancel_button_text)
    )
}

@Composable
fun <T> BitsEditItemDialog(
    item: T,
    edit: @Composable (T, update: (T) -> Unit) -> Unit,
    onConfirm: (T) -> Unit,
    onCancel: () -> Unit,
    isValid: (T) -> Boolean,
    creating: Boolean = true,
    title: String = String.Empty
) {
    var state by remember { mutableStateOf(item) }
    AlertDialog(onDismissRequest = onCancel,
        confirmButton = {
            if (creating) {
                BitsCreateButton(onClick = { onConfirm(state) }, enabled = isValid(state))
            } else {
                BitsUpdateButton(enabled = isValid(state), onClick = { onConfirm(state)})
            }
        },
        dismissButton = {
            BitsCancelButton() {
                onCancel()
            }
        },
        text = {
            edit(state) { state = it }
        },
        title = {Text(title)}
    )
}

@Composable
fun <T> BitsConfirmItemDeleteDialog(
    item: T,
    title: @Composable (T) -> Unit,
    message: @Composable (T) -> Unit,
    onCancel: () -> Unit,
    onConfirm: (T) -> Unit
) {
    AlertDialog(onDismissRequest = onCancel,
        confirmButton = {
            BitsDeleteButton() {
                onConfirm(item)
            }
        },
        dismissButton = {
            BitsCancelButton {
                onCancel()
            }
        },
        title = { title(item) },
        text = { message(item) })
}