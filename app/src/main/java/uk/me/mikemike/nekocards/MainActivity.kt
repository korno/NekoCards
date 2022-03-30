package uk.me.mikemike.nekocards

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uk.me.mikemike.kotlinwani.RequestStatus
import uk.me.mikemike.kotlinwani.SubjectResourceRequestResult
import uk.me.mikemike.kotlinwani.UserRequestResult
import uk.me.mikemike.nekocards.kotlinwani.KotlinWaniViewModel
import uk.me.mikemike.nekocards.ui.theme.NekoCardsTheme


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
                Surface {
                    KotlinWani()
                //NekoCardsTop()
                }

            }
        }
    }
}


@Composable
fun KotlinWani(){

    val kwViewModel = viewModel<KotlinWaniViewModel>(
        key = null,
        factory = KotlinWaniViewModel.Factory("", LocalContext.current.applicationContext as Application)
    )
    val userName: UserRequestResult? by kwViewModel.user.observeAsState();
    val firstSubject: SubjectResourceRequestResult? by kwViewModel.theFirstSubject.observeAsState()

    Column() {

        userName!!.let {
            when (it.status) {
                RequestStatus.LOADING -> {
                    Text("Loading")
                }
                RequestStatus.FAILED -> {
                    Text("Unable to Load user data error:" + it.error)
                }
                RequestStatus.LOADED -> {
                    Column() {
                        Text("Loaded: User name is :" + it.data!!.data.userName)
                        Text("User profile URL is: " + it.data!!.data.profileUrl)
                        Text("User type:" + it.data!!.data.subscription.type)
                        Text("account created on: " + it.data!!.data.createdDate.toString())
                        Text("lesson order:" + it.data!!.data.preferences.lessonPresentationOrder)
                    }
                }
            }
        }

        firstSubject!!.let {
            when (it.status) {
                RequestStatus.LOADING -> {
                    Text("Loading Subject")
                }
                RequestStatus.FAILED -> {
                    Text("Unable to Load subject data error:" + it.error )
                }
                RequestStatus.LOADED -> {
                    Column() {
                        Text("type =" + it.data!!.data.toString())
                    }
                }
            }
        }

    }

    if(userName!!.status ==  RequestStatus.LOADING){
        Text("Loading User Data")
    }
}

@Composable
fun NekoCardsTop() {

    val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "main"
        ) {
            composable("main") {
                DeckListScreenTop(navController)
            }
            composable(route = "{deckid}/edit",
                arguments = listOf(
                    navArgument("deckid") { type = NavType.LongType }
                )) { entry ->
                val id = entry.arguments!!.getLong("deckid")
                DeckEditScreenTop(deckId = id, navController )
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











@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NekoCardsTheme {
        val decks =
            mutableListOf<Deck>(Deck(1, "deck1", "deck1", "sideA", "sideB"), Deck(1, "deck2", "deck2", "sideA", "sideB"))
        DeckListScreen(decks, {}, {}, {}, {})
    }
}