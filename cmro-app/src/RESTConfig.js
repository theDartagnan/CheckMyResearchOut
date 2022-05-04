import axios from 'axios';

const slashMatcher = /\/$/g;
export const ROOT_URL = `${APP_ENV.API_BASE_URL.replace(slashMatcher, '')}`;

// export const TIME_BEFORE_REFRESH_MS = 10 * 1000;

export const ROOT_AX = axios.create({
  withCredentials: true,
  timeout: 90000,
});

/**
 * [configureNetworkErrorHandler description]
 * @param  {[type]} handler               (error, details) -> void
 * @return {[type]}         [description]
 */
export function configureNetworkErrorHandler(handler) {
  ROOT_AX.interceptors.response.use((response) => response, (error) => {
    if (error.config?.silentOnError) {
      return Promise.reject(error);
    }
    let title = null;
    let details = null;
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      title = error.response.data?.error ?? 'Erreur';
      details = error.response.data?.message ?? 'Pas de détail';
    } else if (error.request) {
      // The request was made but no response was received
      // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
      // http.ClientRequest in node.js
      title = 'Pas de réponse';
      details = error.message ?? 'Pas de détail';
    } else {
      // Something happened in setting up the request that triggered an Error
      title = 'Erreur réseau interne';
      details = error.message ?? 'Pas de détail';
    }
    handler(title, details);
    return Promise.reject(error);
  });
}

ROOT_AX.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
