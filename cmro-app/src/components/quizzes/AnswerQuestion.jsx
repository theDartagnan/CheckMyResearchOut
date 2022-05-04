import React, { useEffect, useReducer, useContext } from 'react';
import classNames from 'classnames';
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
import Loading from '../core/Loading';

import style from './AnswerQuestion.scss';

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
                    className={classNames('mb-3', style.questionCheckbox)}
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
              <Alert className="mt-4 mb-3" variant="warning">
                <Alert.Heading>Où trouver la réponse ?</Alert.Heading>
                <hr />
                <p className="mb-0">
                  Va découvrir le poster de &nbsp;
                  <strong>{question.author}</strong>
                  &nbsp;:&nbsp;
                  {question.publication}
                  &nbsp;!
                </p>
              </Alert>
              <LoadingButton className="mb-3" variant="primary" type="submit" loading={onAnswering}>
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
    globalModelHdlr.attemptAutoLogin(true).then(() => {
      // Attempt autologin with quiz: redirect if user not logged or quiz not retrieved
      if (!globalModelHdlr.loggedUser.isReady) {
        navigate('/auth/login');
        throw new Error('Failed to autolog');
      }
      if (!globalModelHdlr.currentQuiz.isReady) {
        navigate('/quizzes');
        throw new Error('Failed to autolog');
      }
    }, (error) => {
      console.warn(`AnswerQuestion: attempt login failed: ${error.message}`);
      throw error;
    }).then(() => {
      // Autologin succeeded, attempt to renew the question if necessairy
      if (!state.questionAnswered) {
        try {
          return globalModelHdlr.currentQuiz.renewQuestion().then((question) => {
            if (!question) {
              navigate('/');
            }
          });
        } catch (error) {
          console.warn(`AnswerQuestion: cannot auto retrieve the question: ${error.message}`);
        }
      }
      return globalModelHdlr.currentQuiz;
    });
  }, [globalModelHdlr, navigate, state.questionAnswered]);

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

  let inner = null;

  if (!globalModelHdlr.currentQuiz || !globalModelHdlr.currentQuiz.isReady
    || (!globalModelHdlr.currentQuiz.currentQuestion && !state.questionAnswered)) {
    inner = (
      <>
        <h3>Chargement de la question</h3>
        <Loading />
      </>
    );
  } else if (state.questionAnswered) {
    // If the question was answered and we did not caught any error :
    // we just show the result message
    // and the possible actions
    inner = (
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
        <Row className="justify-content-between mt-3">
          <Col>
            <GotoQuestionButton
              quiz={globalModelHdlr.currentQuiz}
              gotoQuestion={gotoNextQuestion}
              otherQuestion
              className="h-100"
            />
          </Col>
          <Col>
            <LinkContainer to={`/quizzes/${globalModelHdlr.currentQuiz.name}/analytics/stats`}>
              <Button variant="primary" className="h-100">Classement & ranking</Button>
            </LinkContainer>
          </Col>
          <Col>
            <LinkContainer to={`/quizzes/${globalModelHdlr.currentQuiz.name}`}>
              <Button variant="primary" className="h-100">Retour au questionnaire</Button>
            </LinkContainer>
          </Col>
        </Row>
      </>
    );
  } else {
    // Otherwise (the question was not answered): we show the form and possibily the error
    inner = (
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

  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        { inner }
      </Col>
    </Row>
  );
}

export default observer(AnswerQuestion);
