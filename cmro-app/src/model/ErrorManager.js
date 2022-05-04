import { makeAutoObservable } from 'mobx';

class LastErrorManager {
  _lastError = null;

  constructor() {
    makeAutoObservable(this);
  }

  get hasError() {
    return this._lastError !== null;
  }

  get error() {
    return this._lastError;
  }

  addError(title = 'Erreur', content = 'Erreur inconnue') {
    this._lastError = { title, content };
  }

  removeError() {
    this._lastError = null;
  }
}

// class ErrorManager {
//   _errorIdxCounter = 0;
//
//   _errors = [];
//
//   constructor() {
//     makeAutoObservable(this, {
//       _errorIdxCounter: false,
//     });
//   }
//
//   get errors() {
//     return this._errors;
//   }
//
//   addError(content = 'Erreur inconnue', title = 'Erreur') {
//     this._errorIdxCounter += 1;
//     this._errors.push({ id: this._errorIdxCounter, title, content });
//   }
//
//   removeErrorById(id) {
//     const errorIdx = this._errors.findIndex((err) => err.id === id);
//     if (errorIdx >= 0) {
//       this._errors.splice(errorIdx, 1);
//     }
//   }
// }

export default LastErrorManager;
