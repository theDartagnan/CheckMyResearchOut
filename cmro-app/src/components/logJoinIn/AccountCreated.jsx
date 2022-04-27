import React from 'react';
import Alert from 'react-bootstrap/Alert';
// import RootStore from '../../RootStore';

function AccountCreated() {
  return (
    <Alert variant="success">
      <Alert.Heading>Compte créé</Alert.Heading>
      <p>
        Votre compte a bien été créé. Vous devriez recevoir un courriel
        d&lsquo;ici peu pour le valider.
      </p>
      <hr />
      <p className="mb-0">
        Je n&lsquo;ai pas reçu de courriel ?
      </p>
    </Alert>
  );
}

export default AccountCreated;
