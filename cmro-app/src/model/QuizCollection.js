import {
  makeObservable, observable, override, computed, runInAction,
} from 'mobx';
import AbstractRESTEntity from './AbstractRESTEntity';
import { getQuizzesWithSimpleInfo } from '../services/RESTEndpoints';

class QuizCollection extends AbstractRESTEntity {
  _quizzes;

  constructor() {
    super();
    makeObservable(this, {
      _quizzes: observable,
      quizzes: computed,
      fetch: override,
    });
    this._quizzes = [];
  }

  get quizzes() {
    return this._quizzes;
  }

  async fetch() {
    if (this.isPending) {
      return this;
    }
    runInAction(() => {
      this._state = AbstractRESTEntity.PENDING_STATE;
    });
    try {
      const data = await getQuizzesWithSimpleInfo();
      runInAction(() => {
        this._quizzes = data;
        this._state = AbstractRESTEntity.READY_STATE;
      });
      return this;
    } catch (error) {
      runInAction(() => {
        this._state = AbstractRESTEntity.INIT_STATE;
      });
      throw error;
    }
  }
}

export default QuizCollection;
