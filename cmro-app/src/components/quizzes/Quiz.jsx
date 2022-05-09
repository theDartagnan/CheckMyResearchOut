import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useParams, useNavigate } from 'react-router-dom';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import Loading from '../core/Loading';
import GotoQuestionButton from './GotoQuestionButton';
import RootStore from '../../RootStore';

function Quiz() {
  const { quizName } = useParams();
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);

  useEffect(() => {
    if (!globalModelHdlr.loggedUser.isReady) {
      globalModelHdlr.attemptAutoLogin().catch(() => {
        navigate('/auth/login');
      });
    } else { // force reload
      globalModelHdlr.currentQuiz.fetch({ quizName }).catch(() => {
        navigate('/quizzes');
      });
    }
  }, [globalModelHdlr, globalModelHdlr.loggedUser.isReady, navigate, quizName]);

  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        {
          globalModelHdlr.currentQuiz.isReady ? (
            <>
              <Row>
                <Col>
                  <h1>{globalModelHdlr.currentQuiz.fullName}</h1>
                  <p className="my-2 p-1 bg-secondary text-light">{globalModelHdlr.currentQuiz.description}</p>
                </Col>
              </Row>
              <Row className="justify-content-between mt-3">
                <Col>
                  <GotoQuestionButton quiz={globalModelHdlr.currentQuiz} />
                </Col>
                <Col className="text-end">
                  <LinkContainer to={`/quizzes/${quizName}/analytics/stats`}>
                    <Button variant="success" className="ms-auto">Classement & ranking</Button>
                  </LinkContainer>
                </Col>
              </Row>
            </>
          ) : (
            <>
              <h1>Chargement du Quiz</h1>
              <Loading />
            </>
          )
        }
      </Col>
    </Row>
  );
}

export default observer(Quiz);
