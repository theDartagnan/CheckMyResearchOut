import React, { useContext } from 'react';
import { observer } from 'mobx-react';
import {
  Routes,
  Route,
  useNavigate,
  Link,
} from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Nav from 'react-bootstrap/Nav';
import RootStore from '../../RootStore';
import LogIn from './LogIn';
import JoinIn from './JoinIn';
import AccountCreated from './AccountCreated';

function LogJoinIn() {
  const { globalModelHdlr } = useContext(RootStore);
  const navigate = useNavigate();

  const login = async ({ mail, password, rememberMe }) => {
    await globalModelHdlr.loggedUser.login({ mail, password, rememberMe });
    navigate('/quizzes');
  };

  const createAccount = async ({
    mail, password, firstname, lastname,
  }) => {
    await globalModelHdlr.createAccount({
      mail, password, firstname, lastname,
    });
    navigate('account-created');
  };

  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        <Row className="border-bottom border-primary mb-4">
          <Col>
            <Nav defaultActiveKey="login" className="justify-content-center">
              <Nav.Item>
                <Nav.Link as={Link} to="login" eventKey="login">Se connecter</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="create-account" eventKey="create-account">Créer un compte</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        <Row>
          <Col>
            <Routes>
              <Route path="/login" element={<LogIn attemptLogin={login} />} />
              <Route path="/create-account" element={<JoinIn attemptCreate={createAccount} />} />
              <Route path="/account-created" element={<AccountCreated />} />
            </Routes>
          </Col>
        </Row>
      </Col>
    </Row>
  );
}

export default observer(LogJoinIn);
