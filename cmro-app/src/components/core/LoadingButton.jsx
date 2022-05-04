/* eslint-disable react/jsx-props-no-spreading */
import React from 'react';
import PropTypes from 'prop-types';
import Button from 'react-bootstrap/Button';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

function LoadingButton({ loading, children, ...props }) {
  return (
    <Button {...props}>
      { loading && (
        <FontAwesomeIcon icon={faSpinner} pulse className="me-2" />
      )}
      {
        children
      }
    </Button>
  );
}

LoadingButton.propTypes = {
  loading: PropTypes.bool,
  children: PropTypes.node,
};

LoadingButton.defaultProps = {
  loading: false,
  children: null,
};

export default LoadingButton;
