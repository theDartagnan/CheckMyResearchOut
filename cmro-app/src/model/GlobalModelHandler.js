/* eslint-disable class-methods-use-this */
import { makeAutoObservable } from 'mobx';
import LoggedUser from './LoggedUser';
import CurrentQuiz from './CurrentQuiz';
import {
  createUser, startAccountValidation, checkAccountValidation,
  validateAccount, startPasswordRenewal, checkPasswordRenewal, renewPassword,
} from '../services/RESTEndpoints';

class GlobalModelHandler {
  _loggedUser = new LoggedUser();

  _currentQuiz = new CurrentQuiz();

  constructor() {
    makeAutoObservable(this);
  }

  get loggedUser() {
    return this._loggedUser;
  }

  get currentQuiz() {
    return this._currentQuiz;
  }

  get isUserOrQuizBusy() {
    return this._loggedUser.isPending || this._currentQuiz.isPending;
  }

  /* eslint-disable-next-line class-methods-use-this */
  async createAccount({
    mail, password, firstname, lastname,
  }) {
    await createUser({
      mail, password, firstname, lastname,
    }, true);
    await startAccountValidation({ encodedMail: encodeURI(mail) }, true);
    return true;
  }

  async attemptAutoLogin(withQuiz = false) {
    if (this.isUserOrQuizBusy) {
      console.warn('GMH: Operation running on user or quiz. cancel the autologin order');
      return false;
    }
    try {
      if (this._loggedUser.isInit) {
        await this._loggedUser.fetch(true);
      }
      if (withQuiz && this._currentQuiz.isInit) {
        await this._currentQuiz.attemptFindPreviousQuiz(true);
      }
      return true;
    } catch (error) {
      return false;
    }
  }

  async sendValidationMail({ mail }) {
    await startAccountValidation({ encodedMail: encodeURI(mail) }, true);
    return true;
  }

  async checkAccountValidation({ encodedMail }) {
    try {
      await checkAccountValidation({ encodedMail }, true);
      return true;
    } catch (error) {
      if (error.status === 404) {
        return false;
      }
      throw error;
    }
  }

  async validateAccount({ encodedMail, token }) {
    await validateAccount({ encodedMail, token }, true);
    return true;
  }

  async sendPasswordRenewalMail({ mail }) {
    await startPasswordRenewal({ encodedMail: encodeURI(mail) }, true);
    return true;
  }

  async checkPasswordRenewal({ encodedMail }) {
    try {
      await checkPasswordRenewal({ encodedMail }, true);
      return true;
    } catch (error) {
      if (error.status === 404) {
        return false;
      }
      throw error;
    }
  }

  async renewPassword({ encodedMail, token, password }) {
    await renewPassword({ encodedMail, token, password }, true);
    return true;
  }
}

export default GlobalModelHandler;
