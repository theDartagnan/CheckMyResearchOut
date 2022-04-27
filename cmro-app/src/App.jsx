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

import HomeAutoDiscovery from './components/HomeAutoDiscovery';
import LogJoinIn from './components/logJoinIn/LogJoinIn';
import QuizList from './components/quizzes/QuizList';
import Quiz from './components/quizzes/Quiz';
import AnswerQuestion from './components/quizzes/AnswerQuestion';
import Analytics from './components/analytics/Analytics';

import './App.scss';

function App() {
  return (
    <BrowserRouter basename={APP_ENV.APP_PUBLIC_PATH}>
      <RootStore.Provider value={STORE}>
        <main>
          <Container fluid>
            <Routes>
              <Route path="/" element={<HomeAutoDiscovery />} />
              <Route path="/auth/*" element={<LogJoinIn />} />
              <Route path="/quizzes" element={<QuizList />} />
              <Route path="/quizzes/:quizName" element={<Quiz />} />
              <Route path="/answer-question" element={<AnswerQuestion />} />
              <Route path="/quizzes/:quizName/analytics" element={<Analytics />} />
              {/* <Route path="/test/*" element={<MembersView />} /> */}
            </Routes>
          </Container>
        </main>
      </RootStore.Provider>
    </BrowserRouter>
  );
}

export default App;
