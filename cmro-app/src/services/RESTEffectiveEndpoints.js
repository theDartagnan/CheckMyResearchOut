import { ROOT_AX, ROOT_URL } from '../RESTConfig';

// LOGIN/LOGOUT

export async function login({ mail, password, rememberMe }) {
  if (!mail || !password) {
    throw new Error('Missing mail or password.');
  }
  await ROOT_AX.post(`${ROOT_URL}/login`, {
    mail,
    password,
    rememberMe: rememberMe ?? false,
  });
  return true;
}

export async function logout() {
  await ROOT_AX.get(`${ROOT_URL}/logout`);
  return true;
}

// USER
export async function getMyself() {
  const res = await ROOT_AX.get(`${ROOT_URL}/users/myself`);
  return res.data;
}

export async function createUser({
  mail, firstname, lastname, password,
}) {
  if (!mail || !firstname || !lastname || !password) {
    throw new Error('Missing mail, firstname, lastname or password.');
  }
  const res = await ROOT_AX.post(`${ROOT_URL}/users/myself`, {
    mail, firstname, lastname, password,
  });
  return res.data;
}

export async function patchMyself({ firstname, lastname, password }) {
  const res = await ROOT_AX.patch(`${ROOT_URL}/users/myself`, {
    firstname, lastname, password,
  });
  return res.data;
}

export async function deleteMyself() {
  await ROOT_AX.delete(`${ROOT_URL}/users/myself`);
  return true;
}

// USER ACCOUNT
export async function startAccountValidation({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/validation`, {
    encodedMail,
  });
  return true;
}

export async function checkAccountValidation({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.head(`${ROOT_URL}/users-accounts/validation`, {
    params: {
      m: encodedMail,
    },
  });
  return true;
}

export async function validateAccount({ encodedMail, token }) {
  if (!encodedMail || !token) {
    throw new Error('Missing encodeMail or token.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/validate`, {
    encodedMail,
    token,
  });
  return true;
}

export async function startPasswordRenewal({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/password-renewal`, {
    encodedMail,
  });
  return true;
}

export async function checkPasswordRenewal({ encodedMail }) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.head(`${ROOT_URL}/users-accounts/password-renewal`, {
    params: {
      m: encodedMail,
    },
  });
  return true;
}

export async function renewPassword({ encodedMail, token, password }) {
  if (!encodedMail || !token || !password) {
    throw new Error('Missing encodeMail or token or password.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/password-renew`, {
    encodedMail,
    token,
    password,
  });
  return true;
}

// QUIZ, QUESTIONS, ANSWER
export async function getQuizzesWithSimpleInfo() {
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes`);
  return res.data;
}

export async function getQuiz({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}`);
  return res.data;
}

export async function getQuizQuestionsNumber({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/questions-number`);
  return res.data;
}

export async function getQuizUserInfoForMyself({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/quiz-user-info/myself`);
  return res.data;
}

export async function getRandomQuestionToAnswerForMyself({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/random-question-to-answer/myself`);
  return res.data;
}

export async function answerQuestionForMyself({ quizName, questionId, propositionIndices }) {
  if (!quizName || !questionId || !propositionIndices) {
    throw new Error('Missing quiz name or question id or proposition indices.');
  }
  if ((propositionIndices.length ?? false) === false) {
    throw new Error('Proposition indices should be an array.');
  }
  const res = await ROOT_AX.post(`${ROOT_URL}/quizzes/${quizName}/questions/${questionId}/answers/myself`, propositionIndices);
  return res.data;
}

export async function getQuizRankingForMyself({ quizName }) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/ranking/myself`);
  return res.data;
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
