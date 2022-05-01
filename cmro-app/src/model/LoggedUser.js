import {
  makeObservable, observable, override, computed, action, runInAction,
} from 'mobx';
import AbstractRESTEntity from './AbstractRESTEntity';
import {
  getMyself, patchMyself, deleteMyself, login, logout,
} from '../services/RESTEndpoints';

class LoggedUser extends AbstractRESTEntity {
  _mail;

  _lastname;

  _firstname;

  _admin;

  constructor() {
    super();
    makeObservable(this, {
      _mail: observable,
      _lastname: observable,
      _firstname: observable,
      _admin: observable,
      mail: computed,
      lastname: computed,
      firstname: computed,
      isAdmin: computed,
      fetch: override,
      update: override,
      delete: override,
      login: action,
      logout: action,
      reset: action,
    });
  }

  get mail() {
    return this._mail;
  }

  get lastname() {
    return this._lastname;
  }

  get firstname() {
    return this._firstname;
  }

  get isAdmin() {
    return this._admin;
  }

  reset() {
    this._mail = null;
    this._firstname = null;
    this._lastname = null;
    this._admin = false;
    this._state = AbstractRESTEntity.INIT_STATE;
  }

  async login({ mail, password, rememberMe }, silentOnError = false) {
    if (!this.isInit) {
      throw new Error('User must is already logged in');
    }
    runInAction(() => {
      this.reset();
      this._state = AbstractRESTEntity.PENDING_STATE;
    });
    await login({ mail, password, rememberMe }, true);
    await this.fetch(silentOnError, true);
    return this;
  }

  async logout() {
    if (!this.isReady) {
      throw new Error('User must be ready to logout');
    }
    try {
      await logout();
    } finally {
      runInAction(() => {
        this.reset();
      });
    }
    return true;
  }

  async fetch(silentOnError, forceFetch) {
    if (this.isPending && !forceFetch) {
      return this;
    }
    runInAction(() => {
      this._state = AbstractRESTEntity.PENDING_STATE;
    });
    try {
      const data = await getMyself(silentOnError);
      runInAction(() => {
        this.reset();
        this._mail = data.mail;
        this._lastname = data.lastname;
        this._firstname = data.firstname;
        this._admin = data.admin;
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

  async update({ firstname, lastname, password }) {
    if (!this.isReady) {
      throw new Error('User must be ready to be updated');
    }
    const data = await patchMyself({ firstname, lastname, password });
    runInAction(() => {
      this._lastname = data.lastname;
      this._firstname = data.firstname;
      this._admin = data.admin;
    });
    return this;
  }

  async delete() {
    if (!this.isReady) {
      throw new Error('User must be ready to be updated');
    }
    try {
      await deleteMyself({});
      runInAction(() => {
        this.reset();
      });
      return true;
    } finally {
      await logout();
    }
  }
}

export default LoggedUser;
