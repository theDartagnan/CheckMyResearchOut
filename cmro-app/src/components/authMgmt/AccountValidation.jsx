import React, { useReducer, useContext, useEffect } from 'react';
import { observer } from 'mobx-react';
import { useSearchParams } from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Alert from 'react-bootstrap/Alert';
import { LinkContainer } from 'react-router-bootstrap';
import Button from 'react-bootstrap/Button';
import Loading from '../core/Loading';
import RootStore from '../../RootStore';

const WRONG_URL = -1;
const CHECKING_ACCOUNT = 1;
const WRONG_ACCOUNT = -2;
const VALIDATING_ACCOUNT = 2;
const VALIDATION_FAILED = -3;
const ACCOUNT_VALIDATED = 3;

function getText(step) {
  switch (step) {
    case WRONG_URL:
      return 'Url de validation invalide.';
    case CHECKING_ACCOUNT:
      return 'Vérification du compte...';
    case WRONG_ACCOUNT:
      return "Ce compte ne peut être validé en l'état.";
    case VALIDATING_ACCOUNT:
      return 'Validation du compte...';
    case VALIDATION_FAILED:
      return 'La validation a échoué. Soit le jeton est incorrecte, soit il est périmé.';
    case ACCOUNT_VALIDATED:
      return 'Votre compte a bien été validé. vous pouvez maintenant vous authentifier.';
    default:
      throw new Error(`Unknown step ${step}`);
  }
}

function createState({ encodedMail, token }) {
  return {
    encodedMail,
    token,
    step: encodedMail === null || token === null ? WRONG_URL : CHECKING_ACCOUNT,
    error: null,
  };
}

function reduce(state, action) {
  switch (action?.type) {
    case WRONG_ACCOUNT:
      return { ...state, step: WRONG_ACCOUNT, error: action.error };
    case VALIDATING_ACCOUNT:
      return { ...state, step: VALIDATING_ACCOUNT };
    case VALIDATION_FAILED:
      return { ...state, step: VALIDATION_FAILED, error: action.error };
    case ACCOUNT_VALIDATED:
      return { ...state, step: ACCOUNT_VALIDATED };
    default:
      throw new Error(`Unmanaged reducer action ${action.type}`);
  }
}

function AccountValidation() {
  const [searchParams] = useSearchParams();
  const { globalModelHdlr } = useContext(RootStore);
  const [state, dispatch] = useReducer(reduce, {
    encodedMail: searchParams.get('m'),
    token: searchParams.get('t'),
  }, createState);

  useEffect(() => {
    if (state.step === CHECKING_ACCOUNT) {
      // check. Ok : step= 2 Error: step = -2
      globalModelHdlr.checkAccountValidation({ encodedMail: state.encodedMail })
        .then((success) => {
          if (success) {
            dispatch({ type: VALIDATING_ACCOUNT });
          } else {
            dispatch({ type: WRONG_ACCOUNT, error: { message: "Ce compte n'existe pas ou n'a pas de validation en cours." } });
          }
        }, (error) => {
          dispatch({ type: WRONG_ACCOUNT, error });
        });
    } else if (state.step === VALIDATING_ACCOUNT) {
      // validate. Ok : step = 3. Error: step -3
      globalModelHdlr.validateAccount({ encodedMail: state.encodedMail, token: state.token })
        .then(() => {
          dispatch({ type: ACCOUNT_VALIDATED });
        }, (error) => {
          dispatch({ type: VALIDATION_FAILED, error });
        });
    }
  }, [state.step, state.encodedMail, state.token, globalModelHdlr, dispatch]);

  const text = getText(state.step);
  const alertVariant = state.step === CHECKING_ACCOUNT || state.step === VALIDATING_ACCOUNT
    || state.step === ACCOUNT_VALIDATED ? 'success' : 'danger';
  const showLoading = !state.error
    && (state.step === CHECKING_ACCOUNT || state.step === VALIDATING_ACCOUNT);
  const showValidationResendUrl = state.step === WRONG_ACCOUNT || state.step === VALIDATION_FAILED;
  const showConnectLink = state.step === ACCOUNT_VALIDATED;

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
            showValidationResendUrl && (
              <>
                <hr />
                <p className="mb-0">
                  <LinkContainer to="/auth-mgmt/revalidate">
                    <Button variant="link" className="ms-auto">Essayez de générer de nouveau un lien de validation.</Button>
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

export default observer(AccountValidation);
