import { makeAutoObservable } from 'mobx';

class Question {
  _id;

  _quizName;

  _title;

  _answerPropositions;

  _author;

  _publication;

  _currentAnswerIndices;

  constructor(id, quizName, title, answerPropositions, author, publication) {
    makeAutoObservable(this, {
      hasProposition: false,
    });
    this._id = id;
    this._quizName = quizName;
    this._title = title;
    this._answerPropositions = answerPropositions;
    this._author = author;
    this._publication = publication;
    this._currentAnswerIndices = new Set();
  }

  get id() {
    return this._id;
  }

  get title() {
    return this._title;
  }

  get propositions() {
    if (!this._answerPropositions) {
      return [];
    }
    // Return a shuffled array of {title, propositionIdx}
    return this._answerPropositions
      .map(({ title }, index) => ({ title, index }))
      .sort(() => Math.random() - 0.5);
  }

  get author() {
    return this._author;
  }

  get publication() {
    return this._publication;
  }

  get currentAnswerIndices() {
    return this._currentAnswerIndices;
  }

  resetAnswerIndices() {
    this._currentAnswerIndices.clear();
  }

  selectProposition(proposition) {
    this._currentAnswerIndices.add(proposition.index);
  }

  unselectProposition(proposition) {
    this._currentAnswerIndices.delete(proposition.index);
  }

  switchProposition(proposition) {
    if (this._currentAnswerIndices.has(proposition.index)) {
      this._currentAnswerIndices.delete(proposition.index);
    } else {
      this._currentAnswerIndices.add(proposition.index);
    }
  }

  hasProposition(proposition) {
    return this._currentAnswerIndices.has(proposition.index);
  }
}

export default Question;
