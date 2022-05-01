import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import {
  Routes,
  Route,
  useParams,
  useNavigate,
  Link,
} from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Nav from 'react-bootstrap/Nav';
import Loading from '../core/Loading';
import Ranking from './Ranking';
import PersonalStats from './PersonalStats';
import RootStore from '../../RootStore';

function Analytics() {
  const { quizName } = useParams();
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);

  useEffect(() => {
    if (!globalModelHdlr.loggedUser.isReady) {
      globalModelHdlr.attemptAutoLogin().catch(() => {
        navigate('/auth/login');
      });
    } else if (!globalModelHdlr.currentQuiz || quizName !== globalModelHdlr.currentQuiz.name) {
      globalModelHdlr.currentQuiz.fetch({ quizName }).catch(() => {
        navigate('/quizzes');
      });
    } else if (globalModelHdlr.currentQuiz.isReady) {
      globalModelHdlr.currentQuiz.retrieveRanking();
    }
  }, [globalModelHdlr, quizName, navigate,
    globalModelHdlr.loggedUser.isReady, globalModelHdlr.currentQuiz.isReady]);

  return globalModelHdlr.currentQuiz && globalModelHdlr.currentQuiz.isReady ? (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        <Row>
          <Col>
            <Nav defaultActiveKey="stats" className="justify-content-center">
              <Nav.Item>
                <Nav.Link as={Link} to="stats" eventKey="stats">Mes statistiques</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="ranking" eventKey="ranking">Classement</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        <Row>
          <Col>
            <Routes>
              <Route
                path="/stats"
                element={<PersonalStats ranking={globalModelHdlr.currentQuiz.ranking} />}
              />
              <Route
                path="/ranking"
                element={<Ranking ranking={globalModelHdlr.currentQuiz.ranking} />}
              />
            </Routes>
          </Col>
        </Row>
      </Col>
    </Row>
  ) : (
    <>
      <h1>Chargement du Quiz et des classements</h1>
      <Loading />
    </>
  );
}

export default observer(Analytics);
