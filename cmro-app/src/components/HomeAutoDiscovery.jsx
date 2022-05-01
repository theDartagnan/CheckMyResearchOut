/* eslint-disable no-console */
import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useNavigate } from 'react-router-dom';
import Loading from './core/Loading';
import RootStore from '../RootStore';

function HomeAutoDiscovery() {
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);

  useEffect(() => {
    globalModelHdlr.attemptAutoLogin(true).then(() => {
      if (!globalModelHdlr.loggedUser.isReady) {
        navigate('/auth/login');
        return;
      }
      if (!globalModelHdlr.currentQuiz.isReady) {
        navigate('/quizzes');
        return;
      }
      navigate(`/quizzes/${globalModelHdlr.currentQuiz.name}`);
    }, (error) => {
      console.warn(`HomeAutoDiscovery: attempt login failed: ${error.message}`);
    });
  }, [globalModelHdlr, navigate]);

  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        <h1>Chargement de l&lsquo;application</h1>
        <Loading />
      </Col>
    </Row>
  );
}

export default observer(HomeAutoDiscovery);
