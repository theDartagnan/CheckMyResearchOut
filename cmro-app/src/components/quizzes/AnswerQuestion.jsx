import React, { useEffect, useReducer, useContext } from 'react';
import { observer } from 'mobx-react';
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import Alert from 'react-bootstrap/Alert';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import LoadingButton from '../core/LoadingButton';
import GotoQuestionButton from './GotoQuestionButton';
import RootStore from '../../RootStore';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

async function attempAutoLog(globalModelHdlr, navigate) {
  if (!globalModelHdlr.loggedUser.isAuthenticated) {
    try {
      console.log('AnswerQuestion: Attempt auto log');
      await globalModelHdlr.attemptAutoLogin();
    } catch (error) {
      console.log('AnswerQuestion: Auto log failed, go to login');
      navigate('/auth/login');
      return;
    }
  }
  if (!globalModelHdlr.currentQuiz) {
    try {
      console.log('AnswerQuestion: Attempt auto quiz retrieval');
      await globalModelHdlr.attemptFindPreviousQuiz();
    } catch (error) {
      console.log('AnswerQuestion: Auto quiz retrieval, go to quizzes: ' + error.message);
      navigate('/quizzes');
      return;
    }
  }
}

function _QuizQuestionForm({ question, onSubmit, onAnswering }) {
  const submit = (e) => {
    e.preventDefault();
    onSubmit(question);
  };
  return (
    <>
      <Row>
        <Col>
          <h3>{question.title}</h3>
        </Col>
      </Row>
      <Row>
        <Col>
          <Form onSubmit={submit}>
            <fieldset disabled={onAnswering}>
              {
                question.propositions.map((proposition) => (
                  <Form.Group
                    className="mb-1"
                    key={proposition.index}
                    controlId={`formQuestionProposition_${proposition.index}`}
                  >
                    <Form.Check
                      type="checkbox"
                      label={proposition.title}
                      checked={question.hasProposition(proposition)}
                      onChange={() => question.switchProposition(proposition)}
                    />
                  </Form.Group>
                ))
              }
              <LoadingButton variant="primary" type="submit" loading={onAnswering}>
                Répondre au questionnaire
              </LoadingButton>
            </fieldset>
          </Form>
        </Col>
      </Row>
    </>
  );
}

_QuizQuestionForm.propTypes = {
  question: PropTypes.shape({
    title: PropTypes.string.isRequired,
    author: PropTypes.string.isRequired,
    publication: PropTypes.string.isRequired,
    propositions: PropTypes.arrayOf(PropTypes.shape({
      title: PropTypes.string.isRequired,
      index: PropTypes.number.isRequired,
    })).isRequired,
    hasProposition: PropTypes.func.isRequired,
    switchProposition: PropTypes.func.isRequired,
  }).isRequired,
  onSubmit: PropTypes.func.isRequired,
  onAnswering: PropTypes.bool,
};

_QuizQuestionForm.defaultProps = {
  onAnswering: false,
};

const QuizQuestionForm = observer(_QuizQuestionForm);

function createState() {
  return {
    onAnswering: false,
    questionAnswered: false,
    error: null,
  };
}

function reduce(state, action) {
  switch (action?.type) {
    case 'start-answering':
      return {
        ...state, onAnswering: true, error: null, questionAnswered: false,
      };
    case 'stop-answering':
      return {
        ...state,
        onAnswering: false,
        error: action.error ?? null,
        questionAnswered: !action.error,
      };
    case 'reset':
      return createState();
    default:
      throw new Error(`Unmanaged reducer action ${action.type}`);
  }
}

function AnswerQuestion() {
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);
  const [state, dispatch] = useReducer(reduce, null, createState);

  useEffect(() => {
    attempAutoLog(globalModelHdlr, navigate);
  }, [globalModelHdlr, navigate]);

  useEffect(() => {
    // If the question is not answered but we do not have any question : logical error
    // if (!state.questionAnswered && !globalModelHdlr.currentQuiz?.currentQuestion) {
    //   console.warn('Cannot show unsanswered quizAnswer component without any current question. Back to home.');
    //   navigate('/');
    //   return false;
    // }
    // If we do not have answered the question yet
    if (globalModelHdlr.currentQuiz && globalModelHdlr.currentQuiz.isReady
      && !state.onAnswering && !state.questionAnswered && globalModelHdlr.currentQuiz
      && !globalModelHdlr.currentQuiz.currentQuestion) {
        console.log('AnswerQuestion: renewQuestion');
        globalModelHdlr.currentQuiz.renewQuestion();
    }
  }, [globalModelHdlr.currentQuiz, globalModelHdlr.currentQuiz?.isReady,
    globalModelHdlr.currentQuiz?.currentQuestion, state.onAnswering, state.questionAnswered]);

  const answerQuestion = async () => {
    if (state.onAnswering) {
      console.warn('State already in answering state');
      return;
    }
    dispatch({ type: 'start-answering' });
    try {
      await globalModelHdlr.currentQuiz.answerQuestion();
      dispatch({ type: 'stop-answering' });
    } catch (error) {
      dispatch({ type: 'stop-answering', error });
    }
  };

  const gotoNextQuestion = () => dispatch({ type: 'reset' });

  if (!globalModelHdlr.currentQuiz || !globalModelHdlr.currentQuiz.isReady
    || (!globalModelHdlr.currentQuiz.currentQuestion && !state.questionAnswered)) {
    return (
      <h2><FontAwesomeIcon icon={faSpinner} pulse /></h2>
    );
  }

  // If the question was answered and we did not caught any error : we just show the result message
  // and the possible actions
  if (state.questionAnswered) {
    return (
      <>
        <Row>
          <Col>
            {
              globalModelHdlr.currentQuiz.quizUserInfo.lastAnswerSuccess ? (
                <Alert className="mt-3" variant="success">Tu as bien répondu, bravo !</Alert>
              ) : (
                <Alert className="mt-3" variant="warning">Raté ! Tu pourras re-essayer de répondre à cette question un peu plus tard...</Alert>
              )
            }
          </Col>
        </Row>
        <Row>
          <Col>
            <GotoQuestionButton
              quiz={globalModelHdlr.currentQuiz}
              gotoQuestion={gotoNextQuestion}
            />
            <LinkContainer to={`/quizzes/${globalModelHdlr.currentQuiz.name}/analytics`}>
              <Button variant="success">Classement & ranking</Button>
            </LinkContainer>
            <LinkContainer to={`/quizzes/${globalModelHdlr.currentQuiz.name}`}>
              <Button variant="success">Retour à la présentation du questionnaire</Button>
            </LinkContainer>
          </Col>
        </Row>
      </>
    );
  }
  // Otherwise (the question was not answered): we show the form and possibily the error
  return (
    <>
      <Row>
        <Col>
          <QuizQuestionForm
            question={globalModelHdlr.currentQuiz.currentQuestion}
            onSubmit={answerQuestion}
            onAnswering={state.onAnswering}
          />
        </Col>
      </Row>
      {
        state.error && (
          <Row>
            <Col><Alert className="mt-3" variant="danger">{state.error.message}</Alert></Col>
          </Row>
        )
      }
    </>
  );
}

export default observer(AnswerQuestion);
