package uk.me.mikemike.nekocards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.me.mikemike.nekocards.ui.theme.NekoCardsTheme
import kotlin.math.roundToInt

@Composable
fun QuizScreenActivity(deckId: Long){
    val quizViewModel = viewModel<QuizViewModel>(
        key = null,
        factory =QuizViewModelFactory((LocalContext.current.applicationContext as NekoCardsApplication).deckDataRepository, deckId)
    )
    val quiz: MultipleChoiceQuiz? by quizViewModel.quiz.observeAsState()
    val lastAnswerResult by quizViewModel.lastResult.observeAsState()
    var showMistakeScreen by remember(lastAnswerResult) {
        mutableStateOf(
            if(lastAnswerResult == null){
                false}
            else{
                !lastAnswerResult!!.result
            }
        )
    }

    quiz?.let {
        when {
            showMistakeScreen -> {
                QuizScreen(quiz = it, true,
                    OnAnswerChosen = {
                        showMistakeScreen=false;
                    })
            }
            it.isFinished -> {
                QuizResults(quiz = it)
            }
            else -> {
                QuizScreen(quiz = it, false,
                    OnAnswerChosen = {
                        quizViewModel.answerCurrentQuestion(it)
                    })
            }
        }
    }
}

@Composable
fun QuizScreen(quiz: MultipleChoiceQuiz, showLastAnswerMode: Boolean,
               OnAnswerChosen: (String) -> Unit,
        ){
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text=stringResource(R.string.quiz_screen_title_bar, quiz.sourceDeck.deck.name))
        })}) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            ) {
            QuizStatus(quiz = quiz)
            if (showLastAnswerMode) {
                Question(
                    question = quiz.lastQuestionAnswer!!.question,
                    OnAnswerChosen,
                    stringResource(R.string.quiz_question_title, quiz.sourceDeck.deck.sideAName),
                    stringResource(
                        id = R.string.quiz_answer_title, quiz.sourceDeck.deck.sideBName
                    ),
                    showWrongAnswerMode = showLastAnswerMode
                )
            } else {
                Question(
                    question = quiz.currentQuestion!!,
                    OnAnswerChosen,
                    stringResource(R.string.quiz_question_title, quiz.sourceDeck.deck.sideAName),
                    stringResource(
                        id = R.string.quiz_answer_title, quiz.sourceDeck.deck.sideBName
                    ),
                    showWrongAnswerMode = false
                )

            }
        }
    }

}

@Composable
fun QuizStatus(quiz: MultipleChoiceQuiz) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.primaryVariant)
        .padding(top = 8.dp, bottom = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
        Text(color=MaterialTheme.colors.onPrimary, text=
        stringResource(
            id = R.string.quiz_question_count,
            quiz.questionCount - quiz.remainingQuestionCount,
            quiz.questionCount
        ))
        Text(color=MaterialTheme.colors.onPrimary, text=stringResource(R.string.quiz_correct_question_count, quiz.numberOfCorrectAnswers))
        Text(color=MaterialTheme.colors.onPrimary, text=stringResource(R.string.quiz_incorrect_question_count, quiz.numberOfWrongAnswers))
}
}

@Composable
fun QuizResults(quiz: MultipleChoiceQuiz){
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text=stringResource(R.string.quiz_score_title, quiz.sourceDeck.deck.name))
        })}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            QuizStatus(quiz = quiz)
            if (quiz.is100PercentPerfect) {
                Column(modifier=Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h1,
                        text = stringResource(
                            R.string.quiz_score_label,
                            quiz.correctPercentageOfTotalQuiz
                        ),
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        text = stringResource(R.string.quiz_perfect_score_label),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h4
                    )
                }

            } else {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1,
                    text = stringResource(
                        R.string.quiz_score_label,
                        quiz.correctPercentageOfTotalQuiz
                    ),
                    color = MaterialTheme.colors.secondary
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.h4,
                    text = stringResource(R.string.quiz_mistakes_title)
                )


                Column(
                    modifier = Modifier.padding(
                        top = 10.dp,
                        bottom = 0.dp,
                        start = 10.dp,
                        end = 10.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 0.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(0.5f), text = quiz.sourceDeck.deck.sideAName,
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            modifier = Modifier.weight(0.5f), text = quiz.sourceDeck.deck.sideBName,
                            style = MaterialTheme.typography.subtitle1
                        )

                    }
                    Divider()
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .padding(10.dp)
                )
                {
                    items(
                        quiz.questionMistakes,
                        key = { result -> result.question.sourceCard.cardId }) { result ->
                        QuestionResultItem(result)
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionResultItem(res: QuestionResult){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 8.dp, top = 10.dp, bottom = 10.dp)) {
        Text(modifier = Modifier.weight(0.5f), text = res.question.sourceCard.sideA, textAlign = TextAlign.Left, color=MaterialTheme.colors.error)
        Text(modifier = Modifier.weight(0.5f), text =res.question.sourceCard.sideB, textAlign = TextAlign.Left, color=MaterialTheme.colors.error)
    }
    Row(modifier=Modifier.fillMaxWidth()){
        Divider()
    }
}

