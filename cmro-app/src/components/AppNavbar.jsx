import React, { useContext, useState } from 'react';
import classNames from 'classnames';
// import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import Dropdown from 'react-bootstrap/Dropdown';
// import NavDropdown from 'react-bootstrap/NavDropdown';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser } from '@fortawesome/free-solid-svg-icons';
import Identity from './core/Identity';

import RootStore from '../RootStore';

import style from './AppNavbar.scss';

import logoPict from '../assets/logo.png';

function AppNavbar() {
  const navigate = useNavigate();
  const { globalModelHdlr } = useContext(RootStore);
  const [expanded, setExpanded] = useState(false);

  const onToggleNavbar = (e) => {
    setExpanded(e);
  };

  const traceClick = (e) => {
    if (e.target?.nodeName === 'A' && expanded) {
      setExpanded(false);
    }
  };

  const logout = () => {
    if (!globalModelHdlr.loggedUser.isReady) {
      console.warn('User not ready, cannot logout.');
      return;
    }
    globalModelHdlr.loggedUser.logout().then(() => navigate('/auth/login'));
  };

  const isLogged = globalModelHdlr.loggedUser.isReady;
  const hasCurrentQuiz = globalModelHdlr.currentQuiz.isReady;

  const title = isLogged && hasCurrentQuiz
    ? globalModelHdlr.currentQuiz.fullName : APP_ENV.APP_TITLE;

  return (
    <Navbar
      expand="sm"
      fixed="top"
      bg="dark"
      variant="dark"
      className="py-1"
      expanded={expanded}
      onToggle={onToggleNavbar}
      onClick={traceClick}
    >
      <Navbar.Brand as={Link} to="/">
        <img
          src={logoPict}
          width="30"
          height="30"
          className="d-inline-block align-top"
          alt={`${APP_ENV.APP_TITLE} Logo`}
        />
        {' '}
        {title}
      </Navbar.Brand>
      {
        isLogged ? (
          <>
            <Navbar.Toggle aria-controls="ApplicationNavbar" />
            <Navbar.Collapse id="ApplicationNavbar">
              <Nav className="w-100">
                <Nav.Link as={Link} to="/quizzes">
                  Tous les quiz
                </Nav.Link>
                <Nav.Link as={Link} to="/about">
                  Mentions légales
                </Nav.Link>
                <Dropdown as={Nav.Item} className={classNames('ms-sm-auto', style.usermenu)}>
                  <Dropdown.Toggle as={Nav.Link}>
                    <FontAwesomeIcon icon={faUser} />
                    {' '}
                    <Identity
                      firstname={globalModelHdlr.loggedUser.firstname}
                      lastname={globalModelHdlr.loggedUser.lastname}
                    />
                  </Dropdown.Toggle>
                  <Dropdown.Menu>
                    <Dropdown.Item
                      as={Link}
                      to="/user/profile"
                      className="text-success"
                    >
                      Mon profil
                    </Dropdown.Item>
                    <Dropdown.Divider />
                    <Dropdown.Item onClick={logout}>Se déconnecter</Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
              </Nav>
            </Navbar.Collapse>
          </>
        ) : (
          <>
            <Navbar.Toggle aria-controls="ApplicationNavbar" />
            <Navbar.Collapse id="ApplicationNavbar">
              <Nav className="w-100">
                <Nav.Link as={Link} to="/auth/login" className="ms-sm-auto">
                  Se connecter | Créer son compte
                </Nav.Link>
                <Nav.Link as={Link} to="/about">
                  Mentions légales
                </Nav.Link>
              </Nav>
            </Navbar.Collapse>
          </>
        )
      }
    </Navbar>
  );
}
/* <NavDropdown title="UserMenuDropdown" id="basic-nav-dropdown">
  <NavDropdown.Item as={Link} to={`/my-profile/`} className="text-success">
    <FontAwesomeIcon icon={faUser} />
    {' '}
    <Identity
  firstname={globalModelHdlr.loggedUser.firstname}
  lastname={globalModelHdlr.loggedUser.lastname}
    />
  </NavDropdown.Item>
  <NavDropdown.Divider />
  <NavDropdown.Item onClick={logout}>Se déconnecter</NavDropdown.Item>
</NavDropdown> */
export default observer(AppNavbar);
