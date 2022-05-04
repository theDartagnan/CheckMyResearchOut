/* eslint-disable no-console, no-unused-vars */
function waitToSeconds(seconds = 1) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
    }, seconds * 1000);
  });
}

const emHandler = {
  errorManager: null,
};

export function setErrorManager(errorManager) {
  emHandler.errorManager = errorManager;
}

const raiseError = {
  login: true,
  getMyself: false,
  getQuiz: false,
  getQuizQuestionsNumber: false,
  getQuizUserInfoForMyself: false,
  checkAccountValidation: false,
  validateAccount: false,
  checkPasswordRenewal: false,
  renewPassword: false,
  getQuizRankingForMyself: false,

};

// LOGIN/LOGOUT

export async function login({ mail, password, rememberMe }, silentOnError) {
  if (!mail || !password) {
    throw new Error('Missing mail or password.');
  }
  console.log('login...');
  const goodAuth = mail === 'jean.bon@mail.com';
  await waitToSeconds();
  if (raiseError.login || !goodAuth) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur Login test', 'details erreur');
    }
    throw new Error('Erreur Login test');
  }
  return true;
}

export async function logout(silentOnError) {
  console.log('logout...');
  await waitToSeconds();
  if (raiseError.logout) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur Logout test', 'details erreur');
    }
    throw new Error('Erreur Logout test');
  }
  return true;
}

// USER
export async function getMyself(silentOnError) {
  console.log('getMyself...');
  await waitToSeconds();
  if (raiseError.getMyself) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur getMyself test', 'details erreur');
    }
    throw new Error('Erreur getMyself test');
  }
  return {
    mail: 'jean.bon@mail.com',
    firstname: 'jean',
    lastname: 'bon',
    admin: false,
  };
}

export async function createUser({
  mail, firstname, lastname, password,
}, silentOnError) {
  if (!mail || !firstname || !lastname || !password) {
    throw new Error('Missing mail, firstname, lastname or password.');
  }
  console.log('create user...');
  const duplicateAuth = mail === 'jean.bon@mail.com';
  await waitToSeconds();
  if (raiseError.createUser || duplicateAuth) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur createUser test', 'details erreur');
    }
    throw new Error('Erreur createUser test');
  }
  return {
    mail, firstname, lastname,
  };
}

export async function patchMyself({ firstname, lastname, password }, silentOnError) {
  console.log('patchMyself: ', firstname, lastname, password);
  await waitToSeconds();
  if (raiseError.patchMyself) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur patchMyself test', 'details erreur');
    }
    throw new Error('Erreur patchMyself test');
  }
  return {
    mail: 'jean.bon@mail.com', firstname, lastname,
  };
}

export async function deleteMyself(silentOnError) {
  console.log('logout...');
  await waitToSeconds();
  if (raiseError.deleteMyself) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur deleteMyself test', 'details erreur');
    }
    throw new Error('Erreur deleteMyself test');
  }
  return true;
}

// USER ACCOUNT
export async function startAccountValidation({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  console.log('start account validation...');
  const existingAcc = encodedMail === encodeURI('jean.bon@mail.com') || encodedMail === encodeURI('new@mail');
  await waitToSeconds();
  if (raiseError.startAccountValidation || !existingAcc) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur startAccountValidation test', 'details erreur');
    }
    throw new Error('Erreur startAccountValidation test');
  }
  return true;
}

export async function checkAccountValidation({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  console.log('checkAccountValidation...');
  await waitToSeconds();
  if (raiseError.checkAccountValidation) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur checkAccountValidation test', 'details erreur');
    }
    const e = new Error('Erreur checkAccountValidation test');
    e.status = 404;
    throw e;
  }
  return true;
}

export async function validateAccount({ encodedMail, token }, silentOnError) {
  if (!encodedMail || !token) {
    throw new Error('Missing encodeMail or token.');
  }
  console.log('validateAccount...');
  await waitToSeconds();
  if (raiseError.validateAccount) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur validateAccount test', 'details erreur');
    }
    throw new Error('Erreur validateAccount test');
  }
  return true;
}

export async function startPasswordRenewal({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  console.log('startPasswordRenewal...');
  await waitToSeconds();
  if (raiseError.startPasswordRenewal) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur startPasswordRenewal test', 'details erreur');
    }
    throw new Error('Erreur startPasswordRenewal test');
  }
  return true;
}

export async function checkPasswordRenewal({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  console.log('checkPasswordRenewal...');
  await waitToSeconds();
  if (raiseError.checkPasswordRenewal) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur checkPasswordRenewal test', 'details erreur');
    }
    const e = new Error('Erreur checkPasswordRenewal test');
    e.status = 404;
    throw e;
  }
  return true;
}

export async function renewPassword({ encodedMail, token, password }, silentOnError) {
  if (!encodedMail || !token || !password) {
    throw new Error('Missing encodeMail or token or password.');
  }
  console.log('renewPassword...');
  await waitToSeconds();
  if (raiseError.renewPassword) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur renewPassword test', 'details erreur');
    }
    throw new Error('Erreur renewPassword test');
  }
  return true;
}

