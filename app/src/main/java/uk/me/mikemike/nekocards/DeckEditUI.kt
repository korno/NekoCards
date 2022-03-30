package uk.me.mikemike.nekocards

import android.widget.Toast
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.me.mikemike.nekocards.ui.theme.NekoCardsTheme

@Composable
fun DeckEditScreenTop(deckId: Long, nav: NavHostController) {

    val deckEditViewModel = viewModel<DeckEditViewModel>(
        key = null,
        factory = DeckEditViewModelFactory(
            (LocalContext.current.applicationContext as NekoCardsApplication).deckDataRepository,
            deckId
        )
    )
    val status: LoadStatus? by deckEditViewModel.loadStatus.observeAsState()
    val editingDeck: DeckWithCards? by deckEditViewModel.currentlyEditingDeck.observeAsState(initial = null)
    val lastCardInsertId: Long by deckEditViewModel.lastCardInsertId.observeAsState(initial = 0)
    var showCardAddedConfirmation = remember(lastCardInsertId){
        lastCardInsertId
    }
    val lastCardDeleteId: Long by deckEditViewModel.lastCardDeleteId.observeAsState(initial = 0)
    var showCardDeletedConfirmation = remember(lastCardDeleteId){
        lastCardDeleteId
    }
    val context = LocalContext.current;

    val cardDeletedMessage = remember{context.getString(R.string.card_deleted_message)}
    val cardAddedMessage = remember{context.getString(R.string.card_created_message)}

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = showCardAddedConfirmation){
        if(showCardAddedConfirmation > 0){
            Toast.makeText(context, cardAddedMessage, Toast.LENGTH_SHORT).show()
            showCardAddedConfirmation = 0
        }
    }
    LaunchedEffect(key1 = showCardDeletedConfirmation){
        if(showCardDeletedConfirmation > 0){
            Toast.makeText(context, cardDeletedMessage, Toast.LENGTH_SHORT).show()
            showCardDeletedConfirmation = 0
        }
    }

    if(status == LoadStatus.ERROR){
       LaunchedEffect(status == LoadStatus.ERROR){
           navigateToDeckList(nav)
       }
    }
    else {
        if (editingDeck != null) {
            DeckEditScreenX(
                deck = editingDeck!!,
                onAddCard = { deckEditViewModel.addCardToCurrentlyEditingDeck(it) },
                onDeleteCard = { deckEditViewModel.deleteCard(it) },
                onEditDeck = { deckEditViewModel.updateCurrentlyEditingDeck(it) },
                onEditCard = { deckEditViewModel.updateCard(it) })
        } else {
            // the deck was not found die

        }
    }
}


@Composable
fun DeckEditTopBar(deck: DeckWithCards){
    TopAppBar(
        title = { Text(stringResource(R.string.edit_deck_appbar_title, deck.deck.name))}
    )
}

@Composable
fun DeckEditAddCardFloatingButton(onClick: () -> Unit){
    ExtendedFloatingActionButton(
        onClick = onClick,
        text = { Text(stringResource(id = R.string.add_card_floating_action_button_text))},
        icon = {Icon(Icons.Filled.AddBox, stringResource(id = R.string.add_card_floating_action_button_text))}
            )
}

@Composable
fun DeckEditScreenX(deck: DeckWithCards, onAddCard: CardMethod, onDeleteCard: CardMethod,
                    onEditDeck: DeckMethod, onEditCard: CardMethod, defaultTab: Int=0
) {
    var showEditDeckDetailsDialog by remember {
        mutableStateOf(false)
    }

    var showCardCreateDialog by remember {
        mutableStateOf(false)
    }

    var editingCard : Card? by remember{
        mutableStateOf(null)
    }

    var cardToDelete : Card? by remember{
        mutableStateOf(null)
    }

    val bitsScaffoldTabs = listOf(
        BitsTabScreenItem(
            topBar = {DeckEditTopBar(deck)},
            content = {
                DeckCardsList(deck, {cardToDelete=it}, {
                    editingCard=it.copy()
                })
            },
            floatingActionButton = {DeckEditAddCardFloatingButton { showCardCreateDialog = true } },
            name = stringResource(id = R.string.edit_deck_tabs_cards)
        ),
        BitsTabScreenItem(
            topBar = {DeckEditTopBar(deck)},
            content = {
                DeckDetails(deck.deck)
                Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){Button(modifier=Modifier.padding(8.dp), onClick = { showEditDeckDetailsDialog = true }) { Text("Edit Details") }}
            },
            floatingActionButton = {},
            name = stringResource(id = R.string.edit_deck_tabs_details)
        )
    )

    BitsTabsScreenScaffold(tabs = bitsScaffoldTabs, defaultSelectedTab = defaultTab)

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
        showEditDeckDetailsDialog -> {
            DeckEditDialog(deck.deck.copy(), {
                showEditDeckDetailsDialog=false
                onEditDeck(it)},
                {showEditDeckDetailsDialog=false}
            )
        }
        showCardCreateDialog -> {
            CardDialogX(startValues = Card(0, deck.deck.deckId, "", ""),
                onCancel = { showCardCreateDialog = false },
                onFinish = {
                    showCardCreateDialog = false
                    onAddCard(it)
                },
                sideADisplayName = deck.deck.sideAName,
                sideBDisplayName = deck.deck.sideBName)
        }
        editingCard != null -> {
            CardDialogX(startValues = editingCard!!.copy(), onCancel = {editingCard=null}, onFinish = {
                editingCard=null
                onEditCard(it)},
            sideBDisplayName = deck.deck.sideBName,
            sideADisplayName = deck.deck.sideAName)
        }
    }


}



@Composable
fun DeckDetails(deck: Deck){
    Text(modifier= Modifier
        .fillMaxWidth()
        .padding(8.dp), textAlign = TextAlign.Center, text=deck.name, style = MaterialTheme.typography.h4)

    BitsBoldedHeading(stringResource(R.string.deck_description_label))
    Text(modifier= Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 8.dp), text=deck.description)

    BitsBoldedHeading(stringResource(R.string.deck_card_labels))
    Text(modifier= Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 8.dp), text=stringResource(R.string.deck_sideA_label_with_template, deck.sideAName))
    Text(modifier= Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp), text=stringResource(R.string.deck_sideB_label_with_template, deck.sideBName))

    Divider()
}

@Composable
fun DeckCardsList(deck: DeckWithCards, onDelete: CardMethod, onEdit: CardMethod){

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.primaryVariant)
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
fun DeckEditDialog(deck: Deck, onConfirm: DeckMethod, onCancel: () -> Unit){
    BitsEditItemDialog(
        item = deck,
        edit = {deck, update -> EditDeckPart(deck = deck, update = update)},
        onConfirm = onConfirm,
        onCancel = onCancel,
        isValid = {it.name.isNotEmpty() && it.sideAName.isNotEmpty() && it.sideBName.isNotEmpty()}
    )
}


@Preview
@Composable
fun DeckEditPreviewDetails(){
    NekoCardsTheme() {
        val deck = createTestDeck("name", "description", 20)
        DeckEditScreenX(
            deck = deck,
            onAddCard = {},
            onDeleteCard = {},
            onEditDeck = {},
            onEditCard = {},
        defaultTab = 1)
    }
}

@Preview
@Composable
fun DeckEditPreviewCards(){
    NekoCardsTheme() {
        val deck = createTestDeck("name", "description", 20)
        DeckEditScreenX(
            deck = deck,
            onAddCard = {},
            onDeleteCard = {},
            onEditDeck = {},
            onEditCard = {},
        defaultTab = 0)
    }
}



