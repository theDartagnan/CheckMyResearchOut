/* eslint-disable no-console */
import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';
import RootStore from '../RootStore';

async function attempAutoLog(globalModelHdlr, navigate) {
  if (!globalModelHdlr.loggedUser.isAuthenticated) {
    try {
      console.log('HomeAutoDiscovery: Attempt auto log');
      await globalModelHdlr.attemptAutoLogin();
    } catch (error) {
      console.log('HomeAutoDiscovery: Auto log failed, go to login');
      navigate('/auth/login');
      return;
    }
  }
  if (!globalModelHdlr.currentQuiz) {
    try {
      console.log('HomeAutoDiscovery: Attempt auto quiz retrieval');
      await globalModelHdlr.attemptFindPreviousQuiz();
    } catch (error) {
      console.log('HomeAutoDiscovery: Auto quiz retrieval, go to quizzes: ' + error.message);
      navigate('/quizzes');
      return;
    }
  }
  console.log('go to current quiz');
  navigate(`/quizzes/${globalModelHdlr.currentQuiz.name}`);
}

function HomeAutoDiscovery() {
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);

  useEffect(() => {
    attempAutoLog(globalModelHdlr, navigate);
  }, []);

  return (
    <Row>
      <Col>
        <h1>Chargement de l&lsquo;application</h1>
        <h2><FontAwesomeIcon icon={faSpinner} pulse /></h2>
      </Col>
    </Row>
  );
}

export default observer(HomeAutoDiscovery);
