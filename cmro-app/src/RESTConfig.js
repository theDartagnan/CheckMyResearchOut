import axios from 'axios';

const slashMatcher = /\/$/g;
export const ROOT_URL = `${APP_ENV.API_BASE_URL.replace(slashMatcher, '')}/rest`;

//export const TIME_BEFORE_REFRESH_MS = 10 * 1000;

export const ROOT_AX = axios.create({
  withCredentials: true,
  timeout: 90000,
});

ROOT_AX.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
