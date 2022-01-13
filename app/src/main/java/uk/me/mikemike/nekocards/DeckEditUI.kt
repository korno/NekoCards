package uk.me.mikemike.nekocards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

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

    Scaffold(content = {

        Column {
            Text("Name: " + deck.deck.name)
            Text("Description: " + deck.deck.description)
            Button(onClick = { showEditDialog = true }) { Text("Edit Name and Description") }
            DeckCardsList(deck.cards, onDeleteCard, {
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
        showEditDialog -> {
            /*DeckDialog(startDeck = deck.deck, onCancel = { showEditDialog = false }, onFinish = {
                showEditDialog = false
                onEditDeck(it)
            })*/
            DeckEditDialog2(deck.deck.copy(), {
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
fun DeckCardsList(cards: List<Card>, onDelete: (Card) -> Unit, onEdit: (Card) -> Unit){
    Row{
        Text("Number of Cards: " + cards.size)
    }
    LazyColumn() {
        items(cards, key = {it.cardId}) { card ->
            CardDisplay(card, onDelete, onEdit)
        }
    }
}

@Composable
fun DeckEditDialog2(deck: Deck, onConfirm: (Deck) -> Unit, onCancel: () -> Unit){
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