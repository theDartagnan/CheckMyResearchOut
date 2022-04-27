import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useParams, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';
// import { LinkContainer } from 'react-router-bootstrap';
// import Button from 'react-bootstrap/Button';
// import GotoQuestionButton from './GotoQuestionButton';
import RootStore from '../../RootStore';

function Analytics() {
  const { quizName } = useParams();
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);

  useEffect(() => {
    if (!globalModelHdlr.loggedUser.isAuthenticated) {
      globalModelHdlr.attemptAutoLogin().catch(() => {
        navigate('/auth/login');
      });
    } else if (!globalModelHdlr.currentQuiz || quizName !== globalModelHdlr.currentQuiz.name) {
      globalModelHdlr.switchQuiz(quizName).catch(() => {
        navigate('/quizzes');
      });
    }
  }, [globalModelHdlr, navigate, quizName,
    globalModelHdlr.loggedUser.isAuthenticated, globalModelHdlr.currentQuiz]);

  return globalModelHdlr.currentQuiz && globalModelHdlr.currentQuiz.isReady ? (
    <>
      <Row>
        <Col>
          <h1>{globalModelHdlr.currentQuiz.fullName}</h1>
        </Col>
      </Row>
      <Row>
        <Col>
          <h3>Analytics</h3>
        </Col>
      </Row>
    </>
  ) : (
    <h2><FontAwesomeIcon icon={faSpinner} pulse /></h2>
  );
}

export default observer(Analytics);
