package uk.me.mikemike.nekocards

import java.lang.IllegalStateException
import kotlin.collections.ArrayDeque
import kotlin.math.roundToInt


data class MultipleChoiceQuestion(val question: String, val correctAnswer: String, val choices: List<String>,
                                  val sourceCard: Card)

data class QuestionResult(val question: MultipleChoiceQuestion, val result: Boolean, val answer: String)

class MultipleChoiceQuiz(public val sourceDeck: DeckWithCards, private val numberOfChoicesToAdd: Int, private val numberOfQuestions: Int = sourceDeck.cards.size) {

    private var questionMaster = ArrayDeque<MultipleChoiceQuestion>()
    private var questions = ArrayDeque<MultipleChoiceQuestion>()

    private var _questionResults = mutableListOf<QuestionResult>()
    val questionResults: List<QuestionResult>
        get() {return _questionResults}

    private var _currentQuestion: MultipleChoiceQuestion? = null
    val currentQuestion: MultipleChoiceQuestion?
        get() {return _currentQuestion}

    val questionCount: Int
        get() { return questionMaster.size}

    val numberOfCorrectAnswers: Int
        get() { return _questionResults.count{it.result }}

    val numberOfWrongAnswers: Int
        get() {return _questionResults.count{!it.result}}

    val questionMistakes: List<QuestionResult>
        get() {return _questionResults.filter { !it.result }}

    val remainingQuestionCount: Int
        get() { return questions.size}

    val isFinished: Boolean
        get() { return _currentQuestion == null}

    val lastQuestionAnswer: QuestionResult?
        get(){return questionResults.getOrNull(questionResults.size-1)}

    val correctPercentageOfTotalQuiz: Int
        get() {return (100f * (numberOfCorrectAnswers.toFloat() / numberOfQuestions)).roundToInt()}

    init {
        makeQuiz()
        startQuiz()
    }

    private fun makeQuiz(){
        questionMaster.clear()
        val numberOfQuestionsToAdd = Math.min(numberOfQuestions, sourceDeck.cards.size)
        val possibleAnswers = sourceDeck.cards.toMutableList()
        for(card in sourceDeck.cards.shuffled().take(numberOfQuestionsToAdd)){
            possibleAnswers.remove(card)
            questionMaster.addLast(MultipleChoiceQuestion(card.sideA!!, card.sideB!!,
                (possibleAnswers.take(numberOfChoicesToAdd).map{it.sideB!!} as MutableList<String>)
                    .also{
                        it.add(card.sideB!!);
                        it.shuffle()
                        },
                card))
            possibleAnswers.add(card)
        }
    }

    fun startQuiz(){
        questions.clear()
        questions.addAll(questionMaster)
        _questionResults.clear()
        moveToNextQuestion()
    }

    fun restartQuiz(remakeQuestions: Boolean = false){
        _questionResults.clear()
        if(remakeQuestions){
            makeQuiz()
        }
        startQuiz()
    }

    private fun moveToNextQuestion(){
        _currentQuestion = questions.removeLastOrNull()
    }

    fun answerCurrentQuestion(answer: String) : QuestionResult{
        if(isFinished){
            throw IllegalStateException("There is no current question - the test is finished?")
        }
        val result = QuestionResult(currentQuestion!!, _currentQuestion!!.correctAnswer.trim().equals(answer.trim(), ignoreCase = true), answer)
        _questionResults.add(result)
        moveToNextQuestion()
        return result
    }

    /* HACK to force StateFlow to re emit this is as new value in QuizViewModel  */
    override fun equals(other: Any?): Boolean {
        return false
    }

}


fun finishTestRandomly(quiz: MultipleChoiceQuiz){
    while(!quiz.isFinished){
        quiz.answerCurrentQuestion(quiz.currentQuestion!!.choices[0])
    }
}