import React from 'react';
// import PropTypes from 'prop-types';
// import classNames from 'classnames';
import Container from 'react-bootstrap/Container';
import {
  BrowserRouter,
  Routes,
  Route,
} from 'react-router-dom';
import RootStore from './RootStore';
import STORE from './store';

import { configureNetworkErrorHandler } from './RESTConfig';

import ErrorBoundary from './components/ErrorBoundary';
import ErrorView from './components/ErrorView';
import AppNavbar from './components/AppNavbar';
import HomeAutoDiscovery from './components/HomeAutoDiscovery';
import LogJoinIn from './components/logJoinIn/LogJoinIn';
import UserProfile from './components/user/UserProfile';
import QuizList from './components/quizzes/QuizList';
import Quiz from './components/quizzes/Quiz';
import AnswerQuestion from './components/quizzes/AnswerQuestion';
import Analytics from './components/analytics/Analytics';
import AccountValidationResendMail from './components/authMgmt/AccountValidationResendMail';
import AccountValidation from './components/authMgmt/AccountValidation';
import PasswordRenewal from './components/authMgmt/PasswordRenewal';
import PasswordRenew from './components/authMgmt/PasswordRenew';

import { setErrorManager } from './services/RESTTestEndpoints';

import './App.scss';

setErrorManager(STORE.errorManager);

configureNetworkErrorHandler((title, details) => STORE.errorManager.addError(title, details));

function App() {
  return (
    <BrowserRouter basename={APP_ENV.APP_PUBLIC_PATH}>
      <RootStore.Provider value={STORE}>
        <ErrorBoundary>
          <AppNavbar />
          <ErrorView />
          <main>
            <Container>
              <Routes>
                <Route path="/" element={<HomeAutoDiscovery />} />
                <Route path="/auth/*" element={<LogJoinIn />} />
                <Route path="/user/profile" element={<UserProfile />} />
                <Route path="/quizzes" element={<QuizList />} />
                <Route path="/quizzes/:quizName" element={<Quiz />} />
                <Route path="/answer-question" element={<AnswerQuestion />} />
                <Route path="/quizzes/:quizName/analytics/*" element={<Analytics />} />
                <Route path="/auth-mgmt/revalidate" element={<AccountValidationResendMail />} />
                <Route path="/auth-mgmt/validate" element={<AccountValidation />} />
                <Route path="/auth-mgmt/password-renewal" element={<PasswordRenewal />} />
                <Route path="/auth-mgmt/renew-password" element={<PasswordRenew />} />
                {/* <Route path="/test/*" element={<MembersView />} /> */}
              </Routes>
            </Container>
          </main>
        </ErrorBoundary>
      </RootStore.Provider>
    </BrowserRouter>
  );
}

export default App;
