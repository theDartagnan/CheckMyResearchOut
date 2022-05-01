import React from 'react';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Table from 'react-bootstrap/Table';
import Loading from '../core/Loading';
import Identity from '../core/Identity';

function Ranking({ ranking }) {
  if (ranking === null) {
    return (
      <>
        <h2>Chargement du classement...</h2>
        <Loading />
      </>
    );
  }

  if (!ranking.publicRanking.length) {
    return (
      <h2>Aucun classement pour l&lsquo;instant</h2>
    );
  }

  return (
    <>
      <h1>Classement du quiz</h1>
      <Table striped bordered className="mt-3">
        <thead>
          <tr>
            <th>Rang</th>
            <th>Participant</th>
            <th>Nb questions réussies</th>
            <th>Nb total de tentatives</th>
            <th>Dernière tentative</th>
          </tr>
        </thead>
        <tbody>
          {
            ranking.publicRanking.map((rank) => {
              const cellStyle = classnames({
                'text-primary': rank.position === ranking.rank,
                'fw-bolder': rank.position === ranking.rank,
              });
              return (
                <tr key={rank.position}>
                  <td className={cellStyle}>{rank.position}</td>
                  <td className={cellStyle}>
                    <Identity firstname={rank.firstname} lastname={rank.lastname} />
                  </td>
                  <td className={cellStyle}>{rank.numSuccessfulAnswers ?? 0}</td>
                  <td className={cellStyle}>{rank.numAttempts ?? 0}</td>
                  <td className={cellStyle}>{rank.lastAttempt?.toLocaleString('fr-FR') ?? 'aucune'}</td>
                </tr>
              );
            })
          }
        </tbody>
      </Table>
    </>
  );
}

Ranking.propTypes = {
  ranking: PropTypes.shape({
    rank: PropTypes.number.isRequired,
    publicRanking: PropTypes.arrayOf(PropTypes.shape({
      position: PropTypes.number.isRequired,
      firstname: PropTypes.string.isRequired,
      lastname: PropTypes.string.isRequired,
      numSuccessfulAnswers: PropTypes.number.isRequired,
      numAttempts: PropTypes.number.isRequired,
      lastAttempt: PropTypes.instanceOf(Date).isRequired,
    })).isRequired,
  }),
};

Ranking.defaultProps = {
  ranking: null,
};

export default observer(Ranking);
