import React from 'react';
import { observer } from 'mobx-react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';

export function formatIdentity(firstname, lastname) {
  return `${firstname.toLowerCase().replace(/(?:^|\s|-)\w/g, (m) => m.toUpperCase())} ${lastname?.toUpperCase()}`.trim();
}

function Identity({
  firstname, lastname, href, raw, className,
}) {
  const content = formatIdentity(firstname, lastname);
  if (!content) {
    return raw ? <>Identité inconnue</> : <i className="text-warning">Identité inconnue</i>;
  }
  if (raw) {
    return { content };
  }
  return href
    ? <Link to={href} className={className}>{content}</Link>
    : <span className={className}>{content}</span>;
}

Identity.defaultProps = {
  firstname: null, lastname: null, href: null, raw: false, className: null,
};

Identity.propTypes = {
  firstname: PropTypes.string,
  lastname: PropTypes.string,
  href: PropTypes.string,
  raw: PropTypes.bool,
  className: PropTypes.string,
};

export default observer(Identity);
