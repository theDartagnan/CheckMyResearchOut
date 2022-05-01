import React from 'react';
import { observer } from 'mobx-react';
import PropTypes from 'prop-types';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBan } from '@fortawesome/free-solid-svg-icons';

function GotoQuestionButton({
  quiz, gotoQuestion, otherQuestion, className,
}) {
  if (quiz.quizUserInfo.canAnswerAQuestion) {
    const btnTxt = otherQuestion ? 'Répondre à une autre question' : 'Répondre à une question';
    return gotoQuestion ? (
      <Button variant="primary" onClick={gotoQuestion} className={className}>{btnTxt}</Button>
    ) : (
      <LinkContainer to="/answer-question">
        <Button variant="primary" className={className}>{btnTxt}</Button>
      </LinkContainer>
    );
  } if (quiz.quizUserInfo.successfullyAnsweredQuestions < quiz.questionsNumber) {
    return (
      <Button variant="danger" disabled className={className}>
        <FontAwesomeIcon icon={faBan} className="me-2" />
        Vous ne pouvez pas répondre tout de suite à de nouvelle question,&nbsp;
        merci de revenir dans quelques minutes
      </Button>
    );
  }
  return (
    <Button variant="danger" disabled className={className}>
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
  otherQuestion: PropTypes.bool,
  className: PropTypes.string,
};

GotoQuestionButton.defaultProps = {
  gotoQuestion: null,
  otherQuestion: false,
  className: null,
};

export default observer(GotoQuestionButton);
