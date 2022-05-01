import React, { useReducer, useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import classnames from 'classnames';
import { useSearchParams } from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Alert from 'react-bootstrap/Alert';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Loading from '../core/Loading';
import LoadingButton from '../core/LoadingButton';
import RootStore from '../../RootStore';

const WRONG_URL = -1;
const CHECKING_ACCOUNT = 1;
const WRONG_ACCOUNT = -2;
const READY_TO_RENEW = 3;
const RENEW_PASS_ACCOUNT = 4;
const RENEW_PASS_FAILED = -4;
const PASSWORD_RENEWED = 5;

function getText(step) {
  switch (step) {
    case WRONG_URL:
      return 'Url de renouvellement de mot de passe invalide.';
    case CHECKING_ACCOUNT:
      return 'Vérification du compte...';
    case WRONG_ACCOUNT:
      return 'Vous ne pouvez pas modifier le mot de passe de ce compte.';
    case READY_TO_RENEW:
      return 'Veuillez fournir votre nouveau mot de passe.';
    case RENEW_PASS_ACCOUNT:
      return 'Changement du mot de passe...';
    case RENEW_PASS_FAILED:
      return 'Le changement de mot de passe a échoué. Soit le jeton est incorrecte, soit il est périmé.';
    case PASSWORD_RENEWED:
      return 'Votre mot de passe a bien été renouvelé. vous pouvez maintenant vous authentifier.';
    default:
      throw new Error(`Unknown step ${step}`);
  }
}

function createState({ encodedMail, token }) {
  return {
    encodedMail,
    token,
    step: encodedMail === null || token === null ? WRONG_URL : CHECKING_ACCOUNT,
    password: '',
    passwordCopy: '',
    error: null,
  };
}

function reduce(state, action) {
  switch (action?.type) {
    case WRONG_ACCOUNT:
      return { ...state, step: WRONG_ACCOUNT, error: action.error };
    case READY_TO_RENEW:
      return { ...state, step: READY_TO_RENEW };
    case RENEW_PASS_ACCOUNT:
      return { ...state, step: RENEW_PASS_ACCOUNT };
    case RENEW_PASS_FAILED:
      return { ...state, step: RENEW_PASS_FAILED, error: action.error };
    case PASSWORD_RENEWED:
      return { ...state, step: PASSWORD_RENEWED };
    case 'set-password': {
      if (state.step !== READY_TO_RENEW) {
        throw new Error('set-password action cannot be achieved outside step READY_TO_RENEW');
      }
      return { ...state, password: action.password };
    }
    case 'set-passwordCopy': {
      if (state.step !== READY_TO_RENEW) {
        throw new Error('set-passwordCopy action cannot be achieved outside step READY_TO_RENEW');
      }
      return { ...state, passwordCopy: action.passwordCopy };
    }
    default:
      throw new Error(`Unmanaged reducer action ${action.type}`);
  }
}

function PasswordRenew() {
  const [searchParams] = useSearchParams();
  const { globalModelHdlr } = useContext(RootStore);
  const [state, dispatch] = useReducer(reduce, {
    encodedMail: searchParams.get('m'),
    token: searchParams.get('t'),
  }, createState);

  useEffect(() => {
    if (state.step === CHECKING_ACCOUNT) {
      // check. Ok : step= 2 Error: step = -2
      globalModelHdlr.checkPasswordRenewal({ encodedMail: state.encodedMail })
        .then((success) => {
          if (success) {
            dispatch({ type: READY_TO_RENEW });
          } else {
            dispatch({ type: WRONG_ACCOUNT, error: { message: "Ce compte n'existe pas ou n'a pas de renouvellement de mot de passe en cours." } });
          }
        }, (error) => {
          dispatch({ type: WRONG_ACCOUNT, error });
        });
    } else if (state.step === RENEW_PASS_ACCOUNT) {
      // validate. Ok : step = 3. Error: step -3
      globalModelHdlr.renewPassword({
        encodedMail: state.encodedMail,
        token: state.token,
        password: state.password,
      })
        .then(() => {
          dispatch({ type: PASSWORD_RENEWED });
        }, (error) => {
          dispatch({ type: RENEW_PASS_FAILED, error });
        });
    }
  }, [state.step, state.encodedMail, state.token, state.password, globalModelHdlr]);

  const submitPassword = (e) => {
    e.preventDefault();
    if (!state.password || !state.passwordCopy) {
      console.warn('Cannot change password. Missing data');
      return;
    }
    if (state.password !== state.passwordCopy) {
      console.warn('Passwords do not match.');
      return;
    }
    dispatch({ type: RENEW_PASS_ACCOUNT });
  };

  const text = getText(state.step);
  const alertVariant = state.step === CHECKING_ACCOUNT || state.step === READY_TO_RENEW
    || state.step === RENEW_PASS_ACCOUNT || state.step === PASSWORD_RENEWED ? 'success' : 'danger';
  const showLoading = !state.error
    && (state.step === CHECKING_ACCOUNT || state.step === RENEW_PASS_ACCOUNT);
  const showForm = state.step === READY_TO_RENEW || state.step === RENEW_PASS_ACCOUNT
    || state.step === RENEW_PASS_FAILED;
  const showPasswordRenewResendUrl = state.step === WRONG_ACCOUNT
  || state.step === RENEW_PASS_FAILED;
  const showConnectLink = state.step === PASSWORD_RENEWED;
  const invalidPasswords = state.password && state.passwordCopy
    && state.password !== state.passwordCopy;

  return (
    <Row className="justify-content-md-center">
      <Col md={10} lg={8} xl={6}>
        <Alert variant={alertVariant}>
          <Alert.Heading>{text}</Alert.Heading>
          {
            showLoading && (<Loading />)
          }
          {
            state.error && (<p>{state.error.message}</p>)
          }
          {
            showForm && (
              <>
                <hr />
                <Form onSubmit={submitPassword}>
                  <fieldset disabled={state.step === RENEW_PASS_ACCOUNT}>
                    <Form.Group className="mb-3" controlId="formChangingPassword">
                      <Form.Label>Nouveau mot de passe</Form.Label>
                      <Form.Control
                        type="password"
                        placeholder="Votre nouveau mot de passe"
                        required
                        isInvalid={invalidPasswords}
                        value={state.password}
                        onChange={(e) => dispatch({ type: 'set-password', password: e.target.value })}
                      />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="formCChangingPasswordCopy">
                      <Form.Label>Nouveau mot de passe (vérification)</Form.Label>
                      <Form.Control
                        type="password"
                        placeholder="Votre nouveau mot de passe"
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
                    <LoadingButton
                      variant="primary"
                      type="submit"
                      loading={state.step === RENEW_PASS_ACCOUNT}
                    >
                      Modifier le mot de passe
                    </LoadingButton>
                  </fieldset>
                </Form>
              </>
            )
          }
          {
            showPasswordRenewResendUrl && (
              <>
                <hr />
                <p className="mb-0">
                  <LinkContainer to="/auth-mgmt/password-renewal">
                    <Button variant="link" className="ms-auto">Essayez de générer de nouveau un lien de changement de mot de passe.</Button>
                  </LinkContainer>
                </p>
              </>
            )
          }
          {
            showConnectLink && (
              <>
                <hr />
                <p className="mb-0">
                  <LinkContainer to="/auth/login">
                    <Button variant="link" className="ms-auto">Se connecter.</Button>
                  </LinkContainer>
                </p>
              </>
            )
          }
        </Alert>
      </Col>
    </Row>
  );
}

export default observer(PasswordRenew);
