class StorageEmulator {
  _cache = {};

  getItem(key) {
    return this._cache[key];
  }

  setItem(key, value) {
    this._cache[key] = value;
  }
}

export default StorageEmulator;
