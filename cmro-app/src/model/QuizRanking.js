import { makeAutoObservable } from 'mobx';

class QuizRanking {
  _rank;

  _publicRanking;

  constructor(rank, publicRanking) {
    makeAutoObservable(this);
    this._rank = rank;
    this._publicRanking = publicRanking ?? [];
  }

  get rank() {
    return this._rank;
  }

  get publicRanking() {
    return this._publicRanking;
  }

  get myStats() {
    if (this._rank < 0 || this._rank > this._publicRanking.length) {
      return null;
    }
    return this._publicRanking[this._rank];
  }
}

export default QuizRanking;
