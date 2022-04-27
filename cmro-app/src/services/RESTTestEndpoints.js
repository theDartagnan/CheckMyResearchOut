/* eslint-disable no-console, no-unused-vars */
function waitToSeconds(seconds = 1) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
    }, seconds * 1000);
  });
}

// LOGIN/LOGOUT

export async function login({ mail, password, rememberMe }) {
  if (!mail || !password) {
    throw new Error('Missing mail or password.');
  }
  console.log('login...');
  await waitToSeconds();
  return true;
}

export async function logout() {
  console.log('logout...');
  await waitToSeconds();
  return true;
}

// USER
export async function getMyself() {
  console.log('getMyself...');
  await waitToSeconds();
  return {
    mail: 'jean.bon@mail.com',
    firstname: 'jean',
    lastname: 'bon',
    admin: false,
  };
}

export async function createUser({
  mail, firstname, lastname, password,
}) {
  if (!mail || !firstname || !lastname || !password) {
    throw new Error('Missing mail, firstname, lastname or password.');
  }
  console.log('create user...');
  await waitToSeconds();
  return {
    mail, firstname, lastname,
  };
}

export async function patchMyself({ firstname, lastname, password }) {
  console.log('logout...');
  await waitToSeconds();
  return {
    mail: 'jean.bon@mail.com', firstname, lastname,
  };
}

export async function deleteMyself() {
  console.log('logout...');
  await waitToSeconds();
  return true;
}

// USER ACCOUNT
export async function startAccountValidation({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await waitToSeconds();
  return true;
}

export async function checkAccountValidation({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  console.log('logout...');
  await waitToSeconds();
  return true;
}

export async function validateAccount({ encodedMail, token }) {
  if (!encodedMail || !token) {
    throw new Error('Missing encodeMail or token.');
  }
  console.log('logout...');
  await waitToSeconds();
  return true;
}

export async function startPasswordRenewal({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  console.log('logout...');
  await waitToSeconds();
  return true;
}

export async function checkPasswordRenewal({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  console.log('checkPasswordRenewal...');
  await waitToSeconds();
  return true;
}

export async function renewPassword({ encodedMail, token, password }) {
  if (!encodedMail || !token || !password) {
    throw new Error('Missing encodeMail or token or password.');
  }
  console.log('renewPassword...');
  await waitToSeconds();
  return true;
}

// QUIZ, QUESTIONS, ANSWER
export async function getQuizzesWithSimpleInfo() {
  console.log('getQuizzesWithSimpleInfo...');
  await waitToSeconds();
  return [
    { id: 'aa', name: 'Quiz1', fullName: 'Quiz 1' },
    { id: 'bb', name: 'Quiz2', fullName: 'Quiz 2' },
    { id: 'cc', name: 'Quiz3', fullName: 'Quiz 3' },
  ];
}

export async function getQuiz({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuiz...');
  await waitToSeconds();
  return {
    id: 'aa',
    name: quizName,
    fullName: `Name ${quizName}`,
    description: `Description ${quizName}`,
  };
}

export async function getQuizQuestionsNumber({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuizQuestionsNumber...');
  await waitToSeconds();
  return 10;
}

export async function getQuizUserInfoForMyself({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuizUnsucceededQuestionsNumberForMyself...');
  await waitToSeconds();
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

export async function getRandomQuestionToAnswerForMyself({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getRandomQuestionToAnswerForMyself...');
  await waitToSeconds();
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

export async function answerQuestionForMyself({ quizName, questionId, propositionIndices }) {
  if (!quizName || !questionId || !propositionIndices) {
    console.log(quizName, questionId, propositionIndices);
    throw new Error('Missing quiz name or question id or proposition indices.');
  }
  if ((propositionIndices.length ?? false) === false) {
    throw new Error('Proposition indices should be an array.');
  }
  console.log('answerQuestionForMyself...');
  await waitToSeconds();
  return {
    successfullyAnsweredQuestions: 2,
    canAnswerAQuestion: true,
    lastAnswerSuccess: propositionIndices.length > 2,
  };
}

export async function getQuizRankingForMyself({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  console.log('getQuizRankingForMyself...');
  await waitToSeconds();
  return {
    rank: 2,
    publicRanking: [
      {
        firstname: 'Anne', lastname: 'Toto', numSuccessfulAnswers: 5, numAttempts: 5, lastAttempt: '2022-04-04T15:55:33',
      },
      {
        firstname: 'Jean', lastname: 'Bon', numSuccessfulAnswers: 5, numAttempts: 6, lastAttempt: '2022-04-05T15:55:33',
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
