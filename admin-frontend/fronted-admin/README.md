# Admin Frontend

This project is a Vue 2 + Element UI based admin system, supporting user login, user management, order management, station management, and more. It features token-based authentication and a mock data development mode.

## Project Structure

```
├── public/                 # Public resources
│   └── index.html
├── src/
│   ├── assets/             # Static assets
│   ├── components/         # Reusable components
│   ├── router/             # Router configuration
│   ├── store/              # Vuex state management
│   ├── utiles/             # Utilities and request config
│   ├── views/              # Page views
│   └── main.js             # Entry file
├── mock-server.js          # Local mock data server
├── mock-data.js            # Mock data
├── package.json            # Project dependencies and scripts
├── vue.config.js           # Vue CLI config
└── README.md
```

## Features

- **User Login**: Email + password + captcha login, token is stored and used for subsequent requests.
- **User Management**: CRUD operations for user information.
- **Order Management**: Manage and display order information.
- **Station Management**: Station configuration and status monitoring.
- **Scooter Configuration**: Manage scooter-related data.
- **Feedback Management**: Collect and process user feedback.
- **Mock Data Development**: Local mock data support for frontend-backend separation.

## Getting Started

### 1. Install Dependencies

```bash
npm install
# or
yarn install
```

### 2. Start Development Server

```bash
npm run serve
# or
yarn serve
```
- Default address: http://localhost:8080
- Requests to `/api` are proxied to the backend server (default `http://192.168.17.147:8088`, change in `vue.config.js` if needed).

### 3. Use Mock Data for Development

If you want to use local mock data (no backend required):

```bash
npm run mock
# In another terminal window
npm run serve:mock
```
- Mock server runs at http://localhost:3000 by default.

### 4. Build for Production

```bash
npm run build
```

### 5. Lint and Fix Code

```bash
npm run lint
```

## Authentication & Security

- On successful login, the backend returns:
  ```json
  {
    "statusCode": 200,
    "message": "success",
    "data": {
      "user": { ... },
      "token": "xxx"
    }
  }
  ```
- The frontend automatically stores `user` and `token` in `localStorage`. All subsequent requests include the `Authorization: Bearer <token>` header.

## Main Dependencies

- [Vue 2.x](https://vuejs.org/)
- [Element UI](https://element.eleme.io/)
- [Vue Router](https://router.vuejs.org/)
- [Vuex](https://vuex.vuejs.org/)
- [Axios](https://axios-http.com/)

## Additional Notes

- To change the backend API address, edit the `proxy` config in `vue.config.js`.
- To extend mock data, edit `mock-data.js` and `mock-server.js`.
- Recommended editor: VS Code with the Vetur extension for a better Vue experience.

## FAQ

- **Port already in use**: Change the port in `vue.config.js`.
- **Dependency install fails**: Try deleting `node_modules` and `package-lock.json`, then reinstall.
- **ESLint errors**: Fix code style issues as prompted, or adjust rules in `.eslintrc.js`.
