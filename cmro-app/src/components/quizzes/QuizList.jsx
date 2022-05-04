import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react';
import { useNavigate } from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import ListGroup from 'react-bootstrap/ListGroup';
import { LinkContainer } from 'react-router-bootstrap';
import Loading from '../core/Loading';
import RootStore from '../../RootStore';

import QuizCollection from '../../model/QuizCollection';

function QuizList() {
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);
  const [quizCollec] = useState(new QuizCollection());

  useEffect(() => {
    globalModelHdlr.attemptAutoLogin(false).then(() => {
      if (!globalModelHdlr.loggedUser.isReady) {
        navigate('/auth/login');
        return;
      }
      quizCollec.fetch();
    }, (error) => {
      console.warn(`QuizList: attempt login failed: ${error.message}`);
    });
  }, [globalModelHdlr, navigate, quizCollec]);

  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        <h1>Quizzes disponibles</h1>
        {
          quizCollec.isReady ? (
            <ListGroup>
              {
                quizCollec.quizzes.map((quiz) => (
                  <LinkContainer key={quiz.name} to={`/quizzes/${quiz.name}`}>
                    <ListGroup.Item action>{quiz.fullName}</ListGroup.Item>
                  </LinkContainer>
                ))
              }
            </ListGroup>
          ) : (
            <Loading />
          )
        }
      </Col>
    </Row>
  );
}

export default observer(QuizList);
