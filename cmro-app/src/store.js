import GlobalModelHandler from './model/GlobalModelHandler';
import ErrorManager from './model/ErrorManager';

const STORE = {
  globalModelHdlr: new GlobalModelHandler(),
  errorManager: new ErrorManager(),
};

export default STORE;
