import React from 'react';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Table from 'react-bootstrap/Table';
import Alert from 'react-bootstrap/Alert';
import Loading from '../core/Loading';

function getProgressionColor(progression) {
  if (!progression && progression !== 0) {
    return classnames();
  }
  if (progression < 50) {
    return classnames('text-danger');
  }
  if (progression < 75) {
    return classnames('text-warning');
  }
  if (progression !== 100) {
    return classnames('text-success');
  }
  return classnames('text-success', 'fw-bolder');
}

function getRankColor(rank) {
  if (rank === 1) {
    return classnames('text-success', 'fw-bolder');
  }
  return classnames();
}

function PersonalStats({ ranking }) {
  if (ranking === null) {
    return (
      <>
        <h2>Chargement des statistiques...</h2>
        <Loading />
      </>
    );
  }
  if (!ranking.hasStats) {
    return (
      <h2>Vous n&lsquo;avez aucune statistique pour ce quiz.</h2>
    );
  }
  let alertText = null;
  if (ranking.progression >= 100.0) {
    if (ranking.rank === 1) {
      alertText = 'Félicitations, vous avez fini le quiz et être premier au classement !';
    } else {
      alertText = 'Bravo, vous avez fini le quiz !';
    }
  } else if (ranking.rank === 1) {
    alertText = "Bravo, vous êtes pour l'instant premier au classement !";
  }
  return (
    <>
      <h1>Mes statistiques du quiz</h1>
      <Table striped bordered className="mt-3">
        <tbody>
          <tr>
            <td>Accomplissement de quiz</td>
            <td className={getProgressionColor(ranking.progression)}>
              {ranking.progression?.toFixed() ?? 0}
              %
            </td>
          </tr>
          <tr>
            <td>Position au classement général</td>
            <td className={getRankColor(ranking.rank)}>{ranking.rank > 0 ? ranking.rank : 'classement inconnu'}</td>
          </tr>
          <tr>
            <td>Nombre de questions répondues avec succès</td>
            <td>{ranking.numSuccessfulAnswers ?? 0}</td>
          </tr>
          <tr>
            <td>Nombre total de tentatives</td>
            <td>{ranking.numAttempts ?? 0}</td>
          </tr>
          <tr>
            <td>Dernière tentative</td>
            <td>{ranking.lastAttempt?.toLocaleString('fr-FR') ?? 'aucune'}</td>
          </tr>
        </tbody>
      </Table>
      {
        alertText && (
          <Alert variant="success" className="mt-3">
            <Alert.Heading>{alertText}</Alert.Heading>
          </Alert>
        )
      }
    </>
  );
}

PersonalStats.propTypes = {
  ranking: PropTypes.shape({
    rank: PropTypes.number.isRequired,
    hasStats: PropTypes.bool.isRequired,
    progression: PropTypes.number,
    numSuccessfulAnswers: PropTypes.number,
    numAttempts: PropTypes.number,
    lastAttempt: PropTypes.instanceOf(Date),
  }),
};

PersonalStats.defaultProps = {
  ranking: null,
};

export default observer(PersonalStats);
