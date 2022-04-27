import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useParams, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import GotoQuestionButton from './GotoQuestionButton';
import RootStore from '../../RootStore';

async function attempAutoLog(globalModelHdlr, navigate, quizName) {
  if (!globalModelHdlr.loggedUser.isAuthenticated) {
    try {
      console.log('Quiz: Attempt auto log');
      await globalModelHdlr.attemptAutoLogin();
    } catch (error) {
      console.log('Quiz: Auto log failed, go to login');
      navigate('/auth/login');
      return;
    }
  }
  if (!globalModelHdlr.currentQuiz || quizName !== globalModelHdlr.currentQuiz.name) {
    try {
      console.log('Quiz: Attempt to switch and retrieve quiz ' + quizName);
      await globalModelHdlr.switchQuiz(quizName);
    } catch (error) {
      console.log('Quiz: fail to switch and retrieve quiz: ' + error.message);
      navigate('/quizzes');
    }
  }
}

function Quiz() {
  const { quizName } = useParams();
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);

  useEffect(() => {
    attempAutoLog(globalModelHdlr, navigate, quizName);
  }, [globalModelHdlr, navigate, quizName]);

  return globalModelHdlr.currentQuiz && globalModelHdlr.currentQuiz.isReady ? (
    <>
      <Row>
        <Col>
          <h1>{globalModelHdlr.currentQuiz.fullName}</h1>
          <span>{globalModelHdlr.currentQuiz.description}</span>
        </Col>
      </Row>
      <Row>
        <Col>
          <GotoQuestionButton quiz={globalModelHdlr.currentQuiz} />
          <LinkContainer to={`/quizzes/${quizName}/analytics`}>
            <Button variant="success">Classement & ranking</Button>
          </LinkContainer>
        </Col>
      </Row>
    </>
  ) : (
    <h2><FontAwesomeIcon icon={faSpinner} pulse /></h2>
  );
}

export default observer(Quiz);
