package uk.me.mikemike.nekocards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource

@Composable
fun CardDisplay(c: Card, onDelete: (Card) -> Unit, onEdit: (Card) -> Unit){
    Row{
        Text("Side A:" + c.sideA)
        Text("Side B:" + c.sideB)
        TextButton(onClick = {onDelete(c)}) {Text("Delete")}
        TextButton(onClick = {onEdit(c)}) {Text("Edit")}
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
                TextField(
                    value = sideA,
                    onValueChange = { sideA = it

                    },
                    label = { Text(sideADisplayName) })
                TextField(
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