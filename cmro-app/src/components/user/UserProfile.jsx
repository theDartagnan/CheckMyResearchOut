import React, { useReducer, useContext, useEffect } from 'react';
import classnames from 'classnames';
import { observer } from 'mobx-react';
import { useNavigate } from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import ToggleButton from 'react-bootstrap/ToggleButton';
import Alert from 'react-bootstrap/Alert';
import LoadingButton from '../core/LoadingButton';
import Loading from '../core/Loading';
import RootStore from '../../RootStore';

function createState(user) {
  return {
    firstname: user ? user.firstname : '',
    lastname: user ? user.lastname : '',
    editPassword: false,
    password: '',
    passwordCopy: '',
    updating: false,
    error: null,
  };
}

function reduce(state, action) {
  switch (action?.type) {
    case 'set-firstname':
      return { ...state, firstname: action.firstname };
    case 'set-lastname':
      return { ...state, lastname: action.lastname };
    case 'switch-edit-password':
      return { ...state, editPassword: !state.editPassword };
    case 'set-password':
      return { ...state, password: action.password };
    case 'set-passwordCopy':
      return { ...state, passwordCopy: action.passwordCopy };
    case 'start-updating':
      return { ...state, updating: true, error: null };
    case 'stop-updating':
      return { ...state, updating: false, error: action.error };
    case 'reset':
      return createState(action.user);
    default:
      throw new Error(`Unmanaged reducer action ${action.type}`);
  }
}

function UserProfile() {
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);
  const [state, dispatch] = useReducer(reduce, null, createState);

  useEffect(() => {
    globalModelHdlr.attemptAutoLogin(false).then(() => {
      if (!globalModelHdlr.loggedUser.isReady) {
        navigate('/auth/login');
      }
    }, (error) => {
      console.warn(`UserProfile: attempt login failed: ${error.message}`);
      navigate('/auth/login');
    });
    if (globalModelHdlr.loggedUser.isReady) {
      dispatch({ type: 'reset', user: globalModelHdlr.loggedUser });
    }
  }, [globalModelHdlr, navigate, dispatch, globalModelHdlr.loggedUser,
    globalModelHdlr.loggedUser.isReady]);

  const submitUpdate = (e) => {
    e.preventDefault();
    const updateObj = {};
    if (state.firstname) {
      updateObj.firstname = state.firstname;
    }
    if (state.lastname) {
      updateObj.lastname = state.lastname;
    }
    if (state.editPassword && state.password) {
      if (state.password !== state.passwordCopy) {
        console.warn('Passwords do not match.');
        return;
      }
      updateObj.password = state.password;
    }
    dispatch({ type: 'start-updating' });
    globalModelHdlr.loggedUser.update(updateObj).then(() => {
      dispatch({ type: 'stop-updating' });
    }, (error) => {
      dispatch({ type: 'stop-updating', error });
    });
  };

  const invalidPasswords = state.editPassword && state.password && state.passwordCopy
  && state.password !== state.passwordCopy;

  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        {
          globalModelHdlr.loggedUser.isReady ? (
            <>
              <h1>Mon profil</h1>
              <Form onSubmit={submitUpdate}>
                <fieldset disabled={state.updating}>
                  <Form.Group className="mb-3" controlId="formUpdatingFirstname">
                    <Form.Label>Prénom</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Votre prénom"
                      required
                      value={state.firstname}
                      onChange={(e) => dispatch({ type: 'set-firstname', firstname: e.target.value })}
                    />
                  </Form.Group>
                  <Form.Group className="mb-3" controlId="formUpdatingLastname">
                    <Form.Label>Nom</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Votre nom"
                      required
                      value={state.lastname}
                      onChange={(e) => dispatch({ type: 'set-lastname', lastname: e.target.value })}
                    />
                  </Form.Group>
                  <ToggleButton
                    id="formUpdateingPasswordEdit"
                    value={state.editPassword}
                    onClick={() => dispatch({ type: 'switch-edit-password' })}
                    variant="warning"
                    className="my-3"
                  >
                    Modifier le mot de passe
                  </ToggleButton>
                  <br />
                  {
                    state.editPassword && (
                      <>
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
                      </>
                    )
                  }
                  <LoadingButton variant="primary" type="submit" loading={state.updating}>
                    Modifier
                  </LoadingButton>
                </fieldset>
              </Form>
              {
                state.error && (
                  <Alert className="mt-3" variant="danger">{state.error.message}</Alert>
                )
              }
            </>
          ) : (
            <>
              <h1>Chargement de l&lsquo;Utilisateur</h1>
              <Loading />
            </>
          )
        }
      </Col>
    </Row>
  );
}

export default observer(UserProfile);
