package uk.me.mikemike.nekocards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.me.mikemike.nekocards.ui.theme.NekoCardsTheme

typealias CardMethod = (Card)->Unit


@Composable
fun DeckEditScreen(
    deck: DeckWithCards, onAddCard: (Card) -> Unit, onDeleteCard: (Card) -> Unit,
    onEditDeck: (Deck) -> Unit, onEditCard: (Card) -> Unit, defaultTab: Int=1
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

    var selectedTab by remember{
        mutableStateOf(defaultTab)
    }

    var tabs = listOf("Cards", "Details")
    Scaffold(
        topBar = { TopAppBar(title = { Text("Edit " + deck.deck.name) }) },

        content = {

        Column(modifier=Modifier.fillMaxWidth()) {
            TabRow(selectedTabIndex = selectedTab){
                tabs.forEachIndexed() { index, title ->
                    Tab(
                        content = { Text(modifier= Modifier.padding(8.dp), text=title) },
                        onClick = { selectedTab = index },
                        selected = index == selectedTab
                    )
                }
            }

            when(selectedTab){
                0 -> {DeckCardsList(deck, {cardToDelete=it}, {
                    editingCard=it.copy()
                    showCardEditDialog=true
                })}
                1 -> { DeckDetails(deck.deck)
                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){Button(modifier=Modifier.padding(8.dp), onClick = { showEditDialog = true }) { Text("Edit Details") }}
                   }
            }
        }
    },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {if(selectedTab == 0){
            ExtendedFloatingActionButton(
                onClick = { showCardCreateDialog = true },
                text = { Text(stringResource(id = R.string.add_card_floating_action_button_text))},
                icon = {Icon(Icons.Filled.AddBox, stringResource(id = R.string.add_card_floating_action_button_text))}
            )
        } else {}})

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
fun DeckDetails(deck: Deck){
    Text(modifier=Modifier.fillMaxWidth().padding(8.dp), textAlign = TextAlign.Center, text=deck.name, style = MaterialTheme.typography.h4)

    Text(modifier=Modifier.fillMaxWidth().padding(8.dp), text="Description", fontWeight = FontWeight.Bold)
    Text(modifier=Modifier.fillMaxWidth().padding(8.dp), text=deck.description)
    Divider()

    Text(modifier=Modifier.fillMaxWidth().padding(8.dp), text="Card Labels", fontWeight = FontWeight.Bold)
    Text(modifier=Modifier.fillMaxWidth().padding(start=8.dp, end=8.dp), text="Card Side A Name:")
    Text(modifier=Modifier.fillMaxWidth().padding(start=8.dp, end=8.dp, bottom=8.dp), text="Card Side B Name; ")
    Divider()
}

@Composable
fun DeckCardsList(deck: DeckWithCards, onDelete: (Card) -> Unit, onEdit: (Card) -> Unit){

    Row(modifier = Modifier
        .fillMaxWidth().background(MaterialTheme.colors.primaryVariant)
        .padding(10.dp), verticalAlignment = Alignment.CenterVertically){
        Text(modifier= Modifier
            .weight(0.4f)
            .padding(end = 8.dp), text=deck.deck.sideAName, fontWeight = FontWeight.Bold, color=MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primaryVariant))
        Text(modifier= Modifier
            .weight(0.4f)
            .padding(end = 8.dp), text=deck.deck.sideBName, fontWeight = FontWeight.Bold, color=MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primaryVariant))

        Spacer(modifier=Modifier.weight(0.2f))

    }
    //Divider(thickness = 5.dp)
    LazyColumn(modifier=Modifier.padding(bottom=50.dp)) {
        items(deck.cards, key = {it.cardId}) { card ->
            CardEditDisplay(card, onEdit, onDelete)
        }
    }
}


@Composable
fun CardEditDisplay(card: Card, onEdit: CardMethod = {}, onDelete: CardMethod = {}) {

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
    Divider()
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
fun DeckEditPreviewDetails(){
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

@Preview
@Composable
fun DeckEditPreviewCards(){
    NekoCardsTheme() {
        val deck = createTestDeck("name", "description", 20)
        DeckEditScreen(
            deck = deck,
            onAddCard = {},
            onDeleteCard = {},
            onEditDeck = {},
            onEditCard = {},
        defaultTab = 0)
    }
}



