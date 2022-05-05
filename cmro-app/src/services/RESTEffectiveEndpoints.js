import { ROOT_AX, ROOT_URL } from '../RESTConfig';

// LOGIN/LOGOUT

export async function login({ mail, password, rememberMe }, silentOnError) {
  if (!mail || !password) {
    throw new Error('Missing mail or password.');
  }
  await ROOT_AX.post(`${ROOT_URL}/login`, {
    mail,
    password,
    rememberMe: rememberMe ?? false,
  }, {
    silentOnError,
  });
  return true;
}

export async function logout(silentOnError) {
  await ROOT_AX.get(`${ROOT_URL}/logout`, {
    silentOnError,
  });
  return true;
}

// USER
export async function getMyself(silentOnError) {
  const res = await ROOT_AX.get(`${ROOT_URL}/users/myself`, {
    silentOnError,
  });
  return res.data;
}

export async function createUser({
  mail, firstname, lastname, password,
}, silentOnError) {
  if (!mail || !firstname || !lastname || !password) {
    throw new Error('Missing mail, firstname, lastname or password.');
  }
  const res = await ROOT_AX.post(`${ROOT_URL}/users`, {
    mail, firstname, lastname, password,
  }, {
    silentOnError,
  });
  return res.data;
}

export async function patchMyself({ firstname, lastname, password }, silentOnError) {
  const res = await ROOT_AX.patch(`${ROOT_URL}/users/myself`, {
    firstname, lastname, password,
  }, {
    silentOnError,
  });
  return res.data;
}

export async function deleteMyself(silentOnError) {
  await ROOT_AX.delete(`${ROOT_URL}/users/myself`, {
    silentOnError,
  });
  return true;
}

// USER ACCOUNT
export async function startAccountValidation({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/validation`, {
    encodedMail,
  }, {
    silentOnError,
  });
  return true;
}

export async function checkAccountValidation({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.head(`${ROOT_URL}/users-accounts/validation`, {
    params: {
      m: encodedMail,
    },
    silentOnError,
  });
  return true;
}

export async function validateAccount({ encodedMail, token }, silentOnError) {
  if (!encodedMail || !token) {
    throw new Error('Missing encodeMail or token.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/validate`, {
    encodedMail,
    token,
  }, {
    silentOnError,
  });
  return true;
}

export async function startPasswordRenewal({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/password-renewal`, {
    encodedMail,
  }, {
    silentOnError,
  });
  return true;
}

export async function checkPasswordRenewal({ encodedMail }, silentOnError) {
  if (!encodedMail) {
    throw new Error('Missing encodeMail.');
  }
  await ROOT_AX.head(`${ROOT_URL}/users-accounts/password-renewal`, {
    params: {
      m: encodedMail,
      silentOnError,
    },
  });
  return true;
}

export async function renewPassword({ encodedMail, token, password }, silentOnError) {
  if (!encodedMail || !token || !password) {
    throw new Error('Missing encodeMail or token or password.');
  }
  await ROOT_AX.post(`${ROOT_URL}/users-accounts/password-renew`, {
    encodedMail,
    token,
    password,
  }, {
    silentOnError,
  });
  return true;
}

// QUIZ, QUESTIONS, ANSWER
export async function getQuizzesWithSimpleInfo(silentOnError) {
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes`, {
    silentOnError,
  });
  return res.data;
}

export async function getQuiz({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}`, {
    silentOnError,
  });
  return res.data;
}

export async function getQuizQuestionsNumber({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/questions-number`, {
    silentOnError,
  });
  return res.data;
}

export async function getQuizUserInfoForMyself({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/quiz-user-info/myself`, {
    silentOnError,
  });
  return res.data;
}

export async function getRandomQuestionToAnswerForMyself({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/random-question-to-answer/myself`, {
    silentOnError,
  });
  return res.data;
}

export async function answerQuestionForMyself(
  { quizName, questionId, propositionIndices },
  silentOnError,
) {
  if (!quizName || !questionId || !propositionIndices) {
    throw new Error('Missing quiz name or question id or proposition indices.');
  }
  if ((propositionIndices.length ?? false) === false) {
    throw new Error('Proposition indices should be an array.');
  }
  const res = await ROOT_AX.post(
    `${ROOT_URL}/quizzes/${quizName}/questions/${questionId}/answers/myself`,
    propositionIndices,
    {
      silentOnError,
    },
  );
  return res.data;
}

export async function getQuizRankingForMyself({ quizName }, silentOnError) {
  if (!quizName) {
    throw new Error('Missing quiz name.');
  }
  const res = await ROOT_AX.get(`${ROOT_URL}/quizzes/${quizName}/ranking/myself`, {
    silentOnError,
  });
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
