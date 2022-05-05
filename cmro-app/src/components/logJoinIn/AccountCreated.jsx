import React from 'react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Alert from 'react-bootstrap/Alert';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
// import RootStore from '../../RootStore';

function AccountCreated() {
  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        <Alert variant="success">
          <Alert.Heading>Compte créé</Alert.Heading>
          <p>
            Votre compte a bien été créé. Vous pouvez vous connecter dès maintenant.
          </p>
          <hr />
          <p className="mb-0">
            <LinkContainer to="/auth/login">
              <Button variant="link" className="ms-auto">Se connecter</Button>
            </LinkContainer>
          </p>
        </Alert>
      </Col>
    </Row>
  );
}

export default AccountCreated;
