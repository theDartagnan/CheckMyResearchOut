import React from 'react';
import { observer } from 'mobx-react';
import PropTypes from 'prop-types';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBan } from '@fortawesome/free-solid-svg-icons';

function GotoQuestionButton({ quiz, gotoQuestion }) {
  if (quiz.quizUserInfo.canAnswerAQuestion) {
    return gotoQuestion ? (
      <Button variant="primary" onClick={gotoQuestion}>Répondre à une question</Button>
    ) : (
      <LinkContainer to="/answer-question">
        <Button variant="primary">Répondre à une question</Button>
      </LinkContainer>
    );
  } if (quiz.quizUserInfo.successfullyAnsweredQuestions < quiz.questionsNumber) {
    return (
      <Button variant="danger" disabled>
        <FontAwesomeIcon icon={faBan} className="me-2" />
        Vous ne pouvez pas répondre tout de suite à de nouvelle question,&nbsp;
        merci de revenir dans quelques minutes
      </Button>
    );
  }
  return (
    <Button variant="danger" disabled>
      <FontAwesomeIcon icon={faBan} className="me-2" />
      Vous ne pouvez pas répondre tout de suite à de nouvelle question,&nbsp;
      merci de revenir dans quelques minutes
    </Button>
  );
}

GotoQuestionButton.propTypes = {
  quiz: PropTypes.shape({
    name: PropTypes.string.isRequired,
    questionsNumber: PropTypes.number.isRequired,
    quizUserInfo: PropTypes.shape({
      canAnswerAQuestion: PropTypes.bool.isRequired,
      successfullyAnsweredQuestions: PropTypes.number.isRequired,
    }).isRequired,
  }).isRequired,
  gotoQuestion: PropTypes.func,
};

GotoQuestionButton.defaultProps = {
  gotoQuestion: null,
};

export default observer(GotoQuestionButton);
