# NPM Scripts for School Management System

This project provides convenient npm scripts for running the school management system in different modes.

## 🚀 Quick Start

### Development Mode (Hot Reload)
```bash
npm run dev
```
- Starts the entire system in development mode
- Frontend: http://localhost:3000 (Vite dev server)
- Backend: http://localhost:8080 (Spring Boot with DevTools)
- Database: localhost:3306
- **Hot reload enabled** - changes appear instantly!

### Production Mode
```bash
npm run prod
```
- Starts the entire system in production mode
- Frontend: http://localhost:80 (Nginx)
- Backend: http://localhost:8080 (Spring Boot)
- Database: localhost:3306
- Optimized builds

## 📋 Available Scripts

### Main Commands
| Script | Description |
|--------|-------------|
| `npm run dev` | Start development environment with hot reload |
| `npm run prod` | Start production environment |
| `npm run dev:stop` | Stop development environment |
| `npm run prod:stop` | Stop production environment |

### Logging
| Script | Description |
|--------|-------------|
| `npm run dev:logs` | View development logs (follow mode) |
| `npm run prod:logs` | View production logs (follow mode) |

### Building
| Script | Description |
|--------|-------------|
| `npm run build` | Build production containers |
| `npm run build:dev` | Build development containers |

### Individual Services
| Script | Description |
|--------|-------------|
| `npm run dev:frontend` | Run only frontend in development mode |
| `npm run dev:backend` | Run only backend in development mode |

### Cleanup
| Script | Description |
|--------|-------------|
| `npm run clean` | Stop and remove all containers and volumes |
| `npm run clean:dev` | Stop and remove development containers and volumes |

## 🔧 Development Workflow

1. **Start Development:**
   ```bash
   npm run dev
   ```

2. **Make Changes:**
   - Edit frontend files → changes appear instantly
   - Edit backend files → Spring Boot auto-restarts
   - No rebuilding needed!

3. **View Logs:**
   ```bash
   npm run dev:logs
   ```

4. **Stop Development:**
   ```bash
   npm run dev:stop
   ```

## 🌐 Access URLs

### Development Mode
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:3306

### Production Mode
- **Frontend**: http://localhost:80
- **Backend API**: http://localhost:8080
- **Database**: localhost:3306

## 🔑 Login Credentials
- **Username**: `admin`
- **Password**: `password`

## 💡 Tips

- Use `npm run dev` for daily development
- Use `npm run prod` for testing production builds
- Use `npm run dev:logs` to monitor all services
- Use `npm run clean` to completely reset the environment

## 🐛 Troubleshooting

If you encounter issues:

1. **Clean everything:**
   ```bash
   npm run clean
   ```

2. **Rebuild containers:**
   ```bash
   npm run build:dev
   ```

3. **Restart development:**
   ```bash
   npm run dev
   ```

4. **Check logs:**
   ```bash
   npm run dev:logs
   ```
