import React from 'react';
// import PropTypes from 'prop-types';
// import classNames from 'classnames';
import RootStore from './RootStore';
import STORE from './store';

import './App.scss';

function App() {
  return (
    <RootStore.Provider value={STORE}>
      <main>
        <h1>Hello World</h1>
      </main>
    </RootStore.Provider>

  );
}

export default App;
