import React, { useContext } from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Alert from 'react-bootstrap/Alert';
import RootStore from '../RootStore';

function ErrorView() {
  const { errorManager } = useContext(RootStore);

  if (!errorManager.hasError) {
    return false;
  }

  return (
    <Row>
      <Col>
        <Alert
          variant="danger"
          onClose={() => errorManager.removeError()}
          dismissible
        >
          <Alert.Heading>{errorManager.error.title}</Alert.Heading>
          <p>{errorManager.error.content}</p>
        </Alert>
      </Col>
    </Row>
  );
}

export default observer(ErrorView);
