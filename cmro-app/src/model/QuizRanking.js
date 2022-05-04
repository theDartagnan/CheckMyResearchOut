import { makeAutoObservable } from 'mobx';

function parsePublicRanking(publicRanking) {
  if (!publicRanking || !publicRanking.length) {
    return [];
  }
  return publicRanking.map((rank, idx) => ({
    ...rank,
    lastAttempt: new Date(rank.lastAttempt),
    position: idx + 1,
  }));
}

class QuizRanking {
  _rank;

  _publicRanking;

  _quizQuestionNumber;

  constructor(rank, publicRanking, quizQuestionNumber) {
    makeAutoObservable(this, {
      _quizQuestionNumber: false,
    });
    this._rank = rank;
    this._publicRanking = parsePublicRanking(publicRanking);
    this._quizQuestionNumber = quizQuestionNumber;
  }

  get rank() {
    return this._rank;
  }

  get publicRanking() {
    return this._publicRanking;
  }

  get hasStats() {
    return this._rank >= 1 && this._rank <= this._publicRanking.length;
  }

  get myStats() {
    if (!this.hasStats) {
      return null;
    }
    return this._publicRanking[this._rank - 1];
  }

  get progression() {
    if (!this.myStats) {
      return null;
    }
    return 100.0 * (this.myStats.numSuccessfulAnswers / this._quizQuestionNumber);
  }

  get numSuccessfulAnswers() {
    if (!this.myStats) {
      return null;
    }
    return this.myStats.numSuccessfulAnswers;
  }

  get numAttempts() {
    if (!this.myStats) {
      return null;
    }
    return this.myStats.numAttempts;
  }

  get lastAttempt() {
    if (!this.myStats) {
      return null;
    }
    return this.myStats.lastAttempt;
  }
}

export default QuizRanking;
