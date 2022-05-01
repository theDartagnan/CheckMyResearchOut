import effectiveEP from './RESTEffectiveEndpoints';
import testEP from './RESTTestEndpoints';

const TEST = false;

const usedPackage = TEST ? testEP : effectiveEP;

// export login = usedPackage.login;

export const { login } = usedPackage;
export const { logout } = usedPackage;
export const { getMyself } = usedPackage;
export const { createUser } = usedPackage;
export const { patchMyself } = usedPackage;
export const { deleteMyself } = usedPackage;
export const { startAccountValidation } = usedPackage;
export const { checkAccountValidation } = usedPackage;
export const { validateAccount } = usedPackage;
export const { startPasswordRenewal } = usedPackage;
export const { checkPasswordRenewal } = usedPackage;
export const { renewPassword } = usedPackage;
export const { getQuizzesWithSimpleInfo } = usedPackage;
export const { getQuiz } = usedPackage;
export const { getQuizQuestionsNumber } = usedPackage;
export const { getQuizUserInfoForMyself } = usedPackage;
export const { getRandomQuestionToAnswerForMyself } = usedPackage;
export const { answerQuestionForMyself } = usedPackage;
export const { getQuizRankingForMyself } = usedPackage;
