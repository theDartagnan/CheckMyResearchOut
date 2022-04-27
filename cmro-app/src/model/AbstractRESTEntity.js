/* eslint-disable class-methods-use-this */
import {
  makeObservable, observable, computed, action,
} from 'mobx';

const INIT_STATE = 0;
const PENDING_STATE = 1;
const READY_STATE = 2;
const ERROR_STATE = 3;

class AbstractRestEntity {
  _state = INIT_STATE;

  _refreshPromise = null;

  constructor() {
    makeObservable(this, {
      _state: observable,
      _refreshPromise: false,
      state: computed,
      isInit: computed,
      isPending: computed,
      isReady: computed,
      isError: computed,
      entityPromise: false,
      fetch: action,
      update: action,
      delete: action,
    });
  }

  get state() {
    return this._state;
  }

  get isInit() {
    return this._state === INIT_STATE;
  }

  get isPending() {
    return this._state === PENDING_STATE;
  }

  get isReady() {
    return this._state === READY_STATE;
  }

  get isError() {
    return this._state === ERROR_STATE;
  }

  get entityPromise() {
    return this._refreshPromise;
  }

  async fetch() {
    throw new Error('Not implemented');
  }

  async update() {
    throw new Error('Not implemented');
  }

  async delete() {
    throw new Error('Not implemented');
  }
}

Object.defineProperties(AbstractRestEntity, {
  INIT_STATE: {
    value: INIT_STATE,
    writable: false,
  },
  PENDING_STATE: {
    value: PENDING_STATE,
    writable: false,
  },
  READY_STATE: {
    value: READY_STATE,
    writable: false,
  },
  ERROR_STATE: {
    value: ERROR_STATE,
    writable: false,
  },
});

export default AbstractRestEntity;