// QUIZ, QUESTIONS, ANSWER
export async function getQuizzesWithSimpleInfo(silentOnError) {
  console.log('getQuizzesWithSimpleInfo...');
  await waitToSeconds();
  if (raiseError.getQuizzesWithSimpleInfo) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur getQuizzesWithSimpleInfo test', 'details erreur');
    }
    throw new Error('Erreur getQuizzesWithSimpleInfo test');
  }
  return [
    { id: 'aa', name: 'Quiz1', fullName: 'Quiz 1' },
    { id: 'bb', name: 'Quiz2', fullName: 'Quiz 2' },
    { id: 'cc', name: 'Quiz3', fullName: 'Quiz 3' },
  ];
}

export async function getQuiz({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuiz...');
  await waitToSeconds();
  if (raiseError.getQuiz) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur getQuiz test', 'details erreur');
    }
    throw new Error('Erreur getQuiz test');
  }
  return {
    id: 'aa',
    name: quizName,
    fullName: `Name ${quizName}`,
    description: `Description ${quizName}`,
  };
}

export async function getQuizQuestionsNumber({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuizQuestionsNumber...');
  await waitToSeconds();
  if (raiseError.getQuizQuestionsNumber) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur getQuizQuestionsNumber test', 'details erreur');
    }
    throw new Error('Erreur getQuizQuestionsNumber test');
  }
  return 10;
}

export async function getQuizUserInfoForMyself({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuizUnsucceededQuestionsNumberForMyself...');
  await waitToSeconds();
  if (raiseError.getQuizUserInfoForMyself) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur getQuizUserInfoForMyself test', 'details erreur');
    }
    throw new Error('Erreur getQuizUserInfoForMyself test');
  }
  return {
    successfullyAnsweredQuestions: 2,
    canAnswerAQuestion: true,
  };
}

// export async function getQuizUnsucceededQuestionsNumberForMyself({ quizName }) {
//   if (!quizName) {
//     throw new Error('Missing quiz name.');
//   }
//   console.log('getQuizUnsucceededQuestionsNumberForMyself...');
//   await waitToSeconds();
//   return 3;
// }

export async function getRandomQuestionToAnswerForMyself({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getRandomQuestionToAnswerForMyself...');
  await waitToSeconds();
  if (raiseError.getRandomQuestionToAnswerForMyself) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur getRandomQuestionToAnswerForMyself test', 'details erreur');
    }
    throw new Error('Erreur getRandomQuestionToAnswerForMyself test');
  }
  return {
    id: 'aa',
    quizName,
    title: 'Intitulé question',
    author: 'Auteur question',
    publication: 'Publi question',
    answerPropositions: [
      { title: 'Proposition 0' },
      { title: 'Proposition 1' },
      { title: 'Proposition 2' },
      { title: 'Proposition 3' },
      { title: 'Proposition 4' },
    ],
  };
}

export async function answerQuestionForMyself(
  { quizName, questionId, propositionIndices },
  silentOnError,
) {
  if (!quizName || !questionId || !propositionIndices) {
    console.log(quizName, questionId, propositionIndices);
    throw new Error('Missing quiz name or question id or proposition indices.');
  }
  if ((propositionIndices.length ?? false) === false) {
    throw new Error('Proposition indices should be an array.');
  }
  console.log('answerQuestionForMyself...');
  await waitToSeconds();
  if (raiseError.answerQuestionForMyself) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur answerQuestionForMyself test', 'details erreur');
    }
    throw new Error('Erreur answerQuestionForMyself test');
  }
  return {
    successfullyAnsweredQuestions: 2,
    canAnswerAQuestion: propositionIndices.length >= 1,
    waitingMinutesBeforeNextAnswer: propositionIndices.length < 1 ? 2 : null,
    lastAnswerSuccess: propositionIndices.length > 2,
  };
}

export async function getQuizRankingForMyself({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuizRankingForMyself...');
  await waitToSeconds();
  if (raiseError.getQuizRankingForMyself) {
    if (silentOnError !== true && emHandler.errorManager) {
      emHandler.errorManager.addError('Erreur getQuizRankingForMyself test', 'details erreur');
    }
    throw new Error('Erreur getQuizRankingForMyself test');
  }
  return {
    rank: 2,
    publicRanking: [
      {
        firstname: 'Anne', lastname: 'Toto', numSuccessfulAnswers: 5, numAttempts: 5, lastAttempt: '2022-04-04T15:55:33',
      },
      {
        firstname: 'Jean', lastname: 'Bon', numSuccessfulAnswers: 4, numAttempts: 6, lastAttempt: '2022-04-05T15:55:33',
      },
      {
        firstname: 'Aline', lastname: 'Pilou', numSuccessfulAnswers: 4, numAttempts: 5, lastAttempt: '2022-04-04T15:55:33',
      },
    ],
  };
}

export default {
  login,
  logout,
  getMyself,
  createUser,
  patchMyself,
  deleteMyself,
  startAccountValidation,
  checkAccountValidation,
  validateAccount,
  startPasswordRenewal,
  checkPasswordRenewal,
  renewPassword,
  getQuizzesWithSimpleInfo,
  getQuiz,
  getQuizQuestionsNumber,
  getQuizUserInfoForMyself,
  getRandomQuestionToAnswerForMyself,
  answerQuestionForMyself,
  getQuizRankingForMyself,
};
