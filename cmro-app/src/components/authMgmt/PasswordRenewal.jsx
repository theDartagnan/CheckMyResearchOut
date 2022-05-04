import React, { useReducer, useContext } from 'react';
import { observer } from 'mobx-react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';
import Alert from 'react-bootstrap/Alert';
import LoadingButton from '../core/LoadingButton';
import RootStore from '../../RootStore';

function createState() {
  return {
    mail: '',
    onProcessing: false,
    processed: false,
    error: null,
  };
}

function reduce(state, action) {
  switch (action?.type) {
    case 'set-mail':
      return { ...state, mail: action.mail };
    case 'start-processing':
      return {
        ...state, onProcessing: true, processed: false, error: null,
      };
    case 'stop-processing':
      return {
        ...state, onProcessing: false, processed: true, error: action.error,
      };
    default:
      throw new Error(`Unmanaged reducer action ${action.type}`);
  }
}

function PasswordRenewal() {
  const [state, dispatch] = useReducer(reduce, null, createState);
  const { globalModelHdlr } = useContext(RootStore);

  const startProcessing = (e) => {
    e.preventDefault();
    if (state.onProcessing) {
      console.warn('Already in account password renewal processing');
      return;
    }
    if (!state.mail) {
      console.warn('No mail given');
      return;
    }
    // TODO start process
    dispatch({ type: 'start-processing' });
    globalModelHdlr.sendPasswordRenewalMail({ mail: state.mail }).then(() => {
      dispatch({ type: 'stop-processing' });
    }, (error) => {
      dispatch({ type: 'stop-processing', error });
    });
  };

  return (
    <>
      <Row className="justify-content-lg-center">
        <Col lg={10} xl={6}>
          <h3>Vous avez oublié votre mot de passe ?</h3>
          <p>
            Vous pouvez demander à recevoir un courriel de changement de mot de passe en&nbsp;
            renseignant votre adresse mél utilisée pour votre compte ici.
          </p>
          <Form onSubmit={startProcessing} className="mt-3">
            <fieldset disabled={state.onProcessing}>
              <Row>
                <Col>
                  <InputGroup>
                    <InputGroup.Text id="formPasswordRenewal">@</InputGroup.Text>
                    <FormControl
                      type="email"
                      placeholder="Votre mél"
                      aria-label="Votre mél"
                      aria-describedby="formPasswordRenewal"
                      required
                      value={state.mail}
                      onChange={(e) => dispatch({ type: 'set-mail', mail: e.target.value })}
                    />
                  </InputGroup>
                </Col>
                <Col>
                  <LoadingButton variant="primary" type="submit" loading={state.onProcessing}>
                    Envoyer un mél
                  </LoadingButton>
                </Col>
              </Row>
            </fieldset>
          </Form>
        </Col>
      </Row>
      {
        state.processed && (
          <Row className="justify-content-lg-center mt-3">
            <Col lg={8} xl={6}>
              {
                state.error ? (
                  <Alert variant="danger">{state.error.message}</Alert>
                ) : (
                  <Alert variant="success">Un courriel vous a bien été envoyé à votre adresse.</Alert>
                )
              }
            </Col>
          </Row>
        )
      }
    </>
  );
}

export default observer(PasswordRenewal);
