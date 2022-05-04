import {
  makeObservable, observable, override, computed, runInAction, action,
} from 'mobx';
import AbstractRESTEntity from './AbstractRESTEntity';
import {
  getQuiz, getQuizQuestionsNumber, getQuizUserInfoForMyself,
  getRandomQuestionToAnswerForMyself, answerQuestionForMyself,
  getQuizRankingForMyself,
} from '../services/RESTEndpoints';
import Question from './Question';
import QuizRanking from './QuizRanking';
import cacheSvc from '../services/CacheService';

const QUIZ_NAME_LOCAL_ST_NAME = 'LAST_QUIZ_NAME';

class CurrentQuiz extends AbstractRESTEntity {
  _name;

  _fullName;

  _description;

  _questionsNumber;

  _quizUserInfo; // {successfullyAnsweredQuestions, canAnswerAQuestion, lastAnswerSuccess}

  _currentQuestion;

  _currentQuestionPromise;

  _ranking;

  _rankingPromise;

  constructor() {
    super();
    makeObservable(this, {
      _name: observable,
      _fullName: observable,
      _description: observable,
      _questionsNumber: observable,
      _quizUserInfo: observable,
      _currentQuestion: observable,
      _currentQuestionPromise: observable,
      _ranking: observable,
      _rankingPromise: observable,
      name: computed,
      fullName: computed,
      description: computed,
      questionsNumber: computed,
      quizUserInfo: computed,
      currentQuestion: computed,
      ranking: computed,
      reset: action,
      fetch: override,
      retrieveAQuestion: false,
      retrieveRanking: action,
      renewQuestion: action,
    });
    this._name = null;
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

  get questionLoading() {
    return this._currentQuestionPromise !== null;
  }

  get ranking() {
    return this._ranking;
  }

  reset() {
    this._name = null;
    this._fullName = null;
    this._description = null;
    this._questionsNumber = null;
    this._quizUserInfo = null;
    this._currentQuestion = null;
    this._ranking = null;
    this._state = AbstractRESTEntity.INIT_STATE;
  }

  async fetch({ quizName }, silentOnError) {
    if (!quizName) {
      throw new Error('No Previous quiz');
    }
    if (this.isPending) {
      return this;
    }
    runInAction(() => {
      this._state = AbstractRESTEntity.PENDING_STATE;
    });
    try {
      const quiz = await getQuiz({ quizName }, silentOnError);
      const [resQuestionsNum, resUserInfo] = await Promise.all([
        getQuizQuestionsNumber({ quizName }),
        getQuizUserInfoForMyself({ quizName }, silentOnError),
      ]);
      runInAction(() => {
        this.reset();
        this._name = quizName;
        this._fullName = quiz.fullName;
        this._description = quiz.description;
        this._questionsNumber = resQuestionsNum;
        this._quizUserInfo = resUserInfo;
        this._state = AbstractRESTEntity.READY_STATE;
        cacheSvc.setLocal(QUIZ_NAME_LOCAL_ST_NAME, this._name);
      });
      return this;
    } catch (error) {
      console.warn(`Unable to retrieve quiz: ${error.message}`);
      runInAction(() => {
        this._state = AbstractRESTEntity.INIT_STATE;
      });
      throw error;
    }
  }

  async renewQuestion() {
    if (this._currentQuestion) {
      throw new Error('There is a question to answer already');
    }
    if (!this._currentQuestionPromise) {
      const p = getRandomQuestionToAnswerForMyself({ quizName: this._name })
        .then((rawQuestion) => (rawQuestion ? new Question(
          rawQuestion.id,
          rawQuestion.quizName,
          rawQuestion.title,
          rawQuestion.answerPropositions,
          rawQuestion.author,
          rawQuestion.publication,
        ) : null));
      runInAction(() => {
        this._currentQuestionPromise = p;
      });
      try {
        const question = await p;
        runInAction(() => {
          this._currentQuestion = question;
        });
        return question;
      } catch (error) {
        console.warn(`Unable to retrieve the question: ${error.message}`);
        throw error;
      } finally {
        runInAction(() => {
          this._currentQuestionPromise = null;
        });
      }
    }
    return this._currentQuestionPromise;
  }

  async answerQuestion() {
    if (!this._currentQuestion) {
      throw new Error('Cannot answer to no question');
    }
    const propositionIndices = [...this._currentQuestion.currentAnswerIndices];
    try {
      const res = await answerQuestionForMyself({
        quizName: this._name,
        questionId: this._currentQuestion.id,
        propositionIndices,
      });
      runInAction(() => {
        this._currentQuestion = null;
        this._quizUserInfo = res;
      });
      return this._quizUserInfo.lastAnswerSuccess;
    } catch (error) {
      console.warn(`Unable to answer question: ${error.message}`);
      throw error;
    }
  }

  async retrieveRanking() {
    if (this._rankingPromise) {
      return this._rankingPromise;
    }
    try {
      this._rankingPromise = getQuizRankingForMyself({ quizName: this._name })
        .then((data) => new QuizRanking(data.rank, data.publicRanking, this._questionsNumber));
      const ranking = await this._rankingPromise;
      runInAction(() => {
        this._ranking = ranking;
      });
      return ranking;
    } catch (error) {
      console.warn(`Unable to retrieve ranking: ${error.message}`);
      throw error;
    } finally {
      this._rankingPromise = null;
    }
  }

  async attemptFindPreviousQuiz(silentOnError = false) {
    if (this.isPending) {
      return this._currentQuizLoading;
    }
    const lastQuizName = cacheSvc.getLocal(QUIZ_NAME_LOCAL_ST_NAME);
    if (!lastQuizName) {
      throw new Error('No Previous quiz');
    }
    try {
      await this.fetch({ quizName: lastQuizName }, silentOnError);
      return this;
    } catch (error) {
      console.warn('Error fetching previous quiz. Reset local cache last quiz name');
      cacheSvc.setLocal(QUIZ_NAME_LOCAL_ST_NAME);
      throw error;
    }
  }
}

export default CurrentQuiz;

// getQuizUnsucceededQuestionsNumberForMyself
//
// getRandomQuestionsToAnswerForMyself
