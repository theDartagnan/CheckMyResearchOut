import effectiveEP from './RESTEffectiveEndpoints';
import testEP from './RESTTestEndpoints';

const TEST = true;

const usedPackage = TEST ? testEP : effectiveEP;

//export login = usedPackage.login;


export const login = usedPackage.login;
export const logout = usedPackage.logout;
export const getMyself = usedPackage.getMyself;
export const createUser = usedPackage.createUser;
export const patchMyself = usedPackage.patchMyself;
export const deleteMyself = usedPackage.deleteMyself;
export const startAccountValidation = usedPackage.startAccountValidation;
export const checkAccountValidation = usedPackage.checkAccountValidation;
export const validateAccount = usedPackage.validateAccount;
export const checkPasswordRenewal = usedPackage.checkPasswordRenewal;
export const renewPassword = usedPackage.renewPassword;
export const getQuizzesWithSimpleInfo = usedPackage.getQuizzesWithSimpleInfo;
export const getQuiz = usedPackage.getQuiz;
export const getQuizQuestionsNumber = usedPackage.getQuizQuestionsNumber
export const getQuizUserInfoForMyself = usedPackage.getQuizUserInfoForMyself;
export const getRandomQuestionToAnswerForMyself = usedPackage.getRandomQuestionToAnswerForMyself;
export const answerQuestionForMyself = usedPackage.answerQuestionForMyself;
export const getQuizRankingForMyself = usedPackage.getQuizRankingForMyself;
