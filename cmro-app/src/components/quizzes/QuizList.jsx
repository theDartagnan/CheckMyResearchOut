/* eslint-disable no-console */
import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react';
import { useNavigate } from 'react-router-dom';
import ListGroup from 'react-bootstrap/ListGroup';
import { LinkContainer } from 'react-router-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';
import RootStore from '../../RootStore';

import QuizCollection from '../../model/QuizCollection';

async function attempAutoLog(globalModelHdlr, navigate, quizCollec) {
  if (!globalModelHdlr.loggedUser.isAuthenticated) {
    try {
      console.log('QuizList: Attempt auto log');
      await globalModelHdlr.attemptAutoLogin();
    } catch (error) {
      console.log('QuizList: Auto log failed, go to login');
      navigate('/auth/login');
      return;
    }
  }
  if (quizCollec.isInit) {
    try {
      console.log('QuizList: Attempt quizzes retrieval');
      quizCollec.fetch();
    } catch (error) {
      console.log('QuizList: Auto quiz retrieval, go to quizzes');
      return;
    }
  }
}

function QuizList() {
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);
  const [quizCollec] = useState(new QuizCollection());

  useEffect(() => {
    attempAutoLog(globalModelHdlr, navigate, quizCollec)
  }, []);

  return quizCollec.isReady ? (
    <>
      <h1>Quizzes disponibles </h1>
      <ListGroup>
        {
          quizCollec.quizzes.map((quiz) => (
            <LinkContainer key={quiz.name} to={`/quizzes/${quiz.name}`}>
              <ListGroup.Item>{quiz.fullName}</ListGroup.Item>
            </LinkContainer>
          ))
        }
      </ListGroup>
    </>
  ) : (
    <h2><FontAwesomeIcon icon={faSpinner} pulse /></h2>
  );
}

export default observer(QuizList);
