package uk.me.mikemike.nekocards

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import uk.me.mikemike.nekocards.ui.theme.NekoCardsTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {

    val deckViewModel  by viewModels<DeckViewModel>({DeckViewModelFactory((this.applicationContext as NekoCardsApplication).deckDataRepository)})



    private fun createDeckWithCards(cardCount: Int): DeckWithCards{
        val cards = mutableListOf<Card>()
        repeat(cardCount){
            cards.add(Card(0,0, "A$it", "B$it"))
        }
        return DeckWithCards(Deck(0, "Deck", "Description", "sideA", "sideB"), cards)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        lifecycleScope.launchWhenStarted {
            repeat(20){
                deckViewModel.insertDeck(createDeckWithCards(20))
            }
        }*/

        setContent {
            NekoCardsTheme {
                Surface() {
                    NekoCardsTop()
                }

            }
        }
    }
}


@Composable
fun NekoCardsTop() {

    val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "decks"
        ) {
            composable("decks") {
                DeckListScreenTop(navController)
            }
            composable(route = "{deckid}/edit",
                arguments = listOf(
                    navArgument("deckid") { type = NavType.LongType }
                )) { entry ->
                val id = entry.arguments!!.getLong("deckid")
                DeckEditScreenTop(deckId = id)
            }
            composable(route = "{deckid}/quiz",
                arguments = listOf(
                    navArgument("deckid") { type = NavType.LongType }
                )) { entry ->
                val id = entry.arguments!!.getLong("deckid")
                QuizScreenActivity(deckId = id)
            }
        }
}









@Composable
fun DeckEditScreenTop(deckId: Long) {

    val deckEditViewModel = viewModel<DeckEditViewModel>(
        key = null,
        factory = DeckEditViewModelFactory(
            (LocalContext.current.applicationContext as NekoCardsApplication).deckDataRepository,
            deckId
        )
    )
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

    if (editingDeck != null) {
        DeckEditScreen(
            deck = editingDeck!!,
            onAddCard = { deckEditViewModel.addCardToCurrentlyEditingDeck(it) },
            onDeleteCard = { deckEditViewModel.deleteCard(it) },
            onEditDeck = { deckEditViewModel.updateCurrentlyEditingDeck(it)},
            onEditCard = { deckEditViewModel.updateCard(it)})
    } else {
        Text("Error deck not found deckId" + deckId)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NekoCardsTheme {
        val decks =
            mutableListOf<Deck>(Deck(1, "deck1", "deck1", "sideA", "sideB"), Deck(1, "deck2", "deck2", "sideA", "sideB"))
        DeckListScreen(decks, {}, {}, {}, {})
    }
}