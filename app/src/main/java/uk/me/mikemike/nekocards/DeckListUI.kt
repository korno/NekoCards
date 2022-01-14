package uk.me.mikemike.nekocards


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.me.mikemike.nekocards.ui.theme.NekoCardsTheme


typealias DeckMethod = (Deck) -> Unit

@Composable
fun DeckListScreenTop(nav: NavHostController) {
    val deckViewModel = viewModel<DeckViewModel>(
        key = null,
        factory = DeckViewModelFactory((LocalContext.current.applicationContext as NekoCardsApplication).deckDataRepository)
    )
    val decks: List<Deck> by deckViewModel.allDecks.observeAsState(initial = listOf())
    val lastInsertId: Long by deckViewModel.lastInsertId.observeAsState(initial = 0)
    val lastDeleteId: Long by deckViewModel.lastDeleteId.observeAsState(0)
    val context = LocalContext.current;
    val showAddConfirmation by remember(lastInsertId){
        mutableStateOf(lastInsertId > 0)
    }
    val showDeleteConfirmation by remember(lastDeleteId){
        mutableStateOf(lastDeleteId > 0)
    }
    val deleteConfirmMessage: String = remember { context.getString(R.string.deck_deleted_message)}
    val addConfirmMessage: String = remember{ context.getString(R.string.deck_created_message)}


    LaunchedEffect(key1 = showAddConfirmation){
        if(showAddConfirmation){
            Toast.makeText(context, addConfirmMessage, Toast.LENGTH_SHORT).show()
            navigateToEditDeck(lastInsertId, nav)
            deckViewModel.clearLastInsertId()
        }
    }
    LaunchedEffect(key1 = showDeleteConfirmation,){
        if(showDeleteConfirmation){
            Toast.makeText(context, deleteConfirmMessage, Toast.LENGTH_SHORT).show()
            deckViewModel.clearLastDeleteId()
        }
    }

    DeckListScreen(
        decks = decks,
        onDelete = { deckViewModel.deleteDeck(it) },
        onEdit = { navigateToEditDeck(it.deckId, nav) },
        onAdd = { deckViewModel.insertDeck(DeckWithCards(it, listOf())) },
        onChose = { navigateToQuiz(it.deckId, nav)})
}





@Composable
fun DeckListScreen(
    decks: List<Deck>,
    onChose: DeckMethod,
    onDelete: DeckMethod,
    onAdd: DeckMethod,
    onEdit: DeckMethod
) {

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var deckToDelete: Deck? = remember { null }

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.deck_list_screen_top_bar_title)) }) },
        content = {
            LazyColumn(modifier = Modifier
                .padding(it)
                .padding(bottom = 50.dp)) {
                items(decks, key = { item -> item.deckId }) {
                    deck-> SingleDeckCardDisplay(deck, {onChose(deck)}, {onEdit(deck)}, {}, {onChose(deck)},
                                            {deckToDelete=deck
                                                showDeleteConfirmDialog=true})
                }
            }
            if (showCreateDialog) {
                BitsEditItemDialog(
                    item = Deck(0, stringResource(id = R.string.new_deck_default_name),
                        stringResource(id = R.string.new_deck_default_description), stringResource(R.string.default_side_a_name),
                        stringResource(R.string.default_side_b_name)),
                    edit = { deck, update -> EditDeckPart(deck, update) },
                    onConfirm = { deck->
                        showCreateDialog = false
                        onAdd(deck)
                    } ,
                    onCancel = {showCreateDialog = false },
                    isValid = {deck -> deck.name.isNotBlank() && deck.sideAName.isNotBlank() && deck.sideBName.isNotBlank()},
                    title = stringResource(id = R.string.create_deck_dialog_title), creating = true
                )
            } else if (showDeleteConfirmDialog) {
                deckToDelete?.let {
                        deck -> DeleteDeckConfirmDialog(targetDeck = deck,
                    onConfirm = {
                        onDelete(deck)
                        showDeleteConfirmDialog = false},
                    onCancel = {showDeleteConfirmDialog=false})
                }}


        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                text = { Text(stringResource(id = R.string.create_deck_floating_action_button))},
                icon = {Icon(Icons.Filled.LibraryAdd, stringResource(id = R.string.create_deck_floating_action_icon_content_description))}
            )
        })

}


