import React from 'react';
// import classNames from 'classnames';
// import PropTypes from 'prop-types';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

function Loading() {
  return (
    <Row className="justify-content-center">
      <Col xs={1}>
        <h2><FontAwesomeIcon icon={faSpinner} pulse className="text-primary" /></h2>
      </Col>
    </Row>
  );
}

export default Loading;
