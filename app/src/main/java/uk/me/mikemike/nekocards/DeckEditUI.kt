package uk.me.mikemike.nekocards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.me.mikemike.nekocards.ui.theme.NekoCardsTheme

typealias CardMethod = (Card)->Unit


@Composable
fun DeckEditScreen(
    deck: DeckWithCards, onAddCard: (Card) -> Unit, onDeleteCard: (Card) -> Unit,
    onEditDeck: (Deck) -> Unit, onEditCard: (Card) -> Unit
) {

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var showCardCreateDialog by remember {
        mutableStateOf(false)
    }

    var showCardEditDialog by remember {
        mutableStateOf(false)
    }

    var editingCard : Card? by remember{
        mutableStateOf(null)
    }

    var cardToDelete : Card? by remember{
        mutableStateOf(null)
    }

    Scaffold(content = {

        Column {
            Text("Name: " + deck.deck.name)
            Text("Description: " + deck.deck.description)
            Button(onClick = { showEditDialog = true }) { Text("Edit Name and Description") }
            DeckCardsList(deck, {cardToDelete=it}, {
                editingCard=it.copy()
                showCardEditDialog=true
            })
        }
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCardCreateDialog = true }
            ) {
                Icon(Icons.Filled.Add, "")
            }
        })

    when {
        cardToDelete != null -> {
            BitsConfirmItemDeleteDialog(
                item = cardToDelete!! ,
                title = { Text(stringResource(R.string.delete_card_confirmation_dialog)) } ,
                message = {Text(stringResource(R.string.delete_card_confirmation_message))} ,
                onCancel = { cardToDelete = null },
                onConfirm = {onDeleteCard(it); cardToDelete=null}
            )
        }
        showEditDialog -> {
            DeckEditDialog(deck.deck.copy(), {
                showEditDialog=false
                onEditDeck(it)},
                {showEditDialog=false}
            )
        }
        showCardCreateDialog -> {
            CardDialog(startValues = Card(0, deck.deck.deckId, "", ""),
                onCancel = { showCardCreateDialog = false },
                onFinish = {
                    showCardCreateDialog = false
                    onAddCard(it)
                },
            sideADisplayName = deck.deck.sideAName,
            sideBDisplayName = deck.deck.sideBName)
        }
        showCardEditDialog -> {
            CardDialog(startValues = editingCard!!, onCancel = {showCardEditDialog=false}, onFinish = {
                showCardEditDialog=false
                onEditCard(it)} )
        }
    }

}

@Composable
fun DeckCardsList(deck: DeckWithCards, onDelete: (Card) -> Unit, onEdit: (Card) -> Unit){

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp), verticalAlignment = Alignment.CenterVertically){
        Text(modifier= Modifier
            .weight(0.4f)
            .padding(end = 8.dp), text=deck.deck.sideAName)
        Text(modifier= Modifier
            .weight(0.4f)
            .padding(end = 8.dp), text=deck.deck.sideAName)
        Spacer(modifier=Modifier.weight(0.2f))
    }
    LazyColumn() {
        items(deck.cards, key = {it.cardId}) { card ->
            CardEditDisplay(card, onEdit, onDelete)
        }
    }
}


@Composable
fun CardEditDisplay(card: Card, onEdit: CardMethod = {}, onDelete: CardMethod = {}) {
    Divider()
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(modifier= Modifier
            .weight(0.4f)
            .padding(end = 8.dp), text=card.sideA)
        Text(modifier= Modifier
            .weight(0.4f)
            .padding(end = 8.dp), text=card.sideB)
        BitsIconButton(true, Icons.Default.Edit,  String.Empty, {onEdit(card)})
        BitsIconButton(true, Icons.Default.Delete,  String.Empty, {onDelete(card)})
    }
}

@Composable
fun DeckEditDialog(deck: Deck, onConfirm: (Deck) -> Unit, onCancel: () -> Unit){
    BitsEditItemDialog(
        item = deck,
        edit = { deck, update ->
            Column() {
                TextField(value = deck.name, onValueChange = {
                    deck.name = it
                    update(deck)
                }
                )
                TextField(value = deck.description, onValueChange = {
                    deck.description = it
                    update(deck)
                })
                TextField(value = deck.sideAName, onValueChange = {
                    deck.sideAName = it
                    update(deck)
                })
                TextField(value=deck.sideBName, onValueChange = {
                    deck.sideBName = it
                    update(deck)
                })
            }
        },
        onConfirm = onConfirm,
        onCancel = onCancel,
        isValid = {it.name.isNotEmpty()}
    )
}


@Preview
@Composable
fun DeckEditPreview(){
    NekoCardsTheme() {
        val deck = createTestDeck("name", "description", 20)
        DeckEditScreen(
            deck = deck,
            onAddCard = {},
            onDeleteCard = {},
            onEditDeck = {},
            onEditCard = {})
    }
}