@Composable
fun EditDeckPart(deck: Deck, update: (Deck) -> Unit){
    Column {
        TextField(modifier = Modifier.padding(bottom = 8.dp, top=8.dp),
            value = deck.name,
            onValueChange = {
                deck.name = it
                update(deck)
            },
            label = { Text(stringResource(id = R.string.deck_name_label)) })
        TextField(modifier = Modifier.padding(bottom = 8.dp),
            value = deck.description,
            onValueChange = {
                deck.description = it
                update(deck)
            },
            label = { Text(stringResource(R.string.deck_description_label)) })
        TextField(modifier = Modifier.padding(bottom = 8.dp),
            value = deck.sideAName,
            onValueChange = {
                deck.sideAName = it
                update(deck)
            },
            label = { Text(stringResource(R.string.deck_description_sideA_label)) }
        )
        TextField(modifier = Modifier.padding(bottom = 8.dp),
            value = deck.sideBName,
            onValueChange = {
                deck.sideBName = it
                update(deck)
            },
            label = { Text(stringResource(R.string.deck_description_sideB_label)) }
        )
    }
}

@Composable
fun DeleteDeckConfirmDialog(targetDeck: Deck, onConfirm: DeckMethod, onCancel: () -> Unit) {
    BitsConfirmItemDeleteDialog(
        item = targetDeck,
        title = { Text(stringResource(R.string.delete_deck_dialog_title, it.name))},
        message = { Text(stringResource(R.string.delete_deck_dialog_message, it.name)) },
        onCancel = { onCancel() },
        onConfirm = {onConfirm(it)}
    )
}

@Composable
fun SingleDeckCardDisplay(deck: Deck, onSelect: DeckMethod, onEditSelect: DeckMethod, onStudySelect: DeckMethod,
            onQuizSelect: DeckMethod, onDeleteSelect: DeckMethod){
    BitsCardWithMenu(
        cardTitle = deck.name ,

        menuContent = { 
                hideMenu-> BitsPopupMenuItemWithIcon(text= stringResource(id = R.string.menu_label_edit),
                                icon = Icons.Default.ModeEdit,
                                onClick = {
                                    hideMenu()
                                    onEditSelect(deck) 
                            })
                            Divider()
                            BitsPopupMenuItemWithIcon(text = stringResource(id = R.string.menu_label_delete),
                                icon = Icons.Default.Delete,
                                onClick = {
                                    hideMenu()
                                    onDeleteSelect(deck)
                            })
         },
        cardContent = {

            Text(deck.description, modifier = Modifier.padding(8.dp))
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement=Arrangement.SpaceAround){
                BitsOutlinedButtonWithIcon(
                    onClick = { onStudySelect(deck)},
                    text = stringResource(id = R.string.button_label_deck_study),
                    icon = Icons.Filled.School,
                    iconContentDesc = stringResource(id = R.string.content_description_deck_study),
                    modifier = Modifier.weight(1f, true)
                )
                Spacer(modifier = Modifier.weight(0.1f))
                BitsOutlinedButtonWithIcon(
                    onClick = {(onQuizSelect(deck))},
                    modifier = Modifier.weight(1f, true),
                    icon =  Icons.Filled.QuestionAnswer,
                    text = stringResource(R.string.button_label_deck_quiz),
                    iconContentDesc = stringResource(id = R.string.content_description_deck_quiz)
                )

            }

                      },
        cardClick = { }
    )

}

@Preview
@Composable
fun DeckListPreview() {
    NekoCardsTheme {
        val decks =
            mutableListOf(Deck(1, "deck1", "deck1", "sideA", "sideB"), Deck(2, "deck2", "deck2", "sideA", "sideB"))
        DeckListScreen(decks, {}, {}, {}, {})
    }
}




