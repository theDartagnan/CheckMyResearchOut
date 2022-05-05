/* eslint-disable max-len */
import React from 'react';

function About() {
  return (
    <>
      <h3>Mentions légales de la plateforme CheckMyResearchOut</h3>
      <p>
        Visa
        <ul>
          <li>Loi N°78-17 du 6 janvier 1978 modifiée relative à l&lsquo;informatique, aux fichiers et aux libertés</li>
          <li>Loi N°2004-575 du 21 juin 2004 pour la confiance dans l&lsquo;économie numérique</li>
          <li>Règlement (UE) 2016/679 du Parlement européen et du Conseil du 27 avril 2016 relatif à la protection des personnes physiques à l&lsquo;égard du traitement des données à caractère personnel et à la libre circulation de ces données</li>
        </ul>
      </p>
      <h5>Présentation de la plateforme</h5>
      <p>
        CheckMyResearchOut est une plateforme de jeu gratuite pour faciliter les échanges lors de présentations de travaux de recherche.
      </p>
      <h5>Auteur</h5>
      <p>
        Rémi Venant
        {' '}
        <a href="mailto://remi.venant@univ-lemans.fr">remi.venant@univ-lemans.fr</a>
      </p>
      <h5>Notice légale</h5>
      <p>
        <strong>Conditions générales d&lsquo;utilisation</strong>
        {' '}
        : l&lsquo;utilisation de la plateforme est subordonnées à l&lsquo;acceptation de l&lsquo;intégralité des présentes conditions exposées ci-après.
      </p>
      <p>
        <strong>Informatique, fichiers et liberté</strong>
        {' '}
        : Cette plateforme est à caractère éphèmère : l&lsquo;intégralité des données collectées dans le cadre d&lsquo;une conférence sera effacées à l&lsquo;issue de celle-ci.
        <br />
        Ces données ne sont au aucun cas transmise à une autre entité et n&lsquo;ont pour finalité que :
        <ul>
          <li>l&lsquo;authentification de l&lsquo;utilisateur</li>
          <li>la communication entre participant d&lsquo;information relative au jeu</li>
        </ul>
      </p>
      <p>
        <strong>Utilisation de cookies</strong>
        {' '}
        : Cette plateforme utilise des cookis d&authentification, nécessaires au bon fonctionnement de l&lsquo;application.
      </p>
    </>

  );
}

export default About;
