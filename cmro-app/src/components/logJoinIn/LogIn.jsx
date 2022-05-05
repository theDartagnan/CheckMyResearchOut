import React, { useReducer } from 'react';
// { observer } from 'mobx-react';
import PropTypes from 'prop-types';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import Alert from 'react-bootstrap/Alert';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import LoadingButton from '../core/LoadingButton';

function createState() {
  return {
    mail: '',
    password: '',
    rememberMe: true,
    onLoggin: false,
    error: null,
  };
}

function reduce(state, action) {
  switch (action?.type) {
    case 'set-mail':
      return { ...state, mail: action.mail };
    case 'set-password':
      return { ...state, password: action.password };
    case 'switch-rememberMe':
      return { ...state, rememberMe: !state.rememberMe };
    case 'start-logging':
      return { ...state, onLoggin: true, error: null };
    case 'stop-logging':
      return { ...state, onLoggin: false, error: action.error };
    default:
      throw new Error(`Unmanaged reducer action ${action.type}`);
  }
}

function LogIn({ attemptLogin }) {
  const [state, dispatch] = useReducer(reduce, null, createState);

  const submit = (e) => {
    e.preventDefault();
    if (!state.mail || !state.password) {
      console.warn('Cannot login. Missing data');
      return;
    }
    dispatch({ type: 'start-logging' });
    attemptLogin({
      mail: state.mail,
      password: state.password,
      rememberMe: state.rememberMe,
    }).catch((error) => {
      dispatch({ type: 'stop-logging', error });
    });
  };

  return (
    <>
      <Row>
        <Col>
          <Form onSubmit={submit}>
            <fieldset disabled={state.onLoggin}>
              <Form.Group className="mb-3" controlId="formLoginEmail">
                <Form.Label>Adresse mél</Form.Label>
                <Form.Control
                  type="email"
                  placeholder="Votre mél"
                  required
                  value={state.mail}
                  onChange={(e) => dispatch({ type: 'set-mail', mail: e.target.value })}
                />
                <Form.Text className="text-muted">
                  Nous ne partagerons jamais votre courriel.
                </Form.Text>
              </Form.Group>

              <Form.Group className="mb-3" controlId="formLoginPassword">
                <Form.Label>Mot de passe</Form.Label>
                <Form.Control
                  type="password"
                  placeholder="Mot de passe"
                  required
                  value={state.password}
                  onChange={(e) => dispatch({ type: 'set-password', password: e.target.value })}
                />
              </Form.Group>

              <Form.Group className="mb-3" controlId="formLoginRemeberMe">
                <Form.Check
                  type="checkbox"
                  label="Se souvenir de moi"
                  checked={state.rememberMe}
                  onChange={() => dispatch({ type: 'switch-rememberMe' })}
                />
              </Form.Group>

              <LoadingButton variant="primary" type="submit" loading={state.onLoggin}>
                Se connecter
              </LoadingButton>
            </fieldset>
          </Form>
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <LinkContainer to="/auth-mgmt/password-renewal">
            <Button variant="link" className="text-muted">Mot de passe oublié ?</Button>
          </LinkContainer>
        </Col>
      </Row>
      {
        state.error && (
          <Row className="mt-3">
            <Col>
              <Alert variant="danger">{state.error.message}</Alert>
            </Col>
          </Row>
        )
      }
    </>
  );
}

LogIn.propTypes = {
  attemptLogin: PropTypes.func.isRequired,
};

export default LogIn;
