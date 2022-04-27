import {
  makeObservable, observable, override, computed, runInAction, action,
} from 'mobx';
import AbstractRESTEntity from './AbstractRESTEntity';
import {
  getQuiz, getQuizQuestionsNumber, getQuizUserInfoForMyself,
  getRandomQuestionToAnswerForMyself, answerQuestionForMyself,
} from '../services/RESTEndpoints';
import Question from './Question';
import QuizRanking from './QuizRanking';
import cacheSvc from '../services/CacheService';

class CurrentQuiz extends AbstractRESTEntity {
  _name;

  _fullName;

  _description;

  _questionsNumber;

  _quizUserInfo; // {successfullyAnsweredQuestions, canAnswerAQuestion, lastAnswerSuccess}

  _currentQuestion;

  _ranking;

  constructor(name) {
    super();
    makeObservable(this, {
      _name: observable,
      _fullName: observable,
      _description: observable,
      _questionsNumber: observable,
      _quizUserInfo: observable,
      _currentQuestion: observable,
      _ranking: observable,
      name: computed,
      fullName: computed,
      description: computed,
      questionsNumber: computed,
      quizUserInfo: computed,
      currentQuestion: computed,
      ranking: computed,
      fetch: override,
      retrieveAQuestion: false,
      retrieveRanking: action,
      renewQuestion: action,
    });
    this._name = name;
    this._fullName = null;
    this._description = null;
    this._currentQuestion = null;
    this._ranking = null;
  }

  get name() {
    return this._name;
  }

  get fullName() {
    return this._fullName;
  }

  get description() {
    return this._description;
  }

  get questionsNumber() {
    return this._questionsNumber;
  }

  get quizUserInfo() {
    return this._quizUserInfo;
  }

  get currentQuestion() {
    return this._currentQuestion;
  }

  get ranking() {
    return this._ranking;
  }

  async fetch() {
    runInAction(() => {
      this._state = AbstractRESTEntity.PENDING_STATE;
    });
    try {
      const quiz = await getQuiz({ quizName: this._name });
      const [resQuestionsNum, resUserInfo] = await Promise.all([
        getQuizQuestionsNumber({ quizName: this._name }),
        getQuizUserInfoForMyself({ quizName: this._name }),
      ]);
      runInAction(() => {
        this._fullName = quiz.fullName;
        this._description = quiz.description;
        this._questionsNumber = resQuestionsNum;
        this._quizUserInfo = resUserInfo;
        this._state = AbstractRESTEntity.READY_STATE;
        cacheSvc.setLocal('LAST_QUIZ_NAME', this._name);
      });
    } catch (error) {
      console.warn(`Unable to retrieve quiz: ${error.message}`);
      runInAction(() => {
        this._state = AbstractRESTEntity.INIT_STATE;
      });
    }
    return this;
  }

  async answerQuestion() {
    if (!this._currentQuestion) {
      throw new Error('Cannot answer to no question');
    }
    const propositionIndices = [...this._currentQuestion.currentAnswerIndices];
    console.log('propositionIndices: ', propositionIndices);
    const res = await answerQuestionForMyself({
      quizName: this._name,
      questionId: this._currentQuestion.id,
      propositionIndices,
    });
    runInAction(() => {
      this._quizUserInfo = res;
      this._currentQuestion = null;
    });
    return this._quizUserInfo.lastAnswerSuccess;
  }

  async retrieveRanking() {
    const rawRanking = await getRandomQuestionToAnswerForMyself({ quizName: this._name });
    const ranking = QuizRanking(rawRanking.rank, rawRanking.publicRanking);
    runInAction(() => {
      this._ranking = ranking;
    });
    return ranking;
  }

  async renewQuestion() {
    if (this._currentQuestion) {
      throw new Error('There is a question to answer already');
    }
    if (!this._currentQuestionPromise) {
      const p = await getRandomQuestionToAnswerForMyself({ quizName: this._name })
        .then((rawQuestion) => new Question(
          rawQuestion.id,
          rawQuestion.quizName,
          rawQuestion.title,
          rawQuestion.answerPropositions,
          rawQuestion.author,
          rawQuestion.publication,
        )).finally(() => {
          runInAction(() => {
            this._currentQuestionPromise = null;
          });
        });
      runInAction(() => {
        this._currentQuestion = p;
      });
      return p;
    }
    return this._currentQuestionPromise;
  }
}

export default CurrentQuiz;

// getQuizUnsucceededQuestionsNumberForMyself
//
// getRandomQuestionsToAnswerForMyself
