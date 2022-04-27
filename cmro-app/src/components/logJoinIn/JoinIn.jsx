import React, { useReducer } from 'react';
import classnames from 'classnames';
// import { observer } from 'mobx-react';
import PropTypes from 'prop-types';
import Form from 'react-bootstrap/Form';
import Alert from 'react-bootstrap/Alert';
import LoadingButton from '../core/LoadingButton';

function createState() {
  return {
    mail: '',
    firstname: '',
    lastname: '',
    password: '',
    passwordCopy: '',
    onCreating: false,
    error: null,
  };
}

function reduce(state, action) {
  switch (action?.type) {
    case 'set-mail':
      return { ...state, mail: action.mail };
    case 'set-firstname':
      return { ...state, firstname: action.firstname };
    case 'set-lastname':
      return { ...state, lastname: action.lastname };
    case 'set-password':
      return { ...state, password: action.password };
    case 'set-passwordCopy':
      return { ...state, passwordCopy: action.passwordCopy };
    case 'start-creating':
      return { ...state, onCreating: true, error: null };
    case 'stop-creating':
      return { ...state, onCreating: false, error: action.error };
    default:
      throw new Error(`Unmanaged reducer action ${action.type}`);
  }
}

function JoinIn({ attemptCreate }) {
  const [state, dispatch] = useReducer(reduce, null, createState);

  const submit = (e) => {
    e.preventDefault();
    if (!state.mail || !state.password || !state.passwordCopy || !state.firstname
      || !state.lastname) {
      console.warn('Cannot create account. Missing data');
      return;
    }
    if (state.password !== state.passwordCopy) {
      console.warn('Passwords do not match.');
      return;
    }
    dispatch({ type: 'start-creating' });
    attemptCreate({
      mail: state.mail,
      password: state.password,
      firstname: state.firstname,
      lastname: state.lastname,
    }).catch((error) => {
      dispatch({ type: 'stop-creating', error });
    });
  };

  const invalidPasswords = state.password && state.passwordCopy
  && state.password !== state.passwordCopy;

  return (
    <>
      <Form onSubmit={submit}>
        <fieldset disabled={state.onCreating}>
          <Form.Group className="mb-3" controlId="formCreatingEmail">
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
          <Form.Group className="mb-3" controlId="formCreatingFirstname">
            <Form.Label>Prénom</Form.Label>
            <Form.Control
              type="text"
              placeholder="Votre prénom"
              required
              value={state.firstname}
              onChange={(e) => dispatch({ type: 'set-firstname', firstname: e.target.value })}
            />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formCreatingLastname">
            <Form.Label>Nom</Form.Label>
            <Form.Control
              type="text"
              placeholder="Votre nom"
              required
              value={state.lastname}
              onChange={(e) => dispatch({ type: 'set-lastname', lastname: e.target.value })}
            />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formCreatingPassword">
            <Form.Label>Mot de passe</Form.Label>
            <Form.Control
              type="password"
              placeholder="Votre mot de passe"
              required
              isInvalid={invalidPasswords}
              value={state.password}
              onChange={(e) => dispatch({ type: 'set-password', password: e.target.value })}
            />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formCreatingPasswordCopy">
            <Form.Label>Mot de passe (vérification)</Form.Label>
            <Form.Control
              type="password"
              placeholder="Votre mot de passe"
              required
              isInvalid={invalidPasswords}
              value={state.passwordCopy}
              onChange={(e) => dispatch({ type: 'set-passwordCopy', passwordCopy: e.target.value })}
            />
            {
              invalidPasswords && (
                <Form.Text className={classnames('fw-bold', 'text-danger')}>
                  Les mots de passe ne correspondent pas.
                </Form.Text>
              )
            }

          </Form.Group>

          <LoadingButton variant="primary" type="submit" loading={state.onCreating}>
            Créer son compte
          </LoadingButton>
        </fieldset>
      </Form>
      {
        state.error && (
          <Alert className="mt-3" variant="danger">{state.error.message}</Alert>
        )
      }
    </>

  );
}

JoinIn.propTypes = {
  attemptCreate: PropTypes.func.isRequired,
};

export default JoinIn;
