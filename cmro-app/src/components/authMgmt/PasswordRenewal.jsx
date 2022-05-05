import React from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

function PasswordRenewal() {
  return (
    <Row className="justify-content-lg-center">
      <Col lg={10} xl={6}>
        <h3>Vous avez oublié votre mot de passe ?</h3>
        <p>
          Allez voir Rémi Venant !
        </p>
      </Col>
    </Row>
  );
}

export default observer(PasswordRenewal);
