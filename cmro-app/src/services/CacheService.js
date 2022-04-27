import StorageEmulator from './StorageEmulator';

class CacheService {
  _localStorage = null;

  _sessionStorage = null;

  constructor() {
    this._localStorage = window.localStorage ?? new StorageEmulator();
    this._sessionStorage = window.sessionStorage ?? new StorageEmulator();
  }

  getLocal(key) {
    if (!key) {
      throw new Error(`Invalid key "${key}"`);
    }
    const res = this._localStorage.getItem(key);
    if (res === null) {
      return res;
    }
    return JSON.parse(res);
  }

  setLocal(key, value) {
    if (!key) {
      throw new Error(`Invalid key "${key}"`);
    }
    if (value === undefined) {
      this._localStorage.removeItem(key);
    } else {
      this._localStorage.setItem(key, JSON.stringify(value));
    }
  }

  getSession(key) {
    if (!key) {
      throw new Error(`Invalid key "${key}"`);
    }
    const res = this._sessionStorage.getItem(key);
    if (res === null) {
      return res;
    }
    return JSON.parse(res);
  }

  setSession(key, value) {
    if (!key) {
      throw new Error(`Invalid key "${key}"`);
    }
    if (value === undefined) {
      this._sessionStorage.removeItem(key);
    } else {
      this._sessionStorage.setItem(key, JSON.stringify(value));
    }
  }
}

const CACHE_SVC = new CacheService();

export default CACHE_SVC;