@Composable
fun Question(question: MultipleChoiceQuestion, OnAnswerChosen: (String) -> Unit,
             questionTitle: String = String.Empty, answerTitle: String = String.Empty, showWrongAnswerMode: Boolean = false){
    Column() {

        Card( elevation = 4.dp, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .weight(0.4f)) {
            CardTitle(title = questionTitle)
            Column (verticalArrangement = Arrangement.Center){
                Text( modifier = Modifier.fillMaxWidth(),
                    text = question.question, textAlign = TextAlign.Center, style=MaterialTheme.typography.h5)

            }
        }

        if(showWrongAnswerMode){
            Card( elevation = 4.dp, modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(0.4f)) {
                Column(modifier=Modifier.fillMaxHeight()) {
                    CardTitle(title = answerTitle)
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .fillMaxHeight()
                    ) {
                        for (choice in question.choices) {
                            if(question.correctAnswer.equals(choice, true)) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                        .fillMaxWidth(0.7f),
                                    onClick = { OnAnswerChosen(choice) },
                                    enabled = true  )
                                { Text(text = choice) }
                            }
                            else {
                                Button(
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                        .fillMaxWidth(0.7f),
                                    onClick = { OnAnswerChosen(choice) },
                                    enabled = false
                                )
                                { Text(text = choice) }
                            }
                        }
                    }
                }
            }
        }
        else{
            Card( elevation = 4.dp, modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(0.4f)) {
                Column(modifier=Modifier.fillMaxHeight()) {
                    CardTitle(title = answerTitle)
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .fillMaxHeight()
                    ) {
                        for (choice in question.choices) {
                            Button(
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .fillMaxWidth(0.7f),
                                onClick = { OnAnswerChosen(choice) }) { Text(text = choice) }
                        }
                    }
                }
            }
        }


    }

}


@Composable
fun CardTitle(title: String){
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, start = 8.dp, end = 8.dp)){
        Text(modifier=Modifier.padding(bottom = 8.dp), text=title)
        Divider()
    }
}

@Composable
fun QuestionMistake(question: QuestionResult, onOk: () -> Unit ){
    Column() {
        Text(text = "You made a mistake, idiot!")
        Button(onClick = onOk){Text("Next Question")}
    }
}

@Composable
@Preview(group ="Test Results")
fun QuestionResultPreview(){
    NekoCardsTheme {
        val quiz: MultipleChoiceQuiz =
            with(MultipleChoiceQuiz(createTestDeck("Test", "Test description", 20), 3)) {
                finishTestRandomly(this)
                this
            }
        QuizResults(quiz)
    }
}

@Composable
@Preview
fun QuestionPreview() {
    NekoCardsTheme {
        val deck = createTestDeck("Test", "Test description", 20)
        QuizScreen(
            quiz = MultipleChoiceQuiz(deck, 3),
            OnAnswerChosen = {},
            showLastAnswerMode = false
        )
    }
}

@Composable
@Preview
fun QuestionPreviewWrongAnswer(){
    NekoCardsTheme {
        val deck = createTestDeck("Test", "Test description", 20)
        QuizScreen(
            quiz = with(MultipleChoiceQuiz(deck, 3))
            {
                this.answerCurrentQuestion("this is definitely wrong")
                this
            },
            OnAnswerChosen = {},
            showLastAnswerMode = true
        )
    }
}


@Composable
@Preview(group="Test Results")
fun QuestionResultsPerfect() {
    NekoCardsTheme {
        val deck = createTestDeck("Test", "Test Description", 20)
        val quiz = with(MultipleChoiceQuiz(deck, 3)) {
            finishTestPerfectly(this)
            this
        }
        QuizResults(quiz)
    }
}