import { makeAutoObservable, runInAction } from 'mobx';
import LoggedUser from './LoggedUser';
import CurrentQuiz from './CurrentQuiz';
import { createUser } from '../services/RESTEndpoints';
import cacheSvc from '../services/CacheService';

class GlobalModelHandler {
  _loggedUser = new LoggedUser();

  _currentQuiz = null;

  _currentQuizLoading = false;

  constructor() {
    makeAutoObservable(this);
  }

  get loggedUser() {
    return this._loggedUser;
  }

  get currentQuiz() {
    return this._currentQuiz;
  }

  isAuthenticated() {
    return this._loggedUser.isAuthenticated;
  }

  async attemptAutoLogin() {
    return this._loggedUser.fetch();
  }

  async login({ username, password, rememberMe }) {
    return this._loggedUser.login({ username, password, rememberMe });
  }

  /* eslint-disable-next-line class-methods-use-this */
  async createAccount({ mail, password, rememberMe }) {
    await createUser({ mail, password, rememberMe });
    return true;
  }

  async switchQuiz(quizName) {
    if (this._currentQuizLoading) {
      return this._currentQuizLoading;
    }
    console.log("GMH: switchQuiz");
    try {
      if (this._currentQuiz && this._currentQuiz.name === quizName) {
        if (this._currentQuiz.isInit || this._currentQuiz.isReady) {
          console.log("Fetching the current quiz");
          return this._currentQuiz.fetch();
        }
        return this._currentQuiz;
      }
      console.log("Creating and fetchiung the current quiz");
      const quiz = new CurrentQuiz(quizName);
      runInAction(() => {
        this._currentQuizLoading = quiz;
      });
      await quiz.fetch();
      runInAction(() => {
        this._currentQuiz = quiz;
      });
      return quiz;
    } finally {
      runInAction(() => {
        this._currentQuizLoading = null;
      })
    }

  }

  async attemptFindPreviousQuiz() {
    if (this._currentQuizLoading) {
      return this._currentQuizLoading;
    }
    console.log("GMH: attemptFindPReviousQuiz");
    try {
      const lastQuizName = cacheSvc.getLocal('LAST_QUIZ_NAME');
      if (!lastQuizName) {
        throw new Error('No Previous quiz');
      }
      console.log('last quiz name: ' + lastQuizName);
      const quiz = new CurrentQuiz(lastQuizName);
      runInAction(() => {
        this._currentQuizLoading = quiz;
      })
      await quiz.fetch();
      runInAction(() => {
        this._currentQuiz = quiz;
      });
      return quiz;
    } finally {
      runInAction(() => {
        this._currentQuizLoading = null;
      })
    }

  }
}

export default GlobalModelHandler;
