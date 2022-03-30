package uk.me.mikemike.nekocards

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource




@Composable
fun CardDialogX(startValues: Card,
                sideADisplayName: String = stringResource(R.string.default_side_a_name),
                sideBDisplayName: String = stringResource(R.string.default_side_b_name),
                onCancel: () -> Unit, onFinish: (Card) -> Unit)
{
    BitsEditItemDialog(item = startValues, edit = {card, update -> EditCardPart(card = card, sideADisplayName=sideADisplayName, sideBDisplayName=sideBDisplayName, update = update )}
        , onConfirm = onFinish, onCancel = onCancel, isValid = {it.sideA.isNotEmpty() && it.sideB.isNotEmpty()})
}


@Composable
fun EditCardPart(card: Card, sideADisplayName: String, sideBDisplayName: String, update: (Card) -> Unit){
    Column() {
        OutlinedTextField(
            value = card.sideA,
            onValueChange = {card.sideA = it; update(card)},
            label = { Text(sideADisplayName) }
        )
        OutlinedTextField(
            value = card.sideB,
            onValueChange = {card.sideB = it; update(card)},
            label = { Text(sideBDisplayName) }
        )
    }
}


@Composable
fun CardDialog(startValues: Card,
               sideADisplayName: String = stringResource(R.string.default_side_a_name),
               sideBDisplayName: String = stringResource(R.string.default_side_b_name),
               onCancel: () -> Unit, onFinish: (Card) -> Unit){

    var card = remember { startValues}
    var sideA by remember { mutableStateOf(startValues.sideA) }
    var sideB by remember { mutableStateOf(startValues.sideB) }


    AlertDialog(onDismissRequest = {
        onCancel()
    },
        title = {
            Text("Add New Card to deck")

        },
        text = {
            Column {
                OutlinedTextField(
                    value = sideA,
                    onValueChange = { sideA = it

                    },
                    label = { Text(sideADisplayName) })
                OutlinedTextField(
                    value = sideB,
                    onValueChange = { sideB = it
                    },
                    label = { Text(sideBDisplayName) })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    card.sideA = sideA;
                    card.sideB = sideB;
                    onFinish(card)
                }, enabled = sideA.isNotBlank() && sideB.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCancel()
                }) {
                Text("Cancel")
            }


        })



}